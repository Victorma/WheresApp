package tk.wheresoft.wheresapp.server;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.NotFoundException;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheManager;

import tk.wheresoft.wheresapp.server.domain.ContactNotFound;
import tk.wheresoft.wheresapp.server.domain.UserRegistrationServer;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.inject.Named;

/**
 * A registration endpoint class we are exposing for a device's GCM registration id on the backend
 *
 * For more information, see
 * https://developers.google.com/appengine/docs/java/endpoints/
 *
 * NOTE: This endpoint does not use any form of authorization or
 * authentication! If this app is deployed, anyone can access this endpoint! If
 * you'd like to add authentication, take a look at the documentation.
 */
@Api(name = "registrationApi",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.wheresoft.tk", ownerName = "server.wheresapp.wheresoft.tk", packagePath=""))
public class RegistrationEndpoint {

    private static final Logger log = Logger.getLogger(RegistrationEndpoint.class.getName());

    private static final String API_KEY = System.getProperty("gcm.api.key");

    /**
     * Register a device to the backend
     *
     * @param me The Google Cloud Messaging registration Id to add
     */
    @ApiMethod(name = "register")
    public UserRegistrationServer registerDevice(UserRegistrationServer me) throws ConflictException, IOException {
        Cache cache = null;
        try {
            CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
        } catch (CacheException e) {
            e.printStackTrace();
        }
        UserRegistrationServer record = findRecord(me.getPhone());
        if(record == null) {
            record = new UserRegistrationServer();
            record.setPhone(me.getPhone());
            record.setName(me.getName());
        }
        record.setRegId(me.getRegId());
        OfyService.ofy().save().entity(record).now();
        //ContactNotFound listOfContact = findRecordNew(me.getPhone());
        //if (listOfContact!=null) {
        //    for (String id : listOfContact.getContactKnows()) {
        //        sendUpdate(id);
        //    }
        //}
        me.setId(record.getId());
        cache.put(record.getPhone(),record);
        return me;
    }

    private void sendUpdate(String toId) throws IOException {
        Sender sender = new Sender(API_KEY);
        UserRegistrationServer toUser = OfyService.ofy().load().type(UserRegistrationServer.class).id(Long.parseLong(toId)).now();
        Message msg = null;
            msg = new Message.Builder().addData("type", "contact").build();

        //Message msg = new Message.Builder().addData("message", "Prueba").build();
        Result resultTo = sender.send(msg, toUser.getRegId(), 5);

        if (resultTo.getMessageId() != null) {
            String canonicalRegId = resultTo.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                toUser = OfyService.ofy().load().type(UserRegistrationServer.class).id(Long.parseLong(toId)).now();
                toUser.setRegId(canonicalRegId);
                OfyService.ofy().save().entity(toUser).now();
            }
        }
    }

    /**
     * Unregister a device from the backend
     *
     * @param regId The Google Cloud Messaging registration Id to remove
     */
    @ApiMethod(name = "unregister")
    public void unregisterDevice(@Named("regId") String regId) throws NotFoundException {
        UserRegistrationServer record = findRecord(regId);
        if(record == null) {
            log.info("Device " + regId + " not registered, skipping unregister");
            throw new NotFoundException("Device " + regId + " not registered, skipping unregister");
        }
        OfyService.ofy().delete().entity(record).now();
    }



    /**
     * Return a collection of registered devices
     *
     * @param count The number of devices to list
     * @return a list of Google Cloud Messaging registration Ids
     */
    @ApiMethod(name = "listDevices")
    public CollectionResponse<UserRegistrationServer> listDevices(@Named("count") int count) {
        List<UserRegistrationServer> records = OfyService.ofy().load().type(UserRegistrationServer.class).limit(count).list();
        return CollectionResponse.<UserRegistrationServer>builder().setItems(records).build();
    }

    private List<UserRegistrationServer> getAll() {
        return OfyService.ofy().load().type(UserRegistrationServer.class).list();
    }

    private UserRegistrationServer findRecord(String phone) {
        return OfyService.ofy().load().type(UserRegistrationServer.class).filter("phone", phone).first().now();
    }

    private ContactNotFound findRecordNew(String phone) {
        return OfyService.ofy().load().type(ContactNotFound.class).filter("phone", phone).first().now();
    }


}