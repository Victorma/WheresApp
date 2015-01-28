package tk.wheresoft.wheresapp.server.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;

/**
 * Created by Sergio on 19/11/2014.
 */
@Entity
public class CallServer {
    @Id
    Long serverId;
    @Index
    private String receiver;
    @Index
    private String sender;
    @Index
    private CallStateServer state;
    private Date start;
    private Date end;
    private Date update;

    public CallServer() {
        init();
    }

    public void init() {
        state = CallStateServer.WAIT;
        start = new Date(System.currentTimeMillis());
        receiver = "";
        sender = "";
    }

    public String getServerId() {
        return serverId.toString();
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public CallStateServer getState() {
        return state;
    }

    public void setState(CallStateServer state) {
        this.state = state;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
        this.update = update;
    }

    @Override
    public String toString() {
        return "CallServer{" +
                "serverId='" + serverId + '\'' +
                ", receiver='" + receiver + '\'' +
                ", sender='" + sender + '\'' +
                ", state=" + state +
                ", start=" + start +
                ", end=" + end +
                ", update=" + update +
                '}';
    }
}
