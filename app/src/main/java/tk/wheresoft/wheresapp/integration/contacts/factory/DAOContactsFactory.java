package tk.wheresoft.wheresapp.integration.contacts.factory;

import android.content.Context;

import tk.wheresoft.wheresapp.integration.contacts.DAOContacts;
import tk.wheresoft.wheresapp.integration.contacts.factory.imp.DAOContactsFactoryImp;

/**
 * Created by Victorma on 25/11/2014.
 */
public abstract class DAOContactsFactory {

    private static DAOContactsFactory instance;

    public static DAOContactsFactory getInstance() {
        if (instance == null)
            instance = new DAOContactsFactoryImp();

        return instance;
    }

    public abstract DAOContacts getInstanceDAOContacts(Context context);

}
