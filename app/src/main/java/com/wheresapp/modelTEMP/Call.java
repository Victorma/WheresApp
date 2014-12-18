package com.wheresapp.modelTEMP;

import android.os.Bundle;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.api.client.util.DateTime;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Victorma on 25/11/2014.
 */
@Table(name = "Calls")
public class Call extends Model{

    @Column(name = "ServerId")
    private String serverId;

    @Column(name = "Start")
    private DateTime start;

    @Column(name = "End")
    private DateTime end;

    @Column(name = "Update")
    private DateTime update;

    @Column(name = "Sender")
    private String sender;

    @Column(name = "Receiver")
    private String receiver;

    @Column(name = "State")
    private CallState state;

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime start) {
        this.start = start;
    }

    public DateTime getEnd() {
        return end;
    }

    public void setEnd(DateTime end) {
        this.end = end;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public CallState getState() {
        return state;
    }

    public void setState(CallState state) {
        this.state = state;
    }

    public DateTime getUpdate() {
        return update;
    }

    public void setUpdate(DateTime update) {
        this.update = update;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public static Call fromMap(Bundle map) {
        Call call = new Call();
        call.setReceiver(map.getString("to"));
        call.setSender(map.getString("from"));
        //TODO
        //call.setEnd(map.get("dateEnd"));
        //TODO
        //call.setStart(map.get("dateStart"));
        call.setState(CallState.valueOf(map.getString("state")));
        call.setServerId(map.getString("serverId"));

        return call;
    }
}
