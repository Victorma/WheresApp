package com.wheresapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

import com.wheresapp.R;
import com.wheresapp.SignUpActivity;
import com.wheresapp.modelTEMP.Contact;
import com.wheresapp.server.ServerAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Sergio on 05/12/2014.
 */
public class SyncContacts {
    private String TAG = "SyncContacts";
    private Account account;
    private String authority;
    private Context context;
    private static ContentResolver mContentResolver = null;
    private static String COLUMN_ID = ContactsContract.RawContacts.SYNC1;
    private static String COLUMN_NAME = ContactsContract.RawContacts.SYNC2;
    private static String COLUMN_IMAGE = ContactsContract.RawContacts.SYNC3;
    private String myId;

    public SyncContacts(Context context) {
        this.context = context;
        Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
        account = accounts[0];
        authority = "com.android.contacts";
        myId = getUserId();
    }

    private static class SyncEntry {
        public Long raw_id = 0L;
        public Long user_id = null;
    }

    public void performSync() throws OperationCanceledException {
        HashMap<String, SyncEntry> localContacts = new HashMap<String, SyncEntry>();
        mContentResolver = context.getContentResolver();
        Log.i("SyncContacts", "performSync: " + account.toString());

        List<Contact> listTemp = new ArrayList<Contact>();

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                ContactsContract.CommonDataKinds.Phone.TYPE
        };


        String selection = "(" +ContactsContract.CommonDataKinds.Phone.TYPE +
                " = '2')";

        Cursor cursor = context.getContentResolver().query(uri,projection, selection, null, null);

        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setTelephone(cursor.getString(cursor.getColumnIndex(projection[3])));
                contact.setName(cursor.getString(cursor.getColumnIndex(projection[1])));
                contact.setImageURI(cursor.getString(cursor.getColumnIndex(projection[2])));
                listTemp.add(contact);
            } while (cursor.moveToNext());
        }
        Integer intento =0;
        List<Contact> contactResult = null;
        try {
            while(contactResult==null) {
                contactResult = ServerAPI.getInstance().getContactosRegistrados(myId, listTemp);
                intento++;
                if(intento>5)
                    break;
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        // Load the local contacts
        Uri rawContactUri = ContactsContract.RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(ContactsContract.RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                ContactsContract.RawContacts.ACCOUNT_TYPE, account.type).build();
        Cursor c1 = mContentResolver.query(rawContactUri, new String[] { BaseColumns._ID, COLUMN_ID  }, null, null, null);
        while (c1.moveToNext()) {
            SyncEntry entry = new SyncEntry();
            entry.raw_id = c1.getLong(c1.getColumnIndex(BaseColumns._ID));
            entry.user_id = c1.getLong(c1.getColumnIndex(COLUMN_ID));
            localContacts.put(c1.getString(c1.getColumnIndex(COLUMN_ID)), entry);
        }
        Log.i(TAG, "size localContacts: " + localContacts.size());
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();



        try {

            for (Contact c : contactResult) {
                if (!localContacts.containsKey(c.getServerid().toString())) {
                    addContact(account,c);
                }
            }
            if (operationList.size() > 0)
                mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void addContact(Account account, Contact contact) {
        Log.i(TAG, "Adding contact: " + contact.getName());
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, account.name);
        builder.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, account.type);
        builder.withValue(ContactsContract.RawContacts.SYNC1, contact.getId());
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
        builder.withValue(ContactsContract.Data.DATA1, contact.getId());
        builder.withValue(ContactsContract.Data.DATA2, contact.getTelephone());
        operationList.add(builder.build());

        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getUserId() {
        String userId = null;
        final SharedPreferences prefs = context.getSharedPreferences(
                SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        userId = prefs.getString(SignUpActivity.PROPERTY_USER_ID,"");
        if (userId=="") {
            Log.i(TAG, "Registration not found.");
            return null;
        }
        return userId;
    }
}
