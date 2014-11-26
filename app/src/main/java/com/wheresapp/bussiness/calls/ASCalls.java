package com.wheresapp.bussiness.calls;

import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;

/**
 * Created by Victorma on 26/11/2014.
 */
public interface ASCalls {

    public boolean call(Contact contact);
    public boolean accept(Call call);
    public boolean reject(Call call);
    public boolean end(Call call);
    public boolean receiveCall(Call call);
    public Call getActiveCall();

}
