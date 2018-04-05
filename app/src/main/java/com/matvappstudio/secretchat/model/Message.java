package com.matvappstudio.secretchat.model;

/**
 * Created by Alexandr.
 */
public class Message {

    private String message;
    private User sender;
    private long createdAt;
    private boolean info;

    public Message(String message, User sender, long createdAt, boolean info) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
        this.info = info;
    }

    public boolean isInfo() {
        return info;
    }

    public void setInfo(boolean info) {
        this.info = info;
    }
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
