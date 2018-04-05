package com.matvappstudio.secretchat.server;

import com.google.gson.Gson;
import com.matvappstudio.secretchat.model.ChatClient;
import com.matvappstudio.secretchat.model.Message;
import com.matvappstudio.secretchat.model.User;
import com.matvappstudio.secretchat.utils.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by Alexandr
 */

public class ConnectThread extends Thread {

    public static final String TAG = ConnectThread.class.getSimpleName();

    private Socket socket;
    private ChatClient connectClient;
    private String msgToSend = "";


    private Gson gson;
    private ServerActionListener serverActionListener;
    private PublicKey publicKey;

    ConnectThread(PublicKey publicKey, ChatClient client, Socket socket){
        connectClient = client;
        this.socket= socket;
        this.publicKey = publicKey;
        client.setSocket(socket);
        client.setChatThread(this);
        gson = new Gson();
    }

    @Override
    public void run() {
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            // Getting public key from client
            String recvPublikKeyJsonStr = dataInputStream.readUTF();
            Message recvKeyMessage = gson.fromJson(recvPublikKeyJsonStr, Message.class);

            PublicKey recvPublicKey =
                    KeyFactory
                            .getInstance("EC")
                            .generatePublic(
                                    new X509EncodedKeySpec(Utility.toBytesFromHex(recvKeyMessage.getMessage())));

            serverActionListener.onKeyReceived(recvPublicKey);


            // send public key to client
            Message keyMessage = new Message(
                    Utility.toHexFromBytes(publicKey.getEncoded()),
                    new User(ServerActivity.userNick),
                    0,
                    true);

            dataOutputStream.writeUTF(gson.toJson(keyMessage));
            dataOutputStream.flush();


            // send message about user connect
//            String connectMessageStr = recvKeyMessage.getSender().getNickname() + " connected@" +
//                    connectClient.getSocket().getInetAddress() +
//                    ":" + connectClient.getSocket().getPort();

            String connectMessageStr = "Welcome " + recvKeyMessage.getSender().getNickname() + "!!!";

            Message connMessage = new Message(connectMessageStr, new User(""), 0, true);

            serverActionListener.onSaveMessage(connMessage);
            serverActionListener.onSendMessageForEveryOne(
                    ServerActivity.securityCap.encrypt(gson.toJson(connMessage)));



            while (true) {
                if (dataInputStream.available() > 0) {
                    String newMsg = ServerActivity.securityCap.decrypt(dataInputStream.readUTF());
                    newMsg = ServerActivity.securityCap.decrypt(newMsg);

                    Message recvMessage = gson.fromJson(newMsg, Message.class);

                    serverActionListener.onSaveMessage(recvMessage);
                    serverActionListener.onSendMessageForEveryOne(ServerActivity.securityCap.encrypt(newMsg));
                }

                if(!msgToSend.equals("")){
                    dataOutputStream.writeUTF(msgToSend);
                    dataOutputStream.flush();
                    msgToSend = "";
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } finally {
            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

//            userList.remove(connectClient);
//            ServerActivity.this.runOnUiThread(new Runnable() {
//
//                @Override
//                public void run() {
//                    Toast.makeText(ServerActivity.this,
//                            connectClient.getName() + " removed.", Toast.LENGTH_LONG).show();
//
//                    msgLog += "-- " + connectClient.getName() + " leaved\n";
//                    ServerActivity.this.runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            chatMsg.setText(msgLog);
//                        }
//                    });
//
//                    broadcastMsg("-- " + connectClient.getName() + " leaved\n");
//                }
//            });
        }

    }

    public void sendMsg(String msg){
        msgToSend = msg;
    }

    public ServerActionListener getServerActionListener() {
        return serverActionListener;
    }

    public void setServerActionListener(ServerActionListener serverActionListener) {
        this.serverActionListener = serverActionListener;
    }
}
