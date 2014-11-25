package com.wheresapp.integration.contacts.factory.imp;

import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.integration.contacts.factory.DAOContactsFactory;
import com.wheresapp.integration.contacts.imp.DAOContactsImp;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOContactsFactoryImp extends DAOContactsFactory {

    @Override
    public DAOContacts getInstanceDAOContacts() {
        return new DAOContactsImp();
    }
}
