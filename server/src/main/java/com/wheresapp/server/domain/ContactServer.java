package com.wheresapp.server.domain;

import com.google.appengine.repackaged.com.google.api.client.util.DateTime;

public class ContactServer {
    private Long id;
    private String name;
    private String phone;
    private DateTime last;
    private boolean favorite;
    private String gcmId;

    public ContactServer() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DateTime getLast() {
        return last;
    }

    public void setLast(DateTime last) {
        this.last = last;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
