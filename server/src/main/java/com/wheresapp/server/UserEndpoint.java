package com.wheresapp.server;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.wheresapp.domain.Call;
import com.wheresapp.domain.ContactClient;
import com.wheresapp.domain.ContactList;

import static com.wheresapp.server.OfyService.ofy;

/**
 * Created by Sergio on 27/11/2014.
 */
@Api(name = "user",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.com", ownerName = "server.wheresapp.com", packagePath=""))
public class UserEndpoint {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "contactList", path = "{fromId}/contactList")
    public ContactList contacList(@Named("fromId")String from, ContactList contactList) throws NotFoundException, BadRequestException {
        ContactList exist = new ContactList();
        if (contactList== null || contactList.getContactClientList().size()==0) {
            throw new BadRequestException("Hay que enviar una lista de contactos");
        }
        for (ContactClient c : contactList.getContactClientList()) {
            if (existRecord(c.getPhone())) {
                exist.addContact(c);
            }
        }
        return exist;
    }

    private boolean existRecord(String phone) {
        return ofy().load().type(UserRegistration.class).filter("phone", phone).first().now()!=null;
    }
    
    private UserRegistration findUser(String userId) {
        return ofy().load().type(UserRegistration.class).filter("id", userId).first().now();
    }

}
