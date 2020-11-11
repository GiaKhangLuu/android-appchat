package com.sinhvien.appchatsocketio.model;

import java.io.Serializable;

public class User implements Serializable {
    private String idUser;
    private String displayName;

    public User() {}

    public User(String idUser, String displayName) {
        this.idUser = idUser;
        this.displayName = displayName;
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
