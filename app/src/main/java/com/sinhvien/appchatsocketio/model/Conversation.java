package com.sinhvien.appchatsocketio.model;

import android.widget.ArrayAdapter;
import android.widget.DatePicker;

import java.io.Serializable;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Conversation implements Serializable {
    private String roomId, name, lastMessage, lastTime;

    public Conversation() {
    }

    public Conversation(String roomId, String name, String message, String time) {
        this.roomId = roomId;
        this.name = name;
        this.lastMessage= message;
        this.lastTime = time;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return lastMessage;
    }

    public void setMessage(String message) {
        this.lastMessage = message;
    }

    public String getTime() {
        return lastTime;
    }

    public void setTime(String time) {
        this.lastTime = time;
    }
}
