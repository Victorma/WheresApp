package tk.wheresoft.wheresapp.integration.contacts.factory.imp;

import android.content.Context;

import tk.wheresoft.wheresapp.integration.contacts.DAOContacts;
import tk.wheresoft.wheresapp.integration.contacts.factory.DAOContactsFactory;
import tk.wheresoft.wheresapp.integration.contacts.imp.DAOContactsImp;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOContactsFactoryImp extends DAOContactsFactory {

    @Override
    public DAOContacts getInstanceDAOContacts(Context c) {
        return new DAOContactsImp(c);
    }
}
