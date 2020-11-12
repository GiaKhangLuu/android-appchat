package com.sinhvien.appchatsocketio.model;

import java.io.Serializable;

public class Room implements Serializable {
    private String idRoom, name, createDate;

    public Room() {
    }

    public Room(String idRoom, String name, String createDate) {
        this.idRoom = idRoom;
        this.name = name;
        this.createDate = createDate;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
