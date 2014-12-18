package com.wheresapp.integration.contacts.factory.imp;

import android.accounts.Account;
import android.content.Context;

import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.integration.contacts.factory.DAOContactsFactory;
import com.wheresapp.integration.contacts.imp.DAOContactsImp;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOContactsFactoryImp extends DAOContactsFactory {

    @Override
    public DAOContacts getInstanceDAOContacts(Context c) {
        return new DAOContactsImp(c);
    }
}
