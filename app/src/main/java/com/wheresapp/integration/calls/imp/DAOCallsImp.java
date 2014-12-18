package com.wheresapp.integration.calls.imp;

import android.content.Context;

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
        return call.save() != 0;
    }

    @Override
    public Call read(Call call) {
        //Por ahora no lo veo necesario
        return null;
    }

    @Override
    public boolean update(Call call) {
        return call.save() != 0;
    }

    @Override
    public boolean delete(Call call) {
        call.delete();
        return true;
    }

    @Override
    public List<Call> discover(Call call, int limit, int page) {

        From f = new Select().from(Call.class);

        if(call.getReceiver()!=null)
            f.where("Receiver LIKE '"+call.getReceiver()+"'");

        if(call.getState()!=null)
            f.where("State = "+call.getState()+"");

        if(call.getStart()!=null)
            f.where("Start LIKE '"+call.getStart().toString()+"'");

        if(call.getEnd()!=null)
            f.where("End LIKE '"+call.getEnd().toString()+"'");

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
}
