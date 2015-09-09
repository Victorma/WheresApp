package tk.wheresoft.wheresapp.bussiness.calls.factory;

import android.content.Context;

import tk.wheresoft.wheresapp.bussiness.calls.ASCalls;
import tk.wheresoft.wheresapp.bussiness.calls.factory.imp.ASCallsFactoryImp;

/**
 * Created by Victorma on 26/11/2014.
 */
public abstract class ASCallsFactory {
    private static ASCallsFactory instance;

    public static ASCallsFactory getInstance() {
        if (instance == null)
            instance = new ASCallsFactoryImp();
        return instance;
    }

    public abstract ASCalls getInstanceASCalls(Context context);
}
