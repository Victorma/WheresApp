package tk.wheresoft.wheresapp.server;

import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;
import com.google.gson.Gson;

import tk.wheresoft.wheresapp.server.domain.CallServer;
import tk.wheresoft.wheresapp.server.domain.MessageServer;
import tk.wheresoft.wheresapp.server.domain.CallStateServer;
import tk.wheresoft.wheresapp.server.domain.UserRegistrationServer;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Sergio on 01/12/2014.
 */
public class CronEndpoint extends HttpServlet {

    private Gson gson = new Gson();

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CallServer> queryTransmit = OfyService.ofy().load().type(CallServer.class).filter("state", CallStateServer.ACCEPT).list();
        for (CallServer call : queryTransmit) {
            if (call.getReceiver().equals("test"))
                continue;
            Long nowTime = System.currentTimeMillis();
            Long initTime = call.getStart().getTime();
            if ((nowTime - initTime) > 60000 ) {
                MessageServer messageFrom = OfyService.ofy().load().type(MessageServer.class).filter("callId", call.getServerId()).filter("toId", call.getSender()).order("-date").first().now();
                MessageServer messageTo = OfyService.ofy().load().type(MessageServer.class).filter("callId", call.getServerId()).filter("toId", call.getReceiver()).order("-date").first().now();
                if (messageFrom==null) {
                    call.setState(CallStateServer.END);
                    OfyService.ofy().save().entity(call).now();
                    call.setUpdate(call.getEnd());
                    sendCall(call);
                    continue;
                } else if ((nowTime - messageFrom.getDate().getTime()) > 30000) {
                    call.setState(CallStateServer.END);
                    call.setEnd(new Date(System.currentTimeMillis()));
                    call.setUpdate(call.getEnd());
                    OfyService.ofy().save().entity(call).now();
                    sendCall(call);
                    continue;
                }
                if (messageTo==null) {
                    call.setState(CallStateServer.END);
                    OfyService.ofy().save().entity(call).now();
                    call.setUpdate(call.getEnd());
                    sendCall(call);
                    continue;
                } else if ((nowTime - messageTo.getDate().getTime()) > 30000) {
                    call.setState(CallStateServer.END);
                    call.setEnd(new Date(System.currentTimeMillis()));
                    call.setUpdate(call.getEnd());
                    OfyService.ofy().save().entity(call).now();
                    sendCall(call);
                    continue;
                }
            }
        }

        List<CallServer> queryWait = OfyService.ofy().load().type(CallServer.class).filter("state", CallStateServer.WAIT).list();
        for (CallServer call : queryWait) {
            if (call.getReceiver().equals("test"))
                continue;
            Long init = System.currentTimeMillis();
            Long end = call.getStart().getTime();
            if ((init - end) > 60000 ) {
                call.setEnd(new Date(System.currentTimeMillis()));
                call.setUpdate(call.getEnd());
                call.setState(CallStateServer.END);
                OfyService.ofy().save().entity(call).now();
                sendCall(call);
                continue;
            }
        }
    }

    private void sendCall(CallServer call) throws IOException {
        Sender sender = new Sender(API_KEY);
        Message msg = new Message.Builder().addData("message",gson.toJson(call)).addData("type","call").addData("update","update").build();
        UserRegistrationServer toUser = findUser(Long.parseLong(call.getReceiver()));
        UserRegistrationServer fromUser = findUser(Long.parseLong(call.getSender()));
        Result resultTo = sender.send(msg, toUser.getRegId(), 5);
        Result resultFrom = sender.send(msg, fromUser.getRegId(), 5);
        //Enviar llamada al receptor
        if (resultTo.getMessageId() != null) {
            String canonicalRegId = resultTo.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                toUser.setRegId(canonicalRegId);
                OfyService.ofy().save().entity(toUser).now();
            }
        } else {
            String error = resultTo.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // if the device is no longer registered with Gcm, remove it from the datastore
                OfyService.ofy().delete().entity(toUser).now();
            }
        }
        //Enviar llamada al emisor
        if (resultFrom.getMessageId() != null) {
            String canonicalRegId = resultTo.getCanonicalRegistrationId();
            if (canonicalRegId != null) {
                // if the regId changed, we have to update the datastore
                toUser.setRegId(canonicalRegId);
                OfyService.ofy().save().entity(toUser).now();
            }
        } else {
            String error = resultTo.getErrorCodeName();
            if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
                // if the device is no longer registered with Gcm, remove it from the datastore
                OfyService.ofy().delete().entity(toUser).now();
            }
        }
    }

    private UserRegistrationServer findUser(Long userId) {
        return OfyService.ofy().load().type(UserRegistrationServer.class).filter("id", userId).first().now();
    }

}
