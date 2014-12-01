package com.wheresapp.domain;

/**
 * Created by Sergio on 19/11/2014.
 */
public class UserRegister {

    private String gcmId;
    private String phoneNumber;
    private String userName;

    public UserRegister() {
        super();
    }

    public UserRegister(String gcmId, String phoneNumber, String userName) {
        this.gcmId = gcmId;
        this.phoneNumber = phoneNumber;
        this.userName = userName;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRegister that = (UserRegister) o;

        if (!gcmId.equals(that.gcmId)) return false;
        if (!phoneNumber.equals(that.phoneNumber)) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = gcmId.hashCode();
        result = 31 * result + phoneNumber.hashCode();
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        return result;
    }
}
