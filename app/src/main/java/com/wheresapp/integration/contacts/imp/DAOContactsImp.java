package com.wheresapp.integration.contacts.imp;

import com.wheresapp.integration.contacts.DAOContacts;
import com.wheresapp.modelTEMP.Contact;

import java.util.List;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOContactsImp implements DAOContacts {
    @Override
    public boolean create(Contact contact) {
        return false;
    }

    @Override
    public Contact read(Contact contact) {
        return null;
    }

    @Override
    public boolean update(Contact contact) {
        return false;
    }

    @Override
    public boolean delete(Contact contact) {
        return false;
    }

    @Override
    public List<Contact> discover(Contact contact) {
        return null;
    }
}
