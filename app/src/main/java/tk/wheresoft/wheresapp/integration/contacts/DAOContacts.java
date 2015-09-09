package tk.wheresoft.wheresapp.integration.contacts;

import tk.wheresoft.wheresapp.integration.DAO;
import tk.wheresoft.wheresapp.model.Contact;

/**
 * Created by Victorma on 25/11/2014.
 */
public interface DAOContacts extends DAO<Contact> {
    public static String filterChange = "tk.wheresoft.wheresapp.contact.change";
}
