package com.wheresapp.server.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 24/11/2014.
 */
public class ContactListServer {
    private List<ContactServer> contactServerList;

    public ContactListServer() {
        contactServerList = new ArrayList<ContactServer>();
    }

    public List<ContactServer> getContactServerList() {
        return contactServerList;
    }

    public void setContactServerList(List<ContactServer> contactServerList) {
        this.contactServerList = contactServerList;
    }

    public void addContact(ContactServer contact) {
        contactServerList.add(contact);
    }
}
