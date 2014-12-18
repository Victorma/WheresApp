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
    private String id;
    @Index
    private String to;
    @Index
    private String from;
    @Index
    private CallStateServer state;
    private DateTime dateStart;
    private DateTime dateEnd;

    public CallServer() {

    }

    public void init() {
        state = CallStateServer.RECEIVE;
        dateStart = new DateTime(System.currentTimeMillis());
        dateEnd = new DateTime(0);
        to = "";
        from = "";
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

    public Map<String,String> toMap() {
        Map<String, String> mapa = new TreeMap<String, String>();
        mapa.put("to",to);
        mapa.put("from",from);
        mapa.put("dateStart",dateStart.toString());
        mapa.put("dateEnd",dateEnd.toString());
        mapa.put("state",state.toString());
        mapa.put("serverId",id);

        return mapa;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
