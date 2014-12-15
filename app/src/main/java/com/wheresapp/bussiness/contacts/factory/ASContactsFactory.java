package com.wheresapp.bussiness.contacts.factory;

import android.content.Context;

import com.wheresapp.bussiness.contacts.ASContacts;
import com.wheresapp.bussiness.contacts.factory.imp.ASContactsFactoryImp;

/**
 * Created by Victorma on 26/11/2014.
 */
public abstract class ASContactsFactory {
    private static ASContactsFactory instance;
    public static ASContactsFactory getInstance(){
        if(instance == null)
            instance = new ASContactsFactoryImp();
        return instance;
    }
    public abstract ASContacts getInstanceASContacts(Context context);

}
