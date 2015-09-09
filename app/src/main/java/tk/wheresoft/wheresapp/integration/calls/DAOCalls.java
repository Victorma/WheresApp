package tk.wheresoft.wheresapp.integration.calls;

import tk.wheresoft.wheresapp.integration.DAO;
import tk.wheresoft.wheresapp.model.Call;

/**
 * Created by Victorma on 25/11/2014.
 */
public interface DAOCalls extends DAO<Call> {
    public static String filterChange = "tk.wheresoft.wheresapp.call.change";

}
