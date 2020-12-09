package com.sinhvien.appchatsocketio.model;

import java.io.Serializable;

public class User implements Serializable {
    private String idUser;
    private String displayName;
    private String accountName;
    private String password;
    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public User(String idUser, String displayName, String accountName, String password, String phoneNumber) {
        this.idUser = idUser;
        this.displayName = displayName;
        this.accountName = accountName;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    public User() {}

    public User(String idUser, String displayName) {
        this.idUser = idUser;
        this.displayName = displayName;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
