package com.matvappstudio.secretchat.model;

import com.matvappstudio.secretchat.server.ConnectThread;

import java.net.Socket;

/**
 * Created by Alexandr.
 */
public class ChatClient {
    private String name;
    private Socket socket;
    private ConnectThread chatThread;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public ConnectThread getChatThread() {
        return chatThread;
    }

    public void setChatThread(ConnectThread chatThread) {
        this.chatThread = chatThread;
    }
}
