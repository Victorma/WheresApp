package tk.wheresoft.wheresapp.bussiness.contacts.factory.imp;

import android.content.Context;

import tk.wheresoft.wheresapp.bussiness.contacts.ASContacts;
import tk.wheresoft.wheresapp.bussiness.contacts.factory.ASContactsFactory;
import tk.wheresoft.wheresapp.bussiness.contacts.imp.ASContactsImp;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASContactsFactoryImp extends ASContactsFactory {
    @Override
    public ASContacts getInstanceASContacts(Context context) {
        return new ASContactsImp(context);
    }
}
