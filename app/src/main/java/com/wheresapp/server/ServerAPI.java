package com.wheresapp.server;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.DateTime;
import com.wheresapp.R;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.CallState;
import com.wheresapp.modelTEMP.Contact;
import com.wheresapp.modelTEMP.Message;
import com.wheresapp.server.callApi.CallApi;
import com.wheresapp.server.callApi.model.CallServer;
import com.wheresapp.server.callApi.model.MessageServer;
import com.wheresapp.server.registrationApi.RegistrationApi;
import com.wheresapp.server.registrationApi.model.UserRegistrationServer;
import com.wheresapp.server.userApi.UserApi;
import com.wheresapp.server.userApi.model.ContactListServer;
import com.wheresapp.server.userApi.model.ContactServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 06/12/2014.
 */
public class ServerAPI {
    private static ServerAPI ourInstance = new ServerAPI();

    public static ServerAPI getInstance() {
        return ourInstance;
    }

    private ServerAPI() {
    }

    /**
     * Class instance of the JSON factory.
     */
    public static final JsonFactory JSON_FACTORY = new AndroidJsonFactory();

    /**
     * Class instance of the HTTP transport.
     */
    public static final HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();

    public Contact registrarUsuario(String telefono, String gcmId) throws IOException {
        Contact contact = new Contact();
        UserRegistrationServer user = new UserRegistrationServer().setPhone(telefono).setRegId(gcmId);
        user = getApiRegistrationServiceHandle().register(user).execute();
        contact.setGcmId(user.getRegId());
        contact.setTelephone(user.getPhone());
        contact.setServerid(user.getId().toString());
        return contact;
    }

    public List<Contact> getContactosRegistrados(String fromId, List<Contact> listaContacto) throws IOException {
        ContactListServer contactList = new ContactListServer();
        ContactListServer contactListResult;
        List<ContactServer> contactClients = new ArrayList<ContactServer>();
        List<Contact> contactosRegistrados = new ArrayList<Contact>();
        for (Contact c : listaContacto) {
            contactClients.add(new ContactServer().setName(c.getName())
            .setPhone(c.getTelephone()));
        }
        contactList.setContactServerList(contactClients);
        contactListResult = getApiUserServiceHandle().contactList(fromId,contactList).execute();
        for (ContactServer c : contactListResult.getContactServerList()) {
            Contact contact = new Contact();
            contact.setName(c.getName());
            contact.setTelephone(c.getPhone());
            contact.setServerid(c.getId().toString());
            contactosRegistrados.add(contact);
        }
        return contactosRegistrados;
    }

    public Call crearLlamada(String fromId, String toId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().createCall(fromId,toId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getTo());
        llamadaCreada.setSender(llamadaCreadaServidor.getFrom());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new DateTime(llamadaCreadaServidor.getDateStart().getValue()));
        llamadaCreada.setUpdate(new DateTime(llamadaCreadaServidor.getDateStart().getValue()));
        return llamadaCreada;
    }

    public Call aceptarLlamada(String fromId, String callId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().accept(fromId, callId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getTo());
        llamadaCreada.setSender(llamadaCreadaServidor.getFrom());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new DateTime(llamadaCreadaServidor.getDateStart().getValue()));
        llamadaCreada.setUpdate(new DateTime(System.currentTimeMillis()));
        return llamadaCreada;
    }

    public Call rechazarLlamada(String fromId, String callId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().deny(fromId, callId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getTo());
        llamadaCreada.setSender(llamadaCreadaServidor.getFrom());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new DateTime(llamadaCreadaServidor.getDateStart().getValue()));
        llamadaCreada.setUpdate(new DateTime(llamadaCreadaServidor.getDateEnd().getValue()));
        llamadaCreada.setEnd(new DateTime(llamadaCreadaServidor.getDateEnd().getValue()));
        return llamadaCreada;
    }

    public Call finalizarLlamada(String fromId, String callId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().end(fromId, callId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getTo());
        llamadaCreada.setSender(llamadaCreadaServidor.getFrom());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new DateTime(llamadaCreadaServidor.getDateStart().getValue()));
        llamadaCreada.setUpdate(new DateTime(llamadaCreadaServidor.getDateEnd().getValue()));
        llamadaCreada.setEnd(new DateTime(llamadaCreadaServidor.getDateEnd().getValue()));
        return llamadaCreada;
    }

    public Message enviarPosicion(String fromId, String callId, String position) throws  IOException {
        MessageServer messageServer = getCallUserServiceHandle().transmit(fromId,callId,position).execute();
        Message message = new Message();
        if (message.getMessage()=="")
            return null;
        message.setMessage(messageServer.getMessage());
        message.setToId(messageServer.getToId());
        message.setFromId(messageServer.getFromId());
        message.setCallId(messageServer.getCallId());
        message.setDate(new DateTime(messageServer.getDate().getValue()));
        return  message;
    }

    /**
     * Retrieve a Registration api service handle to access the API.
     */
    private static RegistrationApi getApiRegistrationServiceHandle() {
        // Use a builder to help formulate the API request.
        RegistrationApi.Builder registrationEndpoint = new RegistrationApi.Builder(HTTP_TRANSPORT, JSON_FACTORY,null);

        //registrationEndpoint.setRootUrl("http://localhost:8080/_ah/api");

        return registrationEndpoint.build();
    }

    private static UserApi getApiUserServiceHandle() {
        // Use a builder to help formulate the API request.
        UserApi.Builder userEndpoint = new UserApi.Builder(HTTP_TRANSPORT, JSON_FACTORY,null);

        //userEndpoint.setRootUrl("http://localhost:8080/_ah/api");

        return userEndpoint.build();
    }

    private static CallApi getCallUserServiceHandle() {
        // Use a builder to help formulate the API request.
        CallApi.Builder callEndpoint = new CallApi.Builder(HTTP_TRANSPORT, JSON_FACTORY,null);

        //userEndpoint.setRootUrl("http://localhost:8080/_ah/api");

        return callEndpoint.build();
    }

    private static Account getAccount(Context context){
        Account[] accounts = AccountManager.get(context).getAccountsByType(context.getString(R.string.ACCOUNT_TYPE));
        if (accounts!=null)
            return accounts[0];
        else
            return null;
    }
}
