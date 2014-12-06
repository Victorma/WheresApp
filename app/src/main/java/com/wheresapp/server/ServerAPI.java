package com.wheresapp.server;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.wheresapp.R;
import com.wheresapp.modelTEMP.Contact;
import com.wheresapp.server.call.Call;
import com.wheresapp.server.registration.Registration;
import com.wheresapp.server.registration.model.UserRegistration;
import com.wheresapp.server.user.User;
import com.wheresapp.server.user.model.ContactClient;
import com.wheresapp.server.user.model.ContactList;

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
        UserRegistration user = new UserRegistration().setPhone(telefono).setRegId(gcmId);
        user = getApiRegistrationServiceHandle().register(user).execute();;
        contact.setGcmId(user.getRegId());
        contact.setTelephone(user.getPhone());
        contact.setServerid(user.getId().toString());
        contact.setName(user.getName());
        return contact;
    }

    public List<Contact> getContactosRegistrados(List<Contact> listaContacto) throws IOException {
        ContactList contactList = new ContactList();
        ContactList contactListResult;
        List<ContactClient> contactClients = new ArrayList<ContactClient>();
        List<Contact> contactosRegistrados = new ArrayList<Contact>();
        for (Contact c : listaContacto) {
            contactClients.add(new ContactClient().setName(c.getName())
            .setPhone(c.getTelephone()));
        }
        contactList.setContactClientList(contactClients);
        contactListResult = getApiUserServiceHandle().contactList("",contactList).execute();
        for (ContactClient c : contactListResult.getContactClientList()) {
            Contact contact = new Contact();
            contact.setName(c.getName());
            contact.setTelephone(c.getPhone());
            contact.setServerid(c.getId().toString());
            contactosRegistrados.add(contact);
        }
        return contactosRegistrados;
    }

    public com.wheresapp.modelTEMP.Call crearLlamada(com.wheresapp.modelTEMP.Call call) throws IOException {
        com.wheresapp.server.call.model.Call llamadaCreadaServidor = getCallUserServiceHandle().createCall(call.getSender(),call.getReceiver()).execute();
        com.wheresapp.modelTEMP.Call llamadaCreada = new com.wheresapp.modelTEMP.Call();
        llamadaCreada.setReceiver(llamadaCreadaServidor.getTo());
        llamadaCreada.setSender(llamadaCreadaServidor.getFrom());
        llamadaCreadaServidor.setDateStart(llamadaCreadaServidor.getDateStart());
        llamadaCreadaServidor.setDateUpdate(llamadaCreadaServidor.getDateUpdate());
        return llamadaCreada;
    }

    /**
     * Retrieve a Registration api service handle to access the API.
     */
    private static Registration getApiRegistrationServiceHandle() {
        // Use a builder to help formulate the API request.
        Registration.Builder registrationEndpoint = new Registration.Builder(HTTP_TRANSPORT, JSON_FACTORY,null);

        //registrationEndpoint.setRootUrl("http://localhost:8080/_ah/api");

        return registrationEndpoint.build();
    }

    private static User getApiUserServiceHandle() {
        // Use a builder to help formulate the API request.
        User.Builder userEndpoint = new User.Builder(HTTP_TRANSPORT, JSON_FACTORY,null);

        //userEndpoint.setRootUrl("http://localhost:8080/_ah/api");

        return userEndpoint.build();
    }

    private static Call getCallUserServiceHandle() {
        // Use a builder to help formulate the API request.
        Call.Builder callEndpoint = new Call.Builder(HTTP_TRANSPORT, JSON_FACTORY,null);

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
