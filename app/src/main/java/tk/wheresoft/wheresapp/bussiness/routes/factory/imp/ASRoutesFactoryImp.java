package tk.wheresoft.wheresapp.bussiness.routes.factory.imp;

import android.content.Context;

import tk.wheresoft.wheresapp.bussiness.routes.ASRoutes;
import tk.wheresoft.wheresapp.bussiness.routes.factory.ASRoutesFactory;
import tk.wheresoft.wheresapp.bussiness.routes.imp.ASRoutesImp;

/**
 * Created by Victorma on 26/11/2014.
 */
public class ASRoutesFactoryImp extends ASRoutesFactory {
    @Override
    public ASRoutes getInstanceASRoutes(Context context) {
        return new ASRoutesImp(context);
    }
}
