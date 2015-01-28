package tk.wheresoft.wheresapp.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;

/**
 * Created by Victorma on 25/11/2014.
 */
@Table(name = "Calls")
public class Call extends Model {

    @Column(name = "ServerId")
    private String serverId;

    @Column(name = "Start")
    private Date start;

    @Column(name = "End")
    private Date end;

    @Column(name = "UpdateDate")
    private Date update;

    @Column(name = "SenderDate")
    private String sender;

    @Column(name = "ReceiverDate")
    private String receiver;

    @Column(name = "State")
    private CallState state;

    @Column(name = "Incoming")
    private boolean incoming = false;

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

    public Date getUpdate() {
        return update;
    }

    public void setUpdate(Date update) {
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

    public boolean isIncoming() {
        return incoming;
    }

    public void setIncoming(boolean incoming) {
        this.incoming = incoming;
    }

    @Override
    public String toString() {
        return "Call{" +
                "serverId='" + serverId + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", update=" + update +
                ", sender='" + sender + '\'' +
                ", receiver='" + receiver + '\'' +
                ", state=" + state +
                ", incoming=" + incoming +
                '}';
    }
}
