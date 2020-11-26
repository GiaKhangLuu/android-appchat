package com.sinhvien.appchatsocketio.model;

import java.io.Serializable;

public class Message implements Serializable {
    private String senderId, displayName, message, time;

    public Message() {
    }

    public Message(String senderId, String displayName, String message, String time) {
        this.senderId = senderId;
        this.displayName = displayName;
        this.message = message;
        this.time = time;
    }

    public Message(String displayName, String message, String time) {
        this.displayName = displayName;
        this.message = message;
        this.time = time;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
