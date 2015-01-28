package tk.wheresoft.wheresapp.server;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import tk.wheresoft.wheresapp.server.domain.CallServer;
import tk.wheresoft.wheresapp.server.domain.ContactNotFound;
import tk.wheresoft.wheresapp.server.domain.MessageServer;
import tk.wheresoft.wheresapp.server.domain.UserRegistrationServer;

/**
 * Objectify service wrapper so we can statically register our persistence classes
 * More on Objectify here : https://code.google.com/p/objectify-appengine/
 *
 */
public class OfyService {

    static {
        ObjectifyService.register(UserRegistrationServer.class);
        ObjectifyService.register(CallServer.class);
        ObjectifyService.register(MessageServer.class);
        ObjectifyService.register(ContactNotFound.class);
    }

    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
