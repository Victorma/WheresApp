package com.wheresapp.server.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergio on 27/01/2015.
 */
public class ContactNotFound {
    private Long id;
    private String phone;
    private List<String> contactKnows;

    public ContactNotFound(){
        this.contactKnows = new ArrayList<String>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getContactKnows() {
        return contactKnows;
    }

    public void setContactKnows(List<String> contactKnows) {
        this.contactKnows = contactKnows;
    }
}
