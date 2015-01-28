package tk.wheresoft.wheresapp.bussiness.contacts.imp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import tk.wheresoft.wheresapp.ProjectID;

import tk.wheresoft.wheresapp.R;

import tk.wheresoft.wheresapp.bussiness.contacts.ASContacts;
import tk.wheresoft.wheresapp.integration.calls.DAOCalls;
import tk.wheresoft.wheresapp.integration.calls.factory.DAOCallsFactory;
import tk.wheresoft.wheresapp.integration.contacts.DAOContacts;
import tk.wheresoft.wheresapp.integration.contacts.factory.DAOContactsFactory;
import tk.wheresoft.wheresapp.model.Call;
import tk.wheresoft.wheresapp.model.Contact;
import tk.wheresoft.wheresapp.serverapi.ServerAPI;
import tk.wheresoft.wheresapp.sync.SyncContacts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASContactsImp implements ASContacts {

    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_USER_ID = "userId";
    private static final String TAG = "ASContacts";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_USER_NUMBER = "userNumber";
    private Context context;
    private Account account;

    public ASContactsImp(Context context) {
        this.context = context;
        Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
        if (accounts.length > 0)
            account = accounts[0];
    }

    @Override
    public boolean isRegistered() {
        return getUserRegistered() != null;
    }

    @Override
    public Contact getUserRegistered() {
        Contact user = new Contact();
        final SharedPreferences prefs = context.getSharedPreferences(
                "ASContactsPreferences", Context.MODE_PRIVATE);

        user.setServerid(prefs.getString(PROPERTY_USER_ID, ""));
        if (user.getServerid() == "") {
            Log.i(TAG, "Registration not found.");
            return null;
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return null;
        }
        user.setTelephone(prefs.getString(PROPERTY_USER_NUMBER, ""));
        if (user.getTelephone().isEmpty()) {
            Log.i(TAG, "Phone number not found.");
            return null;
        }
        user.setGcmId(prefs.getString(PROPERTY_REG_ID, ""));
        if (user.getGcmId().isEmpty()) {
            Log.i(TAG, "GCM id not found.");
            return null;
        }
        return user;
    }

    private int getAppVersion() {
        try {
            PackageInfo packageInfo;
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public Bundle register(String telephone) {
        Bundle r = new Bundle();
        String msg = null;
        try {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);
            String regId = gcm.register(ProjectID.SENDER_ID);
            Contact user = ServerAPI.getInstance(context).registrarUsuario(telephone, regId);
            storeRegistration(user);
            Bundle result = null;
            Account account = new Account(user.getTelephone(), context.getString(R.string.ACCOUNT_TYPE));
            AccountManager am = AccountManager.get(context);
            if (am.addAccountExplicitly(account, null, null)) {
                r.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                r.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
            }
            r.putSerializable("USER", user);

            Account[] accounts = am.getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
            context.getContentResolver().setIsSyncable(accounts[0], ContactsContract.AUTHORITY, 1);
            context.getContentResolver().setSyncAutomatically(accounts[0], ContactsContract.AUTHORITY, true);

        } catch (IOException ex) {
            msg = "Error :" + ex.getMessage();
            Log.d(TAG, "Error: " + msg);
        }
        if (msg != null) {
            r.putString("ERROR_MSG", msg);
        }

        return r;
    }

    private void storeRegistration(Contact user) {
        final SharedPreferences prefs = context.getSharedPreferences(
                "ASContactsPreferences", Context.MODE_PRIVATE);
        int appVersion = getAppVersion();
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, user.getGcmId());
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putString(PROPERTY_USER_NUMBER, user.getTelephone());
        editor.putString(PROPERTY_USER_ID, user.getServerid());
        editor.commit();
    }


    @Override
    public List<Contact> getRecentContactList() {

        DAOCalls daoCalls = DAOCallsFactory.getInstance().getInstanceDAOCalls(context);
        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts(context);

        List<Contact> contacts = new ArrayList<Contact>();
        boolean stop = false;
        int page = 0;
        while (!stop && contacts.size() < 15) {
            List<Call> calls = daoCalls.discover(new Call(), 20, page);
            if (calls == null || calls.size() == 0)
                stop = true;
            else {
                for (Call call : calls) {
                    Contact search = new Contact();
                    search.setServerid(call.getReceiver());
                    Contact c = daoContacts.read(search);
                    if (!contacts.contains(c))
                        contacts.add(c);
                }
                page++;
            }
        }

        return contacts;
    }

    @Override
    public List<Contact> getFavouriteContactsList() {

        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts(context);

        Contact pattern = new Contact();
        pattern.setFavourite(true);

        return daoContacts.discover(pattern);
    }

    @Override
    public List<Contact> getContactList() {
        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts(context);
        return daoContacts.discover(new Contact());
    }

    @Override
    public boolean updateContactList() {

        Contact user = getUserRegistered();

        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts(context);

        HashMap<String, Contact> localContacts = new HashMap<String, Contact>();
        Log.i("SyncContacts", "Iniciando sincronización: " + account.toString());

        List<Contact> listTemp = new ArrayList<Contact>();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };

        String selection = "(" + ContactsContract.CommonDataKinds.Phone.TYPE + " = '2')";


        // Cargamos los contactos actuales conocidos por la app

        List<Contact> currentContactList = daoContacts.discover(new Contact());
        Log.i("SyncContacts", "Encontrados : " + currentContactList.size());
        for (Contact c : currentContactList) {
            localContacts.put(c.getTelephone(), c);
            Log.i("SyncContacts", "Contacto: " + c.getName() + " :" + c.getServerid() + " : " + c.getTelephone());
        }

        // Leemos los contactos del móvil evitando aquellos ya conocidos

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, null, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setTelephone(cursor.getString(cursor.getColumnIndex(projection[3])));
                contact.setName(cursor.getString(cursor.getColumnIndex(projection[1])));
                contact.setImageURI(cursor.getString(cursor.getColumnIndex(projection[2])));
                if (!account.name.equals(contact.getTelephone()))
                    if (!localContacts.containsKey(contact.getTelephone()))
                        listTemp.add(contact);

            } while (cursor.moveToNext());
        }

        // Contrastamos los contactos desconocidos con la opinión del servidor

        Integer intento = 0;
        List<Contact> contactResult = null;
        try {
            while (contactResult == null && intento <= 5) {
                contactResult = ServerAPI.getInstance(context).getContactosRegistrados(listTemp);
                intento++;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        // Todos los nuevos, pa dentro a través del dao
        for (Contact c : contactResult) {
            if (!localContacts.containsKey(c.getTelephone())) {
                Log.i("SyncContacts", "Nuevo contacto: " + c.getName() + " :" + c.getServerid() + " : " + c.getTelephone());
                daoContacts.create(c);
            }
        }

        return true;
    }

    @Override
    public Contact getContact(Contact contact) {

        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts(context);
        Contact r = null;
        if (contact.getServerid() != null)
            r = daoContacts.read(contact);

        return r;
    }
}
