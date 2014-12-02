package com.wheresapp.bussiness.contacts.imp;

import com.wheresapp.bussiness.contacts.ASContacts;
import com.wheresapp.integration.calls.DAOCalls;
import com.wheresapp.integration.calls.factory.DAOCallsFactory;
import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.integration.contacts.factory.DAOContactsFactory;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASContactsImp implements ASContacts {

    @Override
    public List<Contact> getRecentContactList() {

        DAOCalls daoCalls = DAOCallsFactory.getInstance().getInstanceDAOCalls();
        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts();

        List<Contact> contacts = new ArrayList<Contact>();
        boolean stop = false;
        int page = 0;
        while(!stop || contacts.size()<15){
            List<Call> calls = daoCalls.discover(new Call(),20,page);
            if(calls == null || calls.size() == 0)
                stop = true;
            else{
                for(Call call : calls){
                    Contact search = new Contact();
                    search.setTelephone(call.getReceiver());
                    Contact c = daoContacts.read(search);
                    if(!contacts.contains(c))
                        contacts.add(c);
                }
                page++;
            }
        }

        return contacts;
    }

    @Override
    public List<Contact> getFavouriteContactsList() {

        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts();

        Contact pattern = new Contact();
        pattern.setFavourite(true);

        return daoContacts.discover(pattern);
    }

    @Override
    public List<Contact> getContactList() {
        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts();
        return daoContacts.discover(new Contact());
    }

    @Override
    public boolean updateContactList() {

        // TODO mañana XD



        return false;
    }

    @Override
    public Contact getContact(Contact contact) {

        DAOContacts daoContacts = DAOContactsFactory.getInstance().getInstanceDAOContacts();
        Contact r = null;
        if(contact.getTelephone()!=null)
            r = daoContacts.read(contact);

        return r;
    }
}
