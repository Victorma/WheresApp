package tk.wheresoft.wheresapp.model;


import com.google.api.client.util.DateTime;

import java.io.Serializable;

/**
 * Created by Victorma on 25/11/2014.
 */
public class Contact implements Serializable {

    private Long raw_Id;

    private String serverid;

    private String telephone;

    private String nickname;

    private String name;

    private Integer state;

    private DateTime lastSeen;

    private Boolean favourite;

    private String imageURI;

    private String gcmId;

    public Contact() {
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public DateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(DateTime lastSeen) {
        this.lastSeen = lastSeen;
    }

    public Boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(Boolean favourite) {
        this.favourite = favourite;
    }

    public String getServerid() {
        return serverid;
    }

    public void setServerid(String serverid) {
        this.serverid = serverid;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getImageURI() {
        return imageURI;
    }


    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public Long getRaw_Id() {
        return raw_Id;
    }

    public void setRaw_Id(Long raw_Id) {
        this.raw_Id = raw_Id;
    }

    public String toString() {
        return this.name;
    }

    public Integer getFavouriteNum() {
        return favourite ? 1 : 0;
    }
}
