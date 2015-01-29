package tk.wheresoft.wheresapp.activity;

import android.accounts.AccountAuthenticatorActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import tk.wheresoft.wheresapp.R;

import tk.wheresoft.wheresapp.bussiness.contacts.ASContacts;
import tk.wheresoft.wheresapp.bussiness.contacts.factory.ASContactsFactory;
import tk.wheresoft.wheresapp.model.Contact;


public class SignUpActivity extends AccountAuthenticatorActivity {

    private static final String TAG = "SignUpActivity";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Button buttonSignUp;
    String userNumber;
    Contact user;
    Context context;
    EditText mUserName;
    GoogleCloudMessaging gcm;
    private ProgressDialog progressBar;
    private ASContacts asContacts;
    private NotificationManager mNotificationManager;
    public static final int NOTIFICATION_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = getApplicationContext();

        // Check device for Play Services APK.
        //If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            asContacts = ASContactsFactory.getInstance().getInstanceASContacts(context);
            if (asContacts.isRegistered()) {
                lanzaApp();
            } else {
                gcm = GoogleCloudMessaging.getInstance(this);
                // Puede ser que no encuentre en preferencias o que no este registrado
                mUserName = (EditText) findViewById(R.id.userName);
                buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
                buttonSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        userNumber = "+34" + mUserName.getText().toString();
                        if (!userNumber.equals("")) {
                            progressBar = ProgressDialog.show(SignUpActivity.this, null, getString(R.string.loading));
                            registerInBackground();
                        } else {
                            Toast.makeText(context, "Enter phone number",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    private void lanzaApp() {
        Intent i = new Intent(context, MainActivity.class);
        Log.d(TAG, "onClick of login: Before starting userlist activity.");
        if (progressBar != null)
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        startActivity(i);
        finish();
        Log.d(TAG, "onClick of Login: After finish.");
    }

    //step 1: register with Google GCM server
    private void registerInBackground() {
        new AsyncTask<Void, Void, Bundle>() {
            @Override
            protected Bundle doInBackground(Void... params) {
                return asContacts.register(userNumber);
            }

            @Override
            protected void onPostExecute(Bundle bundle) {

                if (bundle.containsKey("ERROR_MSG")) {
                    // Tengo que entender esto aún...
                } else if (bundle.containsKey("USER")) {
                    user = (Contact) bundle.getSerializable("USER");
                }
                if (user.getServerid() != null && !user.getServerid().equals("")) {
                    Toast.makeText(context,
                            "Register complete!",
                            Toast.LENGTH_LONG).show();
                    lanzaApp();
                } else {
                    if (progressBar.isShowing()) {
                        progressBar.dismiss();
                    }
                    Toast.makeText(context,
                            "WheresApp Register Not Available!",
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
