package com.wheresapp.integration.contacts;

import com.wheresapp.integration.DAO;
import com.wheresapp.model.Contact;

/**
 * Created by Victorma on 25/11/2014.
 */
public interface DAOContacts extends DAO<Contact> {
    public static String filterChange = "com.wheresapp.contact.change";
}
