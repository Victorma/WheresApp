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
import com.wheresapp.domain.CallState;

import java.io.IOException;

import static com.wheresapp.server.OfyService.ofy;

/**
 * Created by Sergio on 01/12/2014.
 */
@Api(name = "call",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.com", ownerName = "server.wheresapp.com", packagePath=""))
public class CallEndpoint {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "createCall", path = "{fromId}/call/{toId}")
    public Call createCall(@Named("fromId")String from,@Named("toId")String to) throws NotFoundException, InternalServerErrorException {
        Call newCall = new Call();
        UserRegistration record = findUser(to);
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
    public Call accept(@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        Call call = ofy().load().type(Call.class).filterKey(callId).first().now();
        if (call.getState().equals(CallState.WAIT)) {
            call.setState(CallState.ACCEPT);
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
    public Call deny(@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        Call call = ofy().load().type(Call.class).filterKey(callId).first().now();
        if (call.getState().equals(CallState.WAIT)) {
            call.setState(CallState.DENY);
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
    public Call end(@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        Call call = ofy().load().type(Call.class).filterKey(callId).first().now();
        if (call.getState().equals(CallState.TRANSMIT)) {
            call.setState(CallState.END);
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
    public Call transmit(@Named("callId")String callId,@Named("position")String position) throws BadRequestException, InternalServerErrorException {
        Call call = ofy().load().type(Call.class).filterKey(callId).first().now();
        if (call.getState().equals(CallState.TRANSMIT)) {
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



    private void sendCall(Call call) throws IOException {
        if (call.getState().equals(CallState.RECEIVE))
            call.setState(CallState.WAIT);
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().setData(call.toMap()).build();
        UserRegistration toUser = findUser(call.getTo());
        UserRegistration fromUser = findUser(call.getFrom());
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
        return ofy().load().type(UserRegistration.class).filter("phone", phone).first().now()!=null;
    }

    private UserRegistration findUser(String userId) {
        return ofy().load().type(UserRegistration.class).filter("id", userId).first().now();
    }
}
