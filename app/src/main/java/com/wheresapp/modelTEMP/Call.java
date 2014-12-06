package com.wheresapp.modelTEMP;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.api.client.util.DateTime;

/**
 * Created by Victorma on 25/11/2014.
 */
@Table(name = "Calls")
public class Call extends Model{

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

    @Column(name = "Position")
    private DateTime position;

    @Column(name = "State")
    private Integer state;

    public DateTime getStart() {
        return start;
    }

    public void setStart(DateTime lastSeen) {
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

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
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

    public DateTime getPosition() {
        return position;
    }

    public void setPosition(DateTime position) {
        this.position = position;
    }
}
