package com.wheresapp.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import com.wheresapp.broadcastreceiver.ContactDataIntentReceiver;
import com.wheresapp.bussiness.contacts.factory.ASContactsFactory;
import com.wheresapp.model.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 13/12/2014.
 */
public class ContactListLoader extends AsyncTaskLoader<List<Contact>> {

    private List<Contact> mContacts;

    private Boolean favorito = false;
    private ContactDataIntentReceiver mChangeObserver;

    public ContactListLoader(Context context, Boolean favorito, Boolean reciente) {

        super(context);

        this.favorito = favorito;

    }

    @Override
    public List<Contact> loadInBackground() {

        List<Contact> listContact = new ArrayList<Contact>();

        if (favorito)
            listContact = ASContactsFactory.getInstance().getInstanceASContacts(getContext()).getFavouriteContactsList();
        else{
            listContact = ASContactsFactory.getInstance().getInstanceASContacts(getContext()).getContactList();
            Contact test = new Contact();
            test.setName("Test call");
            test.setServerid("0");
            listContact.add(0,test);
        }
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
            mChangeObserver = new ContactDataIntentReceiver(this);
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

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Contact> contacts) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }


}
