package com.wheresapp.bussiness.calls;

import com.wheresapp.modelTEMP.Call;
import com.wheresapp.modelTEMP.Contact;

import java.io.IOException;

/**
 * Created by Victorma on 26/11/2014.
 */
public interface ASCalls {

    public boolean call(Contact contact) throws IOException;
    public boolean accept(Call call) throws IOException;
    public boolean reject(Call call) throws IOException;
    public boolean end(Call call) throws IOException;
    public boolean receiveCall(Call call);
    public Call getActiveCall();

}
