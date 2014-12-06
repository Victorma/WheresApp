package com.wheresapp;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.accounts.OperationCanceledException;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.wheresapp.modelTEMP.Contact;
import com.wheresapp.server.ServerAPI;
import com.wheresapp.server.registration.model.UserRegistration;
import com.wheresapp.sync.SyncContacts;

import java.io.IOException;


public class SignUpActivity extends AccountAuthenticatorActivity {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final String PROPERTY_USER_NUMBER = "userNumber";
    private static final String PROPERTY_USER_ID = "userId";
    private static final String TAG = "SignUpActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog progressBar;
    Button buttonSignUp;
    String regId;
    String userNumber;
    Contact user;
    Context context;
    EditText mUserName;

    GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context = getApplicationContext();
        user = getUserRegistered();

        // Check device for Play Services APK.
        //If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);

            if (user==null) {
                // Puede ser que no encuentre en preferencias o que no este registrado
                mUserName = (EditText) findViewById(R.id.userName);
                buttonSignUp = (Button) findViewById(R.id.buttonSignUp);
                buttonSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        userNumber = "+34" + mUserName.getText().toString();
                        if (userNumber != null && userNumber != "") {
                            progressBar = ProgressDialog.show(SignUpActivity.this, null, getString(R.string.loading));
                            registerInBackground();
                        } else {
                            Toast.makeText(context, "Enter phone number",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });

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
        i.putExtra("NUMBER", user.getTelephone());
        i.putExtra("USERID",user.getId());
        Log.d(TAG, "onClick of login: Before starting userlist activity.");
        if (progressBar != null)
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
        startActivity(i);
        finish();
        Log.d(TAG, "onClick of Login: After finish.");
    }

    private Contact getUserRegistered() {
        user = new Contact();
        final SharedPreferences prefs = getSharedPreferences(
                SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        user.setServerid(prefs.getString(PROPERTY_USER_ID,""));
        if (user.getId()==0) {
            Log.i(TAG, "Registration not found.");
            return null;
        }
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion();
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return null;
        }
        user.setTelephone(prefs.getString(PROPERTY_USER_NUMBER, ""));
        if (user.getTelephone().isEmpty()) {
            Log.i(TAG, "Phone number not found.");
            return null;
        }
        user.setGcmId(prefs.getString(PROPERTY_REG_ID, ""));
        if (user.getGcmId().isEmpty()) {
            Log.i(TAG, "GCM id not found.");
            return null;
        }
        return user;
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
                    user = ServerAPI.getInstance().registrarUsuario(userNumber,regId);
                    Bundle result = null;
                    Account account = new Account(user.getTelephone(), context.getString(R.string.ACCOUNT_TYPE));
                    AccountManager am = AccountManager.get(context);
                    if (am.addAccountExplicitly(account, null, null)) {
                        result = new Bundle();
                        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                        result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                        setAccountAuthenticatorResult(result);
                    }

                    SyncContacts syncContacts = new SyncContacts(SignUpActivity.this);
                    syncContacts.performSync();

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d(TAG, "Error: " + msg);
                } catch (OperationCanceledException e) {
                    msg = "Error :" + e.getMessage();
                    Log.d(TAG, "Error: " + msg);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                if(user.getId()!=null && user.getId()!=0) {
                    storeRegistration(user);
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

    private void storeRegistration(Contact user) {
        final SharedPreferences prefs = getSharedPreferences(
                SignUpActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        int appVersion = getAppVersion();
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, user.getGcmId());
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putString(PROPERTY_USER_NUMBER, user.getTelephone());
        editor.putLong(PROPERTY_USER_ID, user.getId());
        editor.commit();
        Toast.makeText(context, "Sign Up Complete!", Toast.LENGTH_LONG).show();
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
