package tk.wheresoft.wheresapp.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;

import tk.wheresoft.wheresapp.server.domain.ContactListServer;
import tk.wheresoft.wheresapp.server.domain.ContactNotFound;
import tk.wheresoft.wheresapp.server.domain.ContactServer;
import tk.wheresoft.wheresapp.server.domain.UserRegistrationServer;

/**
 * Created by Sergio on 27/11/2014.
 */
@Api(name = "userApi",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.wheresoft.tk", ownerName = "server.wheresapp.wheresoft.tk", packagePath=""))
public class UserEndpoint {

    @ApiMethod(name = "contactList", path = "{fromId}/contactList")
    public ContactListServer contacList(@Named("fromId")String from, ContactListServer contactListServer) throws NotFoundException, BadRequestException {
        ContactListServer exist = new ContactListServer();
        UserRegistrationServer reg;
        if (contactListServer == null || contactListServer.getContactServerList().size()==0) {
            throw new BadRequestException("Hay que enviar una lista de contactos");
        }

        for (ContactServer c : contactListServer.getContactServerList()) {
            reg = getRecord(c.getPhone());
            if (reg!=null) {
                c.setId(reg.getId());
                exist.addContact(c);
            } else {
                ContactNotFound contactNotFound = findRecordNew(c.getPhone());
                if (contactNotFound==null){
                    contactNotFound = new ContactNotFound();
                    contactNotFound.setPhone(c.getPhone());
                    contactNotFound.getContactKnows().add(from);
                } else {
                    contactNotFound.getContactKnows().add(from);
                }
                OfyService.ofy().save().entity(contactNotFound).now();
            }
        }
        return exist;
    }

    private UserRegistrationServer getRecord(String phone) {
        return OfyService.ofy().load().type(UserRegistrationServer.class).filter("phone", phone).first().now();
    }

    private ContactNotFound findRecordNew(String phone) {
        return OfyService.ofy().load().type(ContactNotFound.class).filter("phone", phone).first().now();
    }

}
