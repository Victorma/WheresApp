package com.wheresapp.bussiness.contacts.factory.imp;

import android.content.Context;

import com.wheresapp.bussiness.contacts.ASContacts;
import com.wheresapp.bussiness.contacts.factory.ASContactsFactory;
import com.wheresapp.bussiness.contacts.imp.ASContactsImp;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASContactsFactoryImp extends ASContactsFactory {
    @Override
    public ASContacts getInstanceASContacts(Context context) {
        return new ASContactsImp(context);
    }
}
