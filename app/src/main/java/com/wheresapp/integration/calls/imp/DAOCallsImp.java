package com.wheresapp.integration.calls.imp;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.activeandroid.query.Delete;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;
import com.wheresapp.integration.calls.DAOCalls;
import com.wheresapp.modelTEMP.Call;

import java.util.List;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOCallsImp implements DAOCalls {

    private Context context;

    public DAOCallsImp(Context context) {
        this.context = context;
    }

    @Override
    public boolean create(Call call) {
        Boolean result = call.save() != 0;
        touchObserver();
        return result;
    }

    @Override
    public Call read(Call call) {
        From f = new Select().from(Call.class);
        f.where("ServerId LIKE '"+call.getServerId()+"'");
        return f.executeSingle();
    }

    @Override
    public boolean update(Call call) {
        Boolean result = call.save() != 0;
        touchObserver();
        return result;
    }

    @Override
    public boolean delete(Call call) {
        call.delete();
        touchObserver();
        return true;
    }

    @Override
    public boolean deleteAll() {
        Boolean result = new Delete().from(Call.class).execute()!=null;
        touchObserver();
        return result;
    }

    @Override
    public List<Call> discover(Call call, int limit, int page) {

        From f = new Select().from(Call.class);

        if(call.getReceiver()!=null)
            f.where("Receiver LIKE '"+call.getReceiver()+"'");

        if(call.getState()!=null)
            f.where("State LIKE '"+call.getState().toString()+"'");

        if(call.getStart()!=null)
            f.where("StartDate LIKE '"+call.getStart().toString()+"'");

        if(call.getEnd()!=null)
            f.where("EndDate LIKE '"+call.getEnd().toString()+"'");

        //f.orderBy("End DESC");
        if(limit>0) {
            f.limit(limit);
            if (page >= 0)
                f.offset(limit * page);
        }

        return f.execute();
    }

    @Override
    public List<Call> discover(Call call) {
        return discover(call,-1,-1);
    }

    private void touchObserver() {
        LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(this.filterChange));
    }
}
