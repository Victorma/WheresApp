package com.wheresapp.integration.contacts.imp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.wheresapp.R;
import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOContactsImp implements DAOContacts {

    private static ContentResolver mContentResolver = null;
    private static String COLUMN_ID = ContactsContract.RawContacts.SYNC1;
    private static String COLUMN_NAME = ContactsContract.RawContacts.SYNC2;
    private static String COLUMN_IMAGE = ContactsContract.RawContacts.SYNC3;

    private static final String TAG = "DAOContacts";

    private Context context;
    private Account account;

    public DAOContactsImp(Context context){
        this.context = context;
        mContentResolver = context.getContentResolver();
        account = getAccount();
    }

    private Account getAccount() {
        if (account==null) {
            Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
            if (accounts.length > 0)
                account = accounts[0];
        }
        return account;
    }

    @Override
    public boolean create(Contact contact) {

        Log.i(TAG, "Adding contact: " + contact.getName());
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, account.name);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, account.type);
        builder.withValue(ContactsContract.RawContacts.SYNC1, contact.getServerid());
        builder.withValue(ContactsContract.RawContacts.SYNC2, 1); //Marca si es favorito
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName());
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.com.wheresapp.profile");
        builder.withValue(ContactsContract.Data.DATA1, contact.getTelephone());
        builder.withValue(ContactsContract.Data.DATA2, contact.getTelephone());
        operationList.add(builder.build());

        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        touchObserver();
        return true;
    }

    @Override
    public Contact read(Contact contact) {

        Contact contactFound = null;

        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                ContactsContract.RawContacts.ACCOUNT_TYPE, account.type).build();

        String[] myProjection = new String[]{
                BaseColumns._ID,
                ContactsContract.RawContacts.CONTACT_ID,
                ContactsContract.RawContacts.SYNC1,
                ContactsContract.RawContacts.SYNC2
        };

        String selection = ContactsContract.RawContacts.SYNC1 + " LIKE '" + contact.getServerid()  + "'";

        Cursor c1 = mContentResolver.query(rawContactUri, myProjection, selection, null, null);
        while (c1.moveToNext())
            contact = extractContactFromCursor(c1);
        c1.close();
        return contact;
    }

    @Override
    public boolean update(Contact contact) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        Uri rawContactUri = ContentUris.withAppendedId(ContactsContract.RawContacts.CONTENT_URI, contact.getRaw_Id());

        if (contact.getFavourite()!=null) {
            ops.add(ContentProviderOperation.newUpdate(rawContactUri).withValue(ContactsContract.RawContacts.SYNC2, contact.getFavouriteNum()).build());
            ContentProviderResult[] result = null;
            try {
                result = context.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                touchObserver();
                return true;
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    @Override
    public boolean delete(Contact contact) {
        return false;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }

    @Override
    public List<Contact> discover(Contact contact, int limit, int page) {
        getAccount();
        List<Contact> contacts = new ArrayList<Contact>();


        // TODO ENTENDER ESTO MUST DO!!!
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                ContactsContract.RawContacts.ACCOUNT_TYPE, account.type).build();

        // TODO HACER LA PROYECCION
        String[] myProjection = new String[]{
                BaseColumns._ID,
                ContactsContract.RawContacts.CONTACT_ID,
                ContactsContract.RawContacts.SYNC1,
                ContactsContract.RawContacts.SYNC2,
        };

        String selection = null;
        if (contact.getFavourite() != null)
            selection = ContactsContract.RawContacts.SYNC2 + "='" + contact.getFavouriteNum()  + "'";
        else if (contact.getServerid() != null)
            selection = ContactsContract.RawContacts.SYNC1 + " LIKE '" + contact.getServerid()  + "'";
        else
            selection = null;

        Cursor c1 = mContentResolver.query(rawContactUri, myProjection, selection, null, null);
        while (c1.moveToNext())
            contacts.add(extractContactFromCursor(c1));
        c1.close();

        Log.i(TAG, "size localContacts: " + contacts.size());

        return contacts;
    }

    private Contact extractContactFromCursor(Cursor c){
        Contact contact = new Contact();
        contact.setRaw_Id(c.getLong(0));
        contact.setServerid(c.getString(2));
        if (c.getInt(3)==1)
            contact.setFavourite(true);
        else
            contact.setFavourite(false);

        Uri contactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+ c.getLong(1);

        Cursor cContact = mContentResolver.query(contactUri, new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER}, selection, null, null);
        if (cContact.moveToFirst()) {
            contact.setName(cContact.getString(1));
            contact.setImageURI(cContact.getString(2));
            contact.setTelephone(cContact.getString(3));
        }
        cContact.close();
        return contact;
    }

    @Override
    public List<Contact> discover(Contact contact) {
        return discover(contact,-1,-1);
    }

    private void touchObserver() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(this.filterChange));
    }
}
