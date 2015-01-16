package com.wheresapp.bussiness.calls.imp;

import android.content.Context;
import android.content.Intent;

import com.wheresapp.ActivityIncomingCall;
import com.wheresapp.bussiness.calls.ASCalls;
import com.wheresapp.integration.calls.DAOCalls;
import com.wheresapp.integration.calls.factory.DAOCallsFactory;
import com.wheresapp.integration.contacts.factory.DAOContactsFactory;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.CallState;
import com.wheresapp.modelTEMP.Contact;
import com.wheresapp.server.ServerAPI;

import java.io.IOException;
import java.util.List;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASCallsImp implements ASCalls {

    private Context context;
    private DAOCalls dao;

    public ASCallsImp(Context context) {
        dao = DAOCallsFactory.getInstance().getInstanceDAOCalls(context);
        this.context = context;
    }

    @Override
    public boolean call(Contact contact) throws IOException {
        if (getActiveCall()==null) {
            Call call = ServerAPI.getInstance(context).crearLlamada(contact.getServerid());
            if (call!=null) {
                dao.create(call);
                return true;
            }
            else
                return false;
        }
        return false;
    }

    @Override
    public boolean accept(Call call) throws IOException {
        if (getActiveCall()!=null) {
            Call callAccept = ServerAPI.getInstance(context).aceptarLlamada(call.getServerId());
            if (callAccept!=null) {
                Call temp = getActiveCall();
                temp.setState(callAccept.getState());
                temp.setUpdate(callAccept.getUpdate());
                dao.update(temp);
                return true;
            }
            else
                return false;
        }
        return false;
    }

    @Override
    public boolean reject(Call call) throws IOException {
        if (getActiveCall()!=null) {
            Call callDeny = ServerAPI.getInstance(context).rechazarLlamada(call.getServerId());
            if (callDeny!=null) {
                Call temp = getActiveCall();
                temp.setState(callDeny.getState());
                temp.setEnd(callDeny.getEnd());
                temp.setUpdate(callDeny.getUpdate());
                dao.update(temp);
                return true;
            }
            else
                return false;
        }
        return false;
    }

    @Override
    public boolean end(Call call) throws IOException {
        if (getActiveCall()!=null) {
            Call callEnd;
            if (call.getState().equals(CallState.WAIT))
                callEnd = ServerAPI.getInstance(context).rechazarLlamada(call.getServerId());
            else
                callEnd = ServerAPI.getInstance(context).finalizarLlamada(call.getServerId());
            if (callEnd!=null) {
                Call temp = getActiveCall();
                temp.setState(callEnd.getState());
                temp.setEnd(callEnd.getEnd());
                temp.setUpdate(callEnd.getUpdate());
                dao.update(temp);
                return true;
            }
            else
                return false;
        }
        return false;
    }

    @Override
    public boolean receiveCall(Call call) {
        if (getActiveCall() == null) {
            dao.create(call);
            return true;
        }
        return false;
    }

    @Override
    public Call getActiveCall() {
        Call call = null;
        Call filter = new Call();
        List<Call> listCall;
        filter.setState(CallState.ACCEPT);
        listCall = dao.discover(filter);
        call = listCall.size()>0?listCall.get(0):null;
        if (call!= null)
            return call;
        filter.setState(CallState.WAIT);
        listCall = dao.discover(filter);
        call = listCall.size()>0?listCall.get(0):null;
        return call;
    }
}
