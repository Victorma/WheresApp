package com.wheresapp.server.domain;

import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Id;

import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Sergio on 19/11/2014.
 */
@Entity
public class CallServer {
    @Id
    private long id;
    @Index
    private String to;
    @Index
    private String from;
    @Index
    private CallStateServer state;
    private String position;
    private DateTime dateStart;
    private DateTime dateEnd;
    private DateTime dateUpdate;

    public CallServer() {

    }

    public CallServer(Map<String,String> mapa) {
        to = mapa.get("to");
        from = mapa.get("from");
        dateStart = new DateTime(mapa.get("dateStart"));
        dateEnd = new DateTime(mapa.get("dateEnd"));
        state = CallStateServer.valueOf(mapa.get("state"));
        position = mapa.get("position");
        dateUpdate = new DateTime(mapa.get("dateUpdate"));
    }

    public void init() {
        state = CallStateServer.RECEIVE;
        dateStart = new DateTime(System.currentTimeMillis());
        dateEnd = new DateTime(0);
        to = "";
        from = "";
        position = "";
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public CallStateServer getState() {
        return state;
    }

    public void setState(CallStateServer state) {
        this.state = state;
        new DateTime(System.currentTimeMillis());
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
        new DateTime(System.currentTimeMillis());
    }

    public DateTime getDateStart() {
        return dateStart;
    }

    public void setDateStart(DateTime dateStart) {
        this.dateStart = dateStart;
    }

    public DateTime getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(DateTime dateEnd) {
        this.dateEnd = dateEnd;
        new DateTime(System.currentTimeMillis());
    }

    public DateTime getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(DateTime dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public Map<String,String> toMap() {
        Map<String, String> mapa = new TreeMap<String, String>();
        mapa.put("to",to);
        mapa.put("from",from);
        mapa.put("dateStart",dateStart.toString());
        mapa.put("dateEnd",dateEnd.toString());
        mapa.put("dateUpdate",dateUpdate.toString());
        mapa.put("state",state.toString());
        mapa.put("position",position);

        return mapa;
    }

}
