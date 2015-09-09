package tk.wheresoft.wheresapp.bussiness.routes.factory;

import android.content.Context;

import tk.wheresoft.wheresapp.bussiness.routes.ASRoutes;
import tk.wheresoft.wheresapp.bussiness.routes.factory.imp.ASRoutesFactoryImp;

/**
 * Created by Victorma on 26/11/2014.
 */
public abstract class ASRoutesFactory {

    private static ASRoutesFactory instance;

    public static ASRoutesFactory getInstance() {
        if (instance == null)
            instance = new ASRoutesFactoryImp();
        return instance;
    }

    public abstract ASRoutes getInstanceASRoutes(Context context);
}
