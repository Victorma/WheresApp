package tk.wheresoft.wheresapp.sync;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import tk.wheresoft.wheresapp.R;
import tk.wheresoft.wheresapp.activity.MainActivity;
import tk.wheresoft.wheresapp.bussiness.contacts.ASContacts;
import tk.wheresoft.wheresapp.bussiness.contacts.factory.ASContactsFactory;

/**
 * Created by Sergio on 05/12/2014.
 */
public class SyncContacts {
    private String TAG = "SyncContacts";
    private Account account;
    private Context context;
    public static final int NOTIFICATION_ID = 2;
    private NotificationManager mNotificationManager;

    public SyncContacts(Context context) {
        this.context = context;

        /*
        authority = "com.android.contacts";
        myId = getUserId();*/
    }


    public void performSync() throws OperationCanceledException {
        sendNotification("Actualizando contactos","Espere por favor.");
        ASContacts as = ASContactsFactory.getInstance().getInstanceASContacts(context);
        as.updateContactList();
        sendNotification("Contactos actualizados","Pulse aquí para ver.");
    }

    private void sendNotification(String title,String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
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
