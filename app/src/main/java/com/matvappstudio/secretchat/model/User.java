package com.matvappstudio.secretchat.model;

/**
 * Created by Alexandr.
 */
public class User {
    String nickname;

    public User(String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
