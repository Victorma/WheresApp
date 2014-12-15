package com.wheresapp.modelTEMP;


import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.api.client.util.DateTime;

import java.io.Serializable;

/**
 * Created by Victorma on 25/11/2014.
 */
@Table(name = "Contacts")
public class Contact extends Model implements Serializable{

    @Column(name = "ServerId")
    private String serverid;

    @Column(name = "Telephone")
    private String telephone;

    @Column(name = "Nickname")
    private String nickname;

    @Column(name = "Name")
    private String name;

    @Column(name = "State")
    private Integer state;

    @Column(name = "LastSeen")
    private DateTime lastSeen;

    @Column(name = "Favourite")
    private Boolean favourite = false;

    @Column(name = "Image")
    private String imageURI;

    @Column(name = "GcmId")
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

    public String toString() {
        return this.name;
    }
}
