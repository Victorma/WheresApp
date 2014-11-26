package com.wheresapp.bussiness.contacts;

import com.wheresapp.modelTEMP.Contact;

import java.util.List;

/**
 * Created by Victorma on 26/11/2014.
 */
public interface ASContacts {

    public List<Contact> getRecentContactList();
    public List<Contact> getFavouriteContactsList();
    public List<Contact> getContactList();
    public boolean updateContactList();
    public Contact getContact(Contact contact);

}
