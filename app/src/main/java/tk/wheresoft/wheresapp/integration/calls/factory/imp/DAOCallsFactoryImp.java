package tk.wheresoft.wheresapp.integration.calls.factory.imp;

import android.content.Context;

import tk.wheresoft.wheresapp.integration.calls.DAOCalls;
import tk.wheresoft.wheresapp.integration.calls.factory.DAOCallsFactory;
import tk.wheresoft.wheresapp.integration.calls.imp.DAOCallsImp;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOCallsFactoryImp extends DAOCallsFactory {

    @Override
    public DAOCalls getInstanceDAOCalls(Context context) {
        return new DAOCallsImp(context);
    }
}
