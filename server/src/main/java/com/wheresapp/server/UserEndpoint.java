package com.wheresapp.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.wheresapp.server.domain.ContactListServer;
import com.wheresapp.server.domain.ContactServer;
import com.wheresapp.server.domain.UserRegistrationServer;

import static com.wheresapp.server.OfyService.ofy;

/**
 * Created by Sergio on 27/11/2014.
 */
@Api(name = "userApi",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.com", ownerName = "server.wheresapp.com", packagePath=""))
public class UserEndpoint {

    @ApiMethod(name = "contactList", path = "{fromId}/contactList")
    public ContactListServer contacList(@Named("fromId")String from, ContactListServer contactListServer) throws NotFoundException, BadRequestException {
        ContactListServer exist = new ContactListServer();
        UserRegistrationServer reg;
        if (contactListServer == null || contactListServer.getContactServerList().size()==0) {
            throw new BadRequestException("Hay que enviar una lista de contactos");
        }
        for (ContactServer c : contactListServer.getContactServerList()) {
            if (existRecord(c.getPhone())) {
                reg = getRecord(c.getPhone());
                c.setId(reg.getId());
                exist.addContact(c);
            }
        }
        return exist;
    }

    private boolean existRecord(String phone) {
        return ofy().load().type(UserRegistrationServer.class).filter("phone", phone).first().now()!=null;
    }

    private UserRegistrationServer getRecord(String phone) {
        return ofy().load().type(UserRegistrationServer.class).filter("phone", phone).first().now();
    }

}
