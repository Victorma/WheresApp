package com.wheresapp.server;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.wheresapp.R;
import com.wheresapp.bussiness.contacts.factory.ASContactsFactory;
import com.wheresapp.model.Call;
import com.wheresapp.model.CallState;
import com.wheresapp.model.Contact;
import com.wheresapp.model.Message;
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
import java.util.Date;
import java.util.List;

/**
 * Created by Sergio on 06/12/2014.
 */
public class ServerAPI {
    private static ServerAPI ourInstance;
    private Contact contact;
    private Context context;

    public static ServerAPI getInstance(Context context) {
        ourInstance = new ServerAPI(context);
        return ourInstance;
    }

    private ServerAPI(Context context) {
        this.context = context;
        this.contact = ASContactsFactory.getInstance().getInstanceASContacts(context).getUserRegistered();
    }

    private Contact getContact() {
        if (contact==null)
            contact = ASContactsFactory.getInstance().getInstanceASContacts(context).getUserRegistered();
        return contact;
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

    public List<Contact> getContactosRegistrados(List<Contact> listaContacto) throws IOException {
        ContactListServer contactList = new ContactListServer();
        ContactListServer contactListResult;
        List<ContactServer> contactClients = new ArrayList<ContactServer>();
        List<Contact> contactosRegistrados = new ArrayList<Contact>();
        for (Contact c : listaContacto) {
            contactClients.add(new ContactServer().setName(c.getName())
            .setPhone(c.getTelephone()));
        }
        contactList.setContactServerList(contactClients);
        contactListResult = getApiUserServiceHandle().contactList(getContact().getServerid(),contactList).execute();
        if (contactListResult.containsKey("contactServerList")) {
            for (ContactServer c : contactListResult.getContactServerList()) {
                Contact contact = new Contact();
                contact.setName(c.getName());
                contact.setTelephone(c.getPhone());
                contact.setServerid(c.getId().toString());
                contactosRegistrados.add(contact);
            }
        }
        return contactosRegistrados;
    }

    public Call crearLlamada(String toId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().createCall(getContact().getServerid(),toId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setServerId(llamadaCreadaServidor.getServerId());
        llamadaCreada.setReceiver(llamadaCreadaServidor.getReceiver());
        llamadaCreada.setSender(llamadaCreadaServidor.getSender());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new Date(llamadaCreadaServidor.getStart().getValue()));
        llamadaCreada.setUpdate(new Date(llamadaCreadaServidor.getStart().getValue()));
        return llamadaCreada;
    }

    public Call crearLlamadaPrueba() throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().testCall(getContact().getServerid()).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setServerId(llamadaCreadaServidor.getServerId());
        llamadaCreada.setReceiver(llamadaCreadaServidor.getReceiver());
        llamadaCreada.setSender(llamadaCreadaServidor.getSender());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new Date(llamadaCreadaServidor.getStart().getValue()));
        llamadaCreada.setUpdate(new Date(llamadaCreadaServidor.getStart().getValue()));
        return llamadaCreada;
    }

    public Call aceptarLlamada(String callId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().accept(getContact().getServerid(), callId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getReceiver());
        llamadaCreada.setSender(llamadaCreadaServidor.getSender());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new Date(llamadaCreadaServidor.getStart().getValue()));
        llamadaCreada.setUpdate(new Date(System.currentTimeMillis()));
        return llamadaCreada;
    }

    public Call rechazarLlamada(String callId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().deny(getContact().getServerid(), callId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getReceiver());
        llamadaCreada.setSender(llamadaCreadaServidor.getSender());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new Date(llamadaCreadaServidor.getStart().getValue()));
        llamadaCreada.setUpdate(new Date(llamadaCreadaServidor.getEnd().getValue()));
        llamadaCreada.setEnd(new Date(llamadaCreadaServidor.getEnd().getValue()));
        return llamadaCreada;
    }

    public Call finalizarLlamada(String callId) throws IOException {
        CallServer llamadaCreadaServidor = getCallUserServiceHandle().end(getContact().getServerid(), callId).execute();
        Call llamadaCreada = new Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getReceiver());
        llamadaCreada.setSender(llamadaCreadaServidor.getSender());
        llamadaCreada.setState(CallState.valueOf(llamadaCreadaServidor.getState()));
        llamadaCreada.setStart(new Date(llamadaCreadaServidor.getStart().getValue()));
        llamadaCreada.setUpdate(new Date(llamadaCreadaServidor.getEnd().getValue()));
        llamadaCreada.setEnd(new Date(llamadaCreadaServidor.getEnd().getValue()));
        return llamadaCreada;
    }

    public Message enviarPosicion(String callId, String position) throws  IOException {
        MessageServer messageServer = getCallUserServiceHandle().transmit(callId,getContact().getServerid(),position).execute();
        Message message = new Message();
        if (message.getMessage()=="")
            return null;
        message.setMessage(messageServer.getMessage());
        message.setToId(messageServer.getToId());
        message.setFromId(messageServer.getFromId());
        message.setCallId(messageServer.getCallId());
        message.setDate(new Date(messageServer.getDate().getValue()));
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
