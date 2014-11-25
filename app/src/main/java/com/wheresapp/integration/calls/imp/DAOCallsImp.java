package com.wheresapp.integration.calls.imp;

import com.wheresapp.integration.calls.DAOCalls;
import com.wheresapp.modelTEMP.Call;

import java.util.List;

/**
 * Created by Victorma on 25/11/2014.
 */
public class DAOCallsImp implements DAOCalls {

    @Override
    public boolean create(Call call) {
        return false;
    }

    @Override
    public Call read(Call call) {
        return null;
    }

    @Override
    public boolean update(Call call) {
        return false;
    }

    @Override
    public boolean delete(Call call) {
        return false;
    }

    @Override
    public List<Call> discover(Call call) {
        return null;
    }
}
