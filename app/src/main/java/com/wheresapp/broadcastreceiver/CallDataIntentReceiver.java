package com.wheresapp.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.wheresapp.integration.calls.DAOCalls;
import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.loader.CallListLoader;
import com.wheresapp.loader.ContactListLoader;

/**
 * Created by Sergio on 02/01/2015.
 */
public class CallDataIntentReceiver extends BroadcastReceiver {
    private CallListLoader mLoader;

    public CallDataIntentReceiver(CallListLoader mLoader) {
        this.mLoader = mLoader;
        LocalBroadcastManager.getInstance(mLoader.getContext()).registerReceiver(this,new IntentFilter(DAOCalls.filterChange));
    }

    public void onReceive(Context context, Intent intent) {
        mLoader.onContentChanged();
        Log.i("Loader:", "Broadcast call received");
    }
}
