package com.wheresapp.integration.contacts.factory;

import android.accounts.Account;
import android.content.Context;

import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.integration.contacts.factory.imp.DAOContactsFactoryImp;

/**
 * Created by Victorma on 25/11/2014.
 */
public abstract class DAOContactsFactory {

    private static DAOContactsFactory instance;
    public static DAOContactsFactory getInstance(){
        if(instance == null)
            instance = new DAOContactsFactoryImp();

        return instance;
    }

    public abstract DAOContacts getInstanceDAOContacts(Context context, Account account);

}
