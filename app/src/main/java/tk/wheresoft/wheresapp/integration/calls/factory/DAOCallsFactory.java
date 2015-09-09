package tk.wheresoft.wheresapp.integration.calls.factory;

import android.content.Context;

import tk.wheresoft.wheresapp.integration.calls.DAOCalls;
import tk.wheresoft.wheresapp.integration.calls.factory.imp.DAOCallsFactoryImp;

/**
 * Created by Victorma on 25/11/2014.
 */
public abstract class DAOCallsFactory {

    private static DAOCallsFactory instance;

    public static DAOCallsFactory getInstance() {
        if (instance == null)
            instance = new DAOCallsFactoryImp();

        return instance;
    }

    public abstract DAOCalls getInstanceDAOCalls(Context context);

}
