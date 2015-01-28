package tk.wheresoft.wheresapp.bussiness.calls.factory.imp;

import android.content.Context;

import tk.wheresoft.wheresapp.bussiness.calls.ASCalls;
import tk.wheresoft.wheresapp.bussiness.calls.factory.ASCallsFactory;
import tk.wheresoft.wheresapp.bussiness.calls.imp.ASCallsImp;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASCallsFactoryImp extends ASCallsFactory {
    @Override
    public ASCalls getInstanceASCalls(Context context) {
        return new ASCallsImp(context);
    }
}
