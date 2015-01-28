package tk.wheresoft.wheresapp.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.gson.Gson;
import tk.wheresoft.wheresapp.R;

import tk.wheresoft.wheresapp.activity.MainActivity;
import tk.wheresoft.wheresapp.bussiness.contacts.factory.ASContactsFactory;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmContactUpdateService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    public static final String TAG = "GcmContactUpdateService";
    NotificationCompat.Builder builder;
    private Gson gson = new Gson();
    private NotificationManager mNotificationManager;

    public GcmContactUpdateService() {
        super("GcmContactUpdateService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent " + intent.getDataString());
        Bundle extras = intent.getExtras();
        ASContactsFactory.getInstance().getInstanceASContacts(this).updateContactList();
        sendNotification("Contactos actualizados, pulse aquí.");
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg) {
        Log.d(TAG, "Preparing to send notification...: " + msg);
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("Lista de contactos")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }
}