package com.wheresapp.bussiness.calls.imp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.wheresapp.ActivityIncomingCall;
import com.wheresapp.bussiness.calls.ASCalls;
import com.wheresapp.integration.calls.factory.DAOCallsFactory;
import com.wheresapp.integration.contacts.factory.DAOContactsFactory;
import com.wheresapp.integration.contacts.imp.DAOContactsImp;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASCallsImp implements ASCalls {

    private Context context;

    public ASCallsImp(Context context) {
        this.context = context;
    }

    @Override
    public boolean call(Contact contact) {
        if (getActiveCall()==null) {
            //TODO
            //Llamada API al servidor y guardar en la base de datos
        }
        return false;
    }

    @Override
    public boolean accept(Call call) {
        return false;
    }

    @Override
    public boolean reject(Call call) {
        return false;
    }

    @Override
    public boolean end(Call call) {
        return false;
    }

    @Override
    public boolean receiveCall(Call call) {
        if (getActiveCall() != null) {
            DAOCallsFactory.getInstance().getInstanceDAOCalls(context).create(call);

            Contact contact = new Contact();
            contact.setServerid(call.getSender());
            contact = DAOContactsFactory.getInstance().getInstanceDAOContacts(context).read(contact);
            Intent intent = new Intent(context, ActivityIncomingCall.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(ActivityIncomingCall.KEY_CONTACT,contact);
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public Call getActiveCall() {
        return null;
    }
}
