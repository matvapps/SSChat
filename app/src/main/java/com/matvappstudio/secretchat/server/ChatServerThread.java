package com.matvappstudio.secretchat.server;

import com.matvappstudio.secretchat.model.ChatClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandr
 */

public class ChatServerThread extends Thread {

    public static final String TAG = ChatServerThread.class.getSimpleName();

    private int socketServerPort;

    private ServerSocket serverSocket;
    private ServerActionListener listener;

    // using list for upgrade chat to group chat
    private List<ChatClient> userList;
    private PublicKey publicKey;

    private boolean hasClient = false;


    public ChatServerThread(PublicKey publicKey, final int socketServerPort) {
        this.socketServerPort = socketServerPort;
        this.publicKey = publicKey;
        userList = new ArrayList<>();
    }

    @Override
    public void run() {
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(socketServerPort);

            while (true) {
                socket = serverSocket.accept();
                ChatClient client = new ChatClient();
                userList.add(client);

                ConnectThread connectThread = new ConnectThread(publicKey, client, socket);
                connectThread.setServerActionListener(listener);
                connectThread.start();

            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public List<ChatClient> getUserList() {
        return userList;
    }

    public void setUserList(List<ChatClient> userList) {
        this.userList = userList;
    }
    public ServerActionListener getListener() {
        return listener;
    }

    public void setListener(ServerActionListener listener) {
        this.listener = listener;
    }
    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

}
