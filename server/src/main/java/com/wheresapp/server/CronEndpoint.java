package com.wheresapp.server;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.wheresapp.server.domain.CallServer;
import com.wheresapp.server.domain.CallStateServer;
import com.wheresapp.server.domain.MessageServer;
import com.wheresapp.server.domain.UserRegistrationServer;

import java.io.IOException;
import java.util.List;

import static com.wheresapp.server.OfyService.ofy;

/**
 * Created by Sergio on 01/12/2014.
 */
@Api(name = "cronApi",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.com", ownerName = "server.wheresapp.com", packagePath=""))
public class CronEndpoint {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "check", path = "/check")
    public void contacList() throws IOException {
        List<CallServer> queryTransmit = ofy().load().type(CallServer.class).filter("state", CallStateServer.TRANSMIT).list();
        for (CallServer call : queryTransmit) {
            if ((System.currentTimeMillis() - call.getDateStart().getValue()) > 60000 ) {
                MessageServer messageFrom = ofy().load().type(MessageServer.class).filter("callId", call.getId()).filter("toId", call.getFrom()).order("-date").first().now();
                MessageServer messageTo = ofy().load().type(MessageServer.class).filter("callId", call.getId()).filter("toId", call.getTo()).order("-date").first().now();
                if (messageFrom==null) {
                    call.setState(CallStateServer.FAILED);
                    ofy().save().entity(call).now();
                    sendCall(call);
                    continue;
                }
                if (messageTo==null) {
                    call.setState(CallStateServer.FAILED);
                    ofy().save().entity(call).now();
                    sendCall(call);
                    continue;
                }
                if ((System.currentTimeMillis() - messageFrom.getDate().getValue()) > 60000) {
                    call.setDateEnd(new DateTime(System.currentTimeMillis()));
                    ofy().save().entity(call).now();
                    sendCall(call);
                    continue;
                }
                if ((System.currentTimeMillis() - messageTo.getDate().getValue()) > 60000) {
                    call.setDateEnd(new DateTime(System.currentTimeMillis()));
                    ofy().save().entity(call).now();
                    sendCall(call);
                    continue;
                }
            }
        }

        List<CallServer> queryWait = ofy().load().type(CallServer.class).filter("state", CallStateServer.WAIT).list();
        for (CallServer call : queryWait) {
            if ((System.currentTimeMillis() - call.getDateStart().getValue()) > 120000 ) {
                call.setDateEnd(new DateTime(System.currentTimeMillis()));
                call.setState(CallStateServer.FAILED);
                ofy().save().entity(call).now();
                sendCall(call);
                continue;
            }
        }
    }

    private void sendCall(CallServer call) throws IOException {
        if (call.getState().equals(CallStateServer.RECEIVE))
            call.setState(CallStateServer.WAIT);
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().setData(call.toMap()).build();
        UserRegistrationServer toUser = findUser(call.getTo());
        UserRegistrationServer fromUser = findUser(call.getFrom());
        Result resultTo = sender.send(msg, toUser.getRegId(), 5);
        Result resultFrom = sender.send(msg, fromUser.getRegId(), 5);
        //Enviar llamada al receptor
        if (resultTo.getMessageId() != null) {
            String canonicalRegId = resultTo.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                toUser.setRegId(canonicalRegId);
                ofy().save().entity(toUser).now();
            }
        } else {
            String error = resultTo.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // if the device is no longer registered with Gcm, remove it from the datastore
                ofy().delete().entity(toUser).now();
            }
        }
        //Enviar llamada al emisor
        if (resultTo.getMessageId() != null) {
            String canonicalRegId = resultTo.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                toUser.setRegId(canonicalRegId);
                ofy().save().entity(toUser).now();
            }
        } else {
            String error = resultTo.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // if the device is no longer registered with Gcm, remove it from the datastore
                ofy().delete().entity(toUser).now();
            }
        }
    }

    private UserRegistrationServer findUser(String userId) {
        return ofy().load().type(UserRegistrationServer.class).filter("id", userId).first().now();
    }

}
