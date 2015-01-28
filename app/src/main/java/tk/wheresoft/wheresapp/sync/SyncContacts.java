package tk.wheresoft.wheresapp.sync;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.content.Context;

import tk.wheresoft.wheresapp.bussiness.contacts.ASContacts;
import tk.wheresoft.wheresapp.bussiness.contacts.factory.ASContactsFactory;

/**
 * Created by Sergio on 05/12/2014.
 */
public class SyncContacts {
    private String TAG = "SyncContacts";
    private Account account;
    private Context context;

    public SyncContacts(Context context) {
        this.context = context;

        /*
        authority = "com.android.contacts";
        myId = getUserId();*/
    }


    public void performSync() throws OperationCanceledException {

        ASContacts as = ASContactsFactory.getInstance().getInstanceASContacts(context);
        as.updateContactList();
    }

/*
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
    }*/
}
