package com.wheresapp.loader;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.wheresapp.R;
import com.wheresapp.broadcastreceiver.DataIntentReceiver;
import com.wheresapp.bussiness.contacts.factory.ASContactsFactory;
import com.wheresapp.integration.calls.DAOCalls;
import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.modelTEMP.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 13/12/2014.
 */
public class ContactListLoader extends AsyncTaskLoader<List<Contact>> {

    private List<Contact> mContacts;

    private Boolean favorito = false;
    private Boolean reciente = false;
    private DataIntentReceiver mChangeObserver;

    public ContactListLoader(Context context, Boolean favorito, Boolean reciente) {

        super(context);

        this.favorito = favorito;

        this.reciente = reciente;

    }

    @Override
    public List<Contact> loadInBackground() {

        List<Contact> listContact = new ArrayList<Contact>();

        if (favorito)
            listContact = ASContactsFactory.getInstance().getInstanceASContacts(getContext()).getFavouriteContactsList();
        else if (reciente)
            listContact = ASContactsFactory.getInstance().getInstanceASContacts(getContext()).getRecentContactList();
        else
            listContact = ASContactsFactory.getInstance().getInstanceASContacts(getContext()).getContactList();

        return listContact;
    }
    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<Contact> contacts) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (contacts != null) {
                onReleaseResources(contacts);
            }
        }
        List<Contact> oldApps = mContacts;
        mContacts = contacts;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(contacts);
        }

        // At this point we can release the resources associated with
        // 'oldApps' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldApps != null) {
            onReleaseResources(oldApps);
        }
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override protected void onStartLoading() {
        if (mContacts != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mContacts);
        }

        if (mChangeObserver==null) {
            mChangeObserver = new DataIntentReceiver(this);
        }

        if (mContacts == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    /**
     * Handles a request to cancel a load.
     */
    @Override public void onCanceled(List<Contact> contacts) {
        super.onCanceled(contacts);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(contacts);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mContacts != null) {
            onReleaseResources(mContacts);
            mContacts = null;
        }

        if (mChangeObserver!=null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mChangeObserver);
        }
    }

    public Boolean isLoaderOfRecent() {
        return reciente;
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Contact> contacts) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }


}
