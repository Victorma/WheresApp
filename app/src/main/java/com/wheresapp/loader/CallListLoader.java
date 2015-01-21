package com.wheresapp.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.LocalBroadcastManager;

import com.wheresapp.broadcastreceiver.CallDataIntentReceiver;
import com.wheresapp.broadcastreceiver.ContactDataIntentReceiver;
import com.wheresapp.bussiness.calls.factory.ASCallsFactory;
import com.wheresapp.integration.calls.factory.DAOCallsFactory;
import com.wheresapp.modelTEMP.Call;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 13/12/2014.
 */
public class CallListLoader extends AsyncTaskLoader<List<Call>> {

    private List<Call> mCalls;
    private String contactId = null;

    private CallDataIntentReceiver mChangeObserver;

    public CallListLoader(Context context) {
        super(context);
    }

    public CallListLoader(Context context, String contactId) {
        super(context);
        this.contactId = contactId;
    }

    @Override
    public List<Call> loadInBackground() {

        List<Call> listCall = new ArrayList<Call>();

        if (contactId==null)
            listCall = ASCallsFactory.getInstance().getInstanceASCalls(getContext()).getAllRecentCall();
        else
            listCall = ASCallsFactory.getInstance().getInstanceASCalls(getContext()).getRecentCallFromContact(contactId);

        return listCall;
    }
    /**
     * Called when there is new data to deliver to the client.  The
     * super class will take care of delivering it; the implementation
     * here just adds a little more logic.
     */
    @Override public void deliverResult(List<Call> calls) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (calls != null) {
                onReleaseResources(calls);
            }
        }
        List<Call> oldApps = mCalls;
        mCalls = calls;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(calls);
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
        if (mCalls != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mCalls);
        }

        if (mChangeObserver==null) {
            mChangeObserver = new CallDataIntentReceiver(this);
        }

        if (mCalls == null) {
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
    @Override public void onCanceled(List<Call> calls) {
        super.onCanceled(calls);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(calls);
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
        if (mCalls != null) {
            onReleaseResources(mCalls);
            mCalls = null;
        }

        if (mChangeObserver!=null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mChangeObserver);
        }
    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(List<Call> calls) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }


}
