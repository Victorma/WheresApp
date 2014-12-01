package com.wheresapp.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 24/11/2014.
 */
public class ContactList {
    private List<ContactClient> contactClientList;

    public ContactList() {
        contactClientList = new ArrayList<ContactClient>();
    }

    public List<ContactClient> getContactClientList() {
        return contactClientList;
    }

    public void setContactClientList(List<ContactClient> contactClientList) {
        this.contactClientList = contactClientList;
    }

    public void addContact(ContactClient contact) {
        contactClientList.add(contact);
    }
}
