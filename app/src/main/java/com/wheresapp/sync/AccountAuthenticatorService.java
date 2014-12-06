package com.wheresapp.sync;

import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Service;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.wheresapp.SignUpActivity;

/**
 * Created by Sergio on 03/12/2014.
 */
public class AccountAuthenticatorService extends Service {
    private static final String TAG = "AccountAuthenticatorService";
    private static AccountAuthenticatorImpl sAccountAuthenticator = null;

    public AccountAuthenticatorService() {
        super();
    }

    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        if (intent.getAction().equals(android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            ret = getAuthenticator().getIBinder();
        return ret;
    }

    private AccountAuthenticatorImpl getAuthenticator() {
        if (sAccountAuthenticator == null)
            sAccountAuthenticator = new AccountAuthenticatorImpl(this);
        return sAccountAuthenticator;
    }

    private static class AccountAuthenticatorImpl extends AbstractAccountAuthenticator {
        private Context mContext;
        private final Handler handler = new Handler();

        public AccountAuthenticatorImpl(Context context) {
            super(context);
            mContext = context;
        }

        /*
         *  The user has requested to add a new account to the system.  We return an intent that will launch our login screen if the user has not logged in yet,
         *  otherwise our activity will just pass the user's credentials on to the account manager.
         */
        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            Account[] accounts = AccountManager.get(mContext).getAccountsByType(accountType);
            if (accounts.length>0) {
                final Bundle result = new Bundle();
                result.putInt(AccountManager.KEY_ERROR_CODE, 1001);
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "Solo se permite una cuenta por usuario.");

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "Solo se permite una cuenta por usuario.", Toast.LENGTH_SHORT).show();
                    }
                });

                return result;
            }
            Bundle result = new Bundle();
            Intent i = new Intent(mContext, SignUpActivity.class);
            i.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            result.putParcelable(AccountManager.KEY_INTENT, i);
            return result;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
            return null;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
            return null;
        }
    }
}
