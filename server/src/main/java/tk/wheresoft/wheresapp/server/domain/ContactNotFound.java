package tk.wheresoft.wheresapp.server.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Sergio on 27/01/2015.
 */
@Entity
public class ContactNotFound {
    @Id
    private Long id;
    @Index
    private String phone;
    private Set<String> contactKnows;

    public ContactNotFound(){
        this.contactKnows = new TreeSet<>();
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

    public Set<String> getContactKnows() {
        return contactKnows;
    }

    public void setContactKnows(Set<String> contactKnows) {
        this.contactKnows = contactKnows;
    }
}
