package tk.wheresoft.wheresapp.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Cache cache = null;
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            e.printStackTrace();
        }
        if (cache.isEmpty()) {
            loadCache(cache);
        }
        ContactListServer exist = new ContactListServer();
        UserRegistrationServer reg;
        if (contactListServer == null || contactListServer.getContactServerList().size()==0) {
            throw new BadRequestException("Hay que enviar una lista de contactos");
        }

        for (ContactServer c : contactListServer.getContactServerList()) {
            reg = (UserRegistrationServer) cache.get(c.getPhone());
            if (reg!=null) {
                c.setId(reg.getId());
                exist.addContact(c);
            }
        }
        return exist;
    }

    public void loadCache(Cache cache) {
        List<UserRegistrationServer> allUser = getAll();
        Map<String,UserRegistrationServer> mapa = new HashMap<String,UserRegistrationServer>();
        for (UserRegistrationServer user : allUser) {
            mapa.put(user.getPhone(),user);
        }
        cache.putAll(mapa);
    }

    private List<UserRegistrationServer> getAll() {
        return OfyService.ofy().load().type(UserRegistrationServer.class).list();
    }

    private UserRegistrationServer getRecord(String phone) {
        return OfyService.ofy().load().type(UserRegistrationServer.class).filter("phone", phone).first().now();
    }

    private ContactNotFound findRecordNew(String phone) {
        return OfyService.ofy().load().type(ContactNotFound.class).filter("phone", phone).first().now();
    }

}
