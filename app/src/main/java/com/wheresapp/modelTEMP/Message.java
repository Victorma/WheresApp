package com.wheresapp.modelTEMP;

import com.google.api.client.util.DateTime;

import java.util.Date;

/**
 * Created by Sergio on 07/12/2014.
 */
public class Message {
    private String id;
    private String callId;
    private String fromId;
    private String toId;
    private Date dateSend;
    private String message;

    public Message() {
        this.dateSend = new Date(System.currentTimeMillis());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public Date getDate() {
        return dateSend;
    }

    public void setDate(Date date) {
        this.dateSend = date;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }
}
