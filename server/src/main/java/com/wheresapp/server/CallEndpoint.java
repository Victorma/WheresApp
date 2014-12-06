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
import com.google.api.server.spi.response.InternalServerErrorException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.wheresapp.server.domain.CallServer;
import com.wheresapp.server.domain.CallStateServer;
import com.wheresapp.server.domain.UserRegistrationServer;

import java.io.IOException;

import static com.wheresapp.server.OfyService.ofy;

/**
 * Created by Sergio on 01/12/2014.
 */
@Api(name = "callApi",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.com", ownerName = "server.wheresapp.com", packagePath=""))
public class CallEndpoint {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "createCall", path = "{fromId}/call/{toId}")
    public CallServer createCall(@Named("fromId")String from,@Named("toId")String to) throws NotFoundException, InternalServerErrorException {
        CallServer newCall = new CallServer();
        UserRegistrationServer record = findUser(to);
        if(record == null) {
            throw new NotFoundException("User " + to + " not registered.");
        }
        newCall.setFrom(from);
        newCall.setTo(to);
        try {
            sendCall(newCall);
        } catch (IOException e) {
            throw new InternalServerErrorException("No se ha podido realizar la llamada");
        }
        ofy().save().entity(newCall).now();
        return newCall;
    }

    @ApiMethod(name = "accept", path = "{callId}/accept")
    public CallServer accept(@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        CallServer call = ofy().load().type(CallServer.class).filterKey(callId).first().now();
        if (call.getState().equals(CallStateServer.WAIT)) {
            call.setState(CallStateServer.ACCEPT);
            try {
                sendCall(call);
            } catch (IOException e) {
                throw new InternalServerErrorException("No se ha podido realizar la llamada");
            }
            ofy().save().entity(call).now();
            return call;
        }
        throw new BadRequestException("La llamada esta finalizada");
    }

    @ApiMethod(name = "deny", path = "{callId}/deny")
    public CallServer deny(@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        CallServer call = ofy().load().type(CallServer.class).filterKey(callId).first().now();
        if (call.getState().equals(CallStateServer.WAIT)) {
            call.setState(CallStateServer.DENY);
            try {
                sendCall(call);
            } catch (IOException e) {
                throw new InternalServerErrorException("No se ha podido realizar la llamada");
            }
            ofy().save().entity(call).now();
            return call;
        }
        throw new BadRequestException("La llamada esta finalizada");
    }

    @ApiMethod(name = "end", path = "{callId}/end")
    public CallServer end(@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        CallServer call = ofy().load().type(CallServer.class).filterKey(callId).first().now();
        if (call.getState().equals(CallStateServer.TRANSMIT)) {
            call.setState(CallStateServer.END);
            call.setDateEnd(new DateTime(System.currentTimeMillis()));
            try {
                sendCall(call);
            } catch (IOException e) {
                throw new InternalServerErrorException("No se ha podido realizar la llamada");
            }
            ofy().save().entity(call).now();
            return call;
        }
        throw new BadRequestException("La llamada esta finalizada");
    }

    @ApiMethod(name = "transmit", path = "{callId}/transmit/{position}")
    public CallServer transmit(@Named("callId")String callId,@Named("position")String position) throws BadRequestException, InternalServerErrorException {
        CallServer call = ofy().load().type(CallServer.class).filterKey(callId).first().now();
        if (call.getState().equals(CallStateServer.TRANSMIT)) {
            call.setPosition(position);
            try {
                sendCall(call);
            } catch (IOException e) {
                throw new InternalServerErrorException("No se ha podido realizar la llamada");
            }
            ofy().save().entity(call).now();
            return call;
        }
        throw new BadRequestException("La llamada esta finalizada");
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

    private boolean existRecord(String phone) {
        return ofy().load().type(UserRegistrationServer.class).filter("phone", phone).first().now()!=null;
    }

    private UserRegistrationServer findUser(String userId) {
        return ofy().load().type(UserRegistrationServer.class).filter("id", userId).first().now();
    }
}
