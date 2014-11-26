package com.wheresapp.bussiness.calls.imp;

import com.wheresapp.bussiness.calls.ASCalls;
import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASCallsImp implements ASCalls {

    @Override
    public boolean call(Contact contact) {
        return false;
    }

    @Override
    public boolean accept(Call call) {
        return false;
    }

    @Override
    public boolean reject(Call call) {
        return false;
    }

    @Override
    public boolean end(Call call) {
        return false;
    }

    @Override
    public boolean receiveCall(Call call) {
        return false;
    }

    @Override
    public Call getActiveCall() {
        return null;
    }
}
