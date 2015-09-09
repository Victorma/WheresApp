package tk.wheresoft.wheresapp.bussiness.contacts;

import android.os.Bundle;

import tk.wheresoft.wheresapp.model.Contact;

import java.util.List;

/**
 * Created by Victorma on 26/11/2014.
 */
public interface ASContacts {

    public boolean isRegistered();

    public Contact getUserRegistered();

    public Bundle register(String telephone);


    public List<Contact> getRecentContactList();

    public List<Contact> getFavouriteContactsList();

    public List<Contact> getContactList();

    public boolean updateContactList();

    public Contact getContact(Contact contact);

}
