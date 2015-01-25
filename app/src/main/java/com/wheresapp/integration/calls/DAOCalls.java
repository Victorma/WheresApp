package com.wheresapp.integration.calls;

import com.wheresapp.integration.DAO;
import com.wheresapp.model.Call;

/**
 * Created by Victorma on 25/11/2014.
 */
public interface DAOCalls extends DAO<Call> {
    public static String filterChange = "com.wheresapp.call.change";

}
