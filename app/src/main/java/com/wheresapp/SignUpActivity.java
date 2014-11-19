package com.wheresapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;


public class SignUpActivity extends Activity {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_USER_NUMBER = "userNumber";
    private static final String TAG = "SignUpActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog progressBar;
    Button buttonSignUp;
    Button buttonLogin;
    String regId;
    String userNumber;
    AsyncTask<Void, Void, String> sendTask;
    AtomicInteger ccsMsgId = new AtomicInteger();
    Context context;

    GoogleCloudMessaging gcm;
    Intent intent;
    MessageSender messageSender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);
        context = getApplicationContext();

        progressBar = ProgressDialog.show(SignUpActivity.this, null, getString(R.string.loading));

        // Check device for Play Services APK.
        //If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regId = getRegistrationId();

            intent = new Intent(this, GcmIntentService.class);

            lanzaApp();

            if (regId.isEmpty()) {
                registerReceiver(broadcastReceiver, new IntentFilter("com.wheresapp.signup"));

                // Puede ser que no encuentre en preferencias o que no este registrado
                EditText mUserName = (EditText) findViewById(R.id.userName);
                userNumber = mUserName.getText().toString();
                if (userNumber != null && userNumber != "") {
                    buttonLogin = (Button) findViewById(R.id.buttonLogin);
                    buttonLogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            //step 0: check if it's registered
                            sendRequest();
                        }
                    });

                    buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
                    messageSender = new MessageSender();
                    buttonSignUp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            //step 1: check if it's registered
                            sendRequest();
                        }
                    });
                } else {
                    Toast.makeText(context, "Enter phone number",
                            Toast.LENGTH_LONG).show();
                }
            } else {
                lanzaApp();
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

    private int getAppVersion() {
        try {
            PackageInfo packageInfo;
            packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void lanzaApp() {
        Intent i = new Intent(context, MainActivity.class);
        i.putExtra("NUMBER", userNumber);
        Log.d(TAG, "onClick of login: Before starting userlist activity.");
        if (progressBar.isShowing()) {
            progressBar.dismiss();
        }
        startActivity(i);
        finish();
        Log.d(TAG, "onClick of Login: After finish.");
    }

    private String getRegistrationId() {
        final SharedPreferences prefs = getSharedPreferences(
                SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        userNumber = prefs.getString(PROPERTY_USER_NUMBER, "");
        if (userNumber.isEmpty()) {
            Log.i(TAG, "Phone number not found.");
            return "";
        }
        return registrationId;
    }

    //step 1: register with Google GCM server
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regId = gcm.register(ProjectID.SENDER_ID);
                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;
                    storeRegistrationId(regId);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d(TAG, "Error: " + msg);
                }
                Log.d(TAG, "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(!regId.isEmpty()) {
                    Log.d(TAG, "Registered with GCM Server." + msg);
                    sendRequest();
                } else {
                    Toast.makeText(context,
                            "Google GCM RegId Not Available!",
                            Toast.LENGTH_LONG).show();
                }
            }
        }.execute(null, null, null);
    }

    //step 2: register with XMPP App Server
    private void sendRequest() {
        Bundle dataBundle = new Bundle();
        dataBundle.putString("ACTION", "SIGNUP");
        dataBundle.putString("USER_NAME", userNumber);
        dataBundle.putString("REG_ID", regId);
        messageSender.sendMessage(dataBundle,gcm);
        Toast.makeText(context, "Sign Up Complete!",
                Toast.LENGTH_LONG).show();
    }

    private void storeRegistrationId(String regId) {
        final SharedPreferences prefs = getSharedPreferences(
                SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion();
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putString(PROPERTY_USER_NUMBER, userNumber);
        editor.commit();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            regId = intent.getStringExtra("REG_ID");
            if (regId != null && regId != ""){
                storeRegistrationId(regId);
                Toast.makeText(context, "Complete!",
                        Toast.LENGTH_LONG).show();
                lanzaApp();
            }
            else {
                registerInBackground();
                Log.d(TAG, "GCM RegId: " + regId);
            }
        }
    };

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
