package tk.wheresoft.wheresapp.broadcastreceiver;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.google.gson.Gson;

import tk.wheresoft.wheresapp.activity.ActivityIncomingCall;
import tk.wheresoft.wheresapp.bussiness.calls.factory.ASCallsFactory;
import tk.wheresoft.wheresapp.integration.contacts.factory.DAOContactsFactory;
import tk.wheresoft.wheresapp.model.Call;
import tk.wheresoft.wheresapp.model.CallState;
import tk.wheresoft.wheresapp.model.Contact;
import tk.wheresoft.wheresapp.service.GcmContactUpdateService;

import java.io.IOException;

/**
 * This {@code WakefulBroadcastReceiver} takes care of creating and managing a
 * partial wake lock for your app. It passes off the work of processing the GCM
 * message to an {@code IntentService}, while ensuring that the device does not
 * go back to sleep in the transition. The {@code IntentService} calls
 * {@code GcmBroadcastReceiver.completeWakefulIntent()} when it is ready to
 * release the wake lock.
 */

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Gson gson = new Gson();
        Bundle extras = intent.getExtras();
        if (extras.containsKey("new")) {

            String message = extras.getString("message");
            Call call = gson.fromJson(message, Call.class);
            call.setIncoming(true);
            if (CallState.WAIT.equals(call.getState())) {
                Call temp = ASCallsFactory.getInstance().getInstanceASCalls(context).getActiveCall();
                if (temp == null) {
                    Contact contact = new Contact();
                    contact.setServerid(call.getSender());
                    contact = DAOContactsFactory.getInstance().getInstanceDAOContacts(context).read(contact);
                    if (contact != null) {
                        if (ASCallsFactory.getInstance().getInstanceASCalls(context).receiveCall(call)) {
                            Intent newIntent = new Intent(context, ActivityIncomingCall.class);
                            newIntent.putExtra(ActivityIncomingCall.KEY_CONTACT, contact);
                            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(newIntent);
                        }
                    } else {
                        try {
                            ASCallsFactory.getInstance().getInstanceASCalls(context).reject(call);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    try {
                        ASCallsFactory.getInstance().getInstanceASCalls(context).reject(call);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            setResultCode(Activity.RESULT_OK);
        } else if (extras.containsKey("contact")) {
            Intent service = new Intent(context, GcmContactUpdateService.class);
            startWakefulService(context, service);
        }
    }
}
