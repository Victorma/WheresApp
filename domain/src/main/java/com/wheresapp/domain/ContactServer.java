package com.wheresapp.domain;

/**
 * Created by Sergio on 19/11/2014.
 */
public class ContactServer extends ContactClient {
    private String gcmId;

    public ContactServer() {
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }
}
