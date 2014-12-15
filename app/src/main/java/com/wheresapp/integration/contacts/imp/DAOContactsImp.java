package com.wheresapp.integration.contacts.imp;

import android.accounts.Account;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.modelTEMP.Contact;

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

    public DAOContactsImp(Context context, Account account){
        this.context = context;
        mContentResolver = context.getContentResolver();
    }

    @Override
    public boolean create(Contact contact) {

        Log.i(TAG, "Adding contact: " + contact.getName());
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, account.name);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, account.type);
        builder.withValue(ContactsContract.RawContacts.SYNC1, contact.getServerid());
        builder.withValue(ContactsContract.RawContacts.SYNC2, false);
        builder.withValue(ContactsContract.RawContacts.SYNC3, false);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, contact.getName());
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.com.wheresapp.profile");
        builder.withValue(ContactsContract.Data.DATA1, contact.getGcmId());
        builder.withValue(ContactsContract.Data.DATA2, contact.getTelephone());
        operationList.add(builder.build());

        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        return contact.save() != 0;
    }

    @Override
    public Contact read(Contact contact) {
        List<Contact> c = new Select()
                .from(Contact.class)
                .where("telephone LIKE '"+contact.getTelephone()+"'")
                .limit(1).execute();
        return (c.size() > 0)? c.get(0): null;
    }

    @Override
    public boolean update(Contact contact) {

        Contact c = this.read(contact);

        c.setName(contact.getName());
        c.setFavourite(contact.getFavourite());
        c.setLastSeen(contact.getLastSeen());
        c.setState(contact.getState());
        c.setNickname(contact.getNickname());

        Long id = c.save();

        return id != 0;
    }

    @Override
    public boolean delete(Contact contact) {
        boolean r = false;
        Contact c = this.read(contact);
        if(c!=null){
            c.delete();
            r = true;
        }
        return r;
    }

    @Override
    public List<Contact> discover(Contact contact, int limit, int page) {

        List<Contact> contacts = new ArrayList<Contact>();


        // TODO ENTENDER ESTO MUST DO!!!
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                ContactsContract.RawContacts.ACCOUNT_TYPE, account.type).build();

        // TODO HACER LA PROYECCION
        String[] myProjection = new String[]{
                BaseColumns._ID,
                ContactsContract.RawContacts.SYNC1
        };

        Cursor c1 = mContentResolver.query(rawContactUri, myProjection, null, null, null);
        while (c1.moveToNext())
            contacts.add(extractContactFromCursor(c1));

        Log.i(TAG, "size localContacts: " + contacts.size());

        return contacts;
    }

    private Contact extractContactFromCursor(Cursor c){

        //TODO Hacer la extracción

        /*
        SyncEntry entry = new SyncEntry();
        entry.raw_id = c1.getLong(0);
        entry.user_id = c1.getLong(1);
        localContacts.put(entry.user_id.toString(), entry);
        */

        return null;
    }

    @Override
    public List<Contact> discover(Contact contact) {
        return discover(contact,-1,-1);
    }
}
