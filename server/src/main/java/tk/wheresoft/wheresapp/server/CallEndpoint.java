package tk.wheresoft.wheresapp.server;

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
import com.google.gson.Gson;

import tk.wheresoft.wheresapp.server.domain.CallServer;
import tk.wheresoft.wheresapp.server.domain.CallStateServer;
import tk.wheresoft.wheresapp.server.domain.MessageServer;
import tk.wheresoft.wheresapp.server.domain.UserRegistrationServer;

import java.io.IOException;
import java.util.Date;

/**
 * Created by Sergio on 01/12/2014.
 */
@Api(name = "callApi",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.wheresoft.tk", ownerName = "server.wheresapp.wheresoft.tk", packagePath=""))
public class CallEndpoint {

    private Gson gson = new Gson();

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "testCall", path = "{fromId}/testcall")
    public CallServer test(@Named("fromId") String fromId) throws BadRequestException, InternalServerErrorException {
        CallServer newCall = new CallServer();
        newCall.setSender(fromId);
        newCall.setReceiver("test");
        newCall.setState(CallStateServer.ACCEPT);
        newCall.setStart(new Date(System.currentTimeMillis()));
        newCall.setUpdate(newCall.getStart());
        OfyService.ofy().save().entity(newCall).now();
        return newCall;
    }


    @ApiMethod(name = "createCall", path = "{fromId}/call/{toId}")
    public CallServer createCall(@Named("fromId")String from,@Named("toId")String to) throws NotFoundException, InternalServerErrorException {
        CallServer newCall = new CallServer();
        UserRegistrationServer record = findUser(Long.parseLong(to));
        if(record == null) {
            throw new NotFoundException("User " + to + " not registered.");
        }
        newCall.setSender(from);
        newCall.setReceiver(to);
        OfyService.ofy().save().entity(newCall).now();
        try {
                sendCall(newCall,record.getRegId());
        } catch (IOException e) {
            throw new InternalServerErrorException("No se ha podido realizar la llamada");
        }
        return newCall;
    }

    @ApiMethod(name = "accept", path = "{fromId}/accept/{callId}")
    public CallServer accept(@Named("fromId")String fromId,@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        CallServer call = OfyService.ofy().load().type(CallServer.class).id(Long.parseLong(callId)).now();
        if (call.getState().equals(CallStateServer.WAIT)) {
            call.setState(CallStateServer.ACCEPT);
            call.setUpdate(new Date(System.currentTimeMillis()));
            OfyService.ofy().save().entity(call).now();
            try {
                sendCall(call,findUser(Long.parseLong(call.getSender())).getRegId());
            } catch (IOException e) {
                throw new InternalServerErrorException("No se ha podido realizar la llamada");
            }
            return call;
        }
        throw new BadRequestException("La llamada esta finalizada");
    }

    @ApiMethod(name = "deny", path = "{fromId}/deny/{callId}")
    public CallServer deny(@Named("fromId")String fromId,@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        CallServer call = OfyService.ofy().load().type(CallServer.class).id(Long.parseLong(callId)).now();
        if (call.getState().equals(CallStateServer.WAIT)) {
            call.setState(CallStateServer.END);
            call.setUpdate(new Date(System.currentTimeMillis()));
            call.setEnd(new Date(System.currentTimeMillis()));
            OfyService.ofy().save().entity(call).now();
            try {
                if (call.getSender().equals(fromId))
                    sendCall(call,findUser(Long.parseLong(call.getReceiver())).getRegId());
                else
                    sendCall(call,findUser(Long.parseLong(call.getSender())).getRegId());
            } catch (IOException e) {
                throw new InternalServerErrorException("No se ha podido realizar la llamada");
            }
            return call;
        }
        throw new BadRequestException("La llamada esta finalizada");
    }

    @ApiMethod(name = "end", path = "{fromId}/end/{callId}")
    public CallServer end(@Named("fromId")String fromId,@Named("callId")String callId) throws BadRequestException, InternalServerErrorException {
        CallServer call = OfyService.ofy().load().type(CallServer.class).id(Long.parseLong(callId)).now();
        if (call.getState().equals(CallStateServer.ACCEPT)) {
            call.setState(CallStateServer.END);
            call.setUpdate(new Date(System.currentTimeMillis()));
            call.setEnd(new Date(System.currentTimeMillis()));
            OfyService.ofy().save().entity(call).now();
            try {
                if (call.getSender().equals(fromId) && !call.getReceiver().equals("test"))
                    sendCall(call,findUser(Long.parseLong(call.getReceiver())).getRegId());
                else if (!call.getReceiver().equals("test"))
                    sendCall(call,findUser(Long.parseLong(call.getSender())).getRegId());
            } catch (IOException e) {
                throw new InternalServerErrorException("No se ha podido realizar la llamada");
            }
            return call;
        }
        throw new BadRequestException("La llamada esta finalizada");
    }

    @ApiMethod(name = "transmit", path = "{callId}/{fromId}/transmit/{position}")
    public MessageServer transmit(@Named("callId")String callId, @Named("fromId") String fromId, @Named("position")String position) throws BadRequestException, InternalServerErrorException {
        CallServer call = OfyService.ofy().load().type(CallServer.class).id(Long.parseLong(callId)).now();
        if (call.getState().equals(CallStateServer.ACCEPT)) {
            MessageServer message = new MessageServer();
            message.setCallId(callId);
            message.setFromId(fromId);
            message.setMessage(position);
            if (call.getSender().equals(fromId))
                message.setToId(call.getReceiver());
            else
                message.setToId(call.getSender());
            OfyService.ofy().save().entity(message).now();
            if (call.getReceiver().equals("test")) {
                Double lat = Double.parseDouble(message.getMessage().split(",")[0]) + 0.01;
                Double longitud = Double.parseDouble(message.getMessage().split(",")[1]) + 0.01;
                String newpos = lat + "," + longitud;
                MessageServer messageServer = new MessageServer();
                messageServer.setCallId(callId);
                messageServer.setFromId("test");
                messageServer.setToId(fromId);
                messageServer.setMessage(newpos);
                OfyService.ofy().save().entity(messageServer).now();
            }
            MessageServer messageServer = OfyService.ofy().load().type(MessageServer.class).filter("callId",call.getServerId()).filter("toId", fromId).order("-dateSend").first().now();
            if (messageServer==null) {
                messageServer = new MessageServer();
                messageServer.setMessage("WAIT");
            }
            return messageServer;
        }
        throw new BadRequestException("La llamada esta finalizada");
    }

    private void sendCall(CallServer call, String toId) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = null;
        if (call.getState().equals(CallStateServer.WAIT))
            msg = new Message.Builder().addData("message",gson.toJson(call)).addData("type", "call").addData("new","new").build();
        else
            msg = new Message.Builder().addData("message",gson.toJson(call)).addData("type", "call").addData("update","update").build();
        //Message msg = new Message.Builder().addData("message", "Prueba").build();
        Result resultTo = sender.send(msg, toId, 5);

        if (resultTo.getMessageId() != null) {
            String canonicalRegId = resultTo.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                UserRegistrationServer toUser = findUser(Long.parseLong(toId));
                toUser.setRegId(canonicalRegId);
                OfyService.ofy().save().entity(toUser).now();
            }
        }
    }

    private boolean existRecord(String phone) {
        return OfyService.ofy().load().type(UserRegistrationServer.class).filter("phone", phone).first().now()!=null;
    }

    private UserRegistrationServer findUser(Long userId) {
        return OfyService.ofy().load().type(UserRegistrationServer.class).filter("id", userId).first().now();
    }
}
