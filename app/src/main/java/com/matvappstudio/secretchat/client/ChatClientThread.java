package com.matvappstudio.secretchat.client;


import com.google.gson.Gson;
import com.matvappstudio.secretchat.model.Message;
import com.matvappstudio.secretchat.model.User;
import com.matvappstudio.secretchat.utils.AESSecurityCap;
import com.matvappstudio.secretchat.utils.Utility;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

/**
 * Created by Alexandr
 */

public class ChatClientThread extends Thread {

    public static final String TAG = ChatClientThread.class.getSimpleName();

    private String dstAddress;
    private int dstPort;

    private PublicKey publicKey;
    private PublicKey recvPublicKey = null;
    private AESSecurityCap securityCap;

    private Gson gson;
    private String msgToSend = "";
    private boolean goOut = false;

    private ClientActionListener clientActionListener;

    ChatClientThread(String address, int port) {
        securityCap = new AESSecurityCap();
        this.publicKey = securityCap.getPublickey();
        dstAddress = address;
        dstPort = port;
        gson = new Gson();
    }

    @Override
    public void run() {
        Socket socket = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;

        try {
            socket = new Socket(dstAddress, dstPort);
            dataOutputStream = new DataOutputStream(
                    socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());

            Message keyMessage =
                    new Message(
                            Utility.toHexFromBytes(publicKey.getEncoded()),
                            new User(ClientActivity.userNick),
                            0,
                            true);


            dataOutputStream.writeUTF(gson.toJson(keyMessage));
            dataOutputStream.flush();

            while (!goOut) {
                if (dataInputStream.available() > 0) {

                    final String newMessage = dataInputStream.readUTF();

                    if (recvPublicKey == null) {
                        // get server public key
                        Message message = gson.fromJson(newMessage, Message.class);

                        recvPublicKey =
                                KeyFactory
                                        .getInstance("EC")
                                        .generatePublic(
                                                new X509EncodedKeySpec(Utility.toBytesFromHex(message.getMessage())));

                        securityCap.setReceiverPublicKey(recvPublicKey);

                    } else {
                        Message message = gson.fromJson(securityCap.decrypt(newMessage), Message.class);
                        clientActionListener.onSaveMessage(message);
                    }

                }

                if (!msgToSend.equals("")) {

                    boolean isInfo = msgToSend.contains("disconnect");

                    Message message = new Message(msgToSend,
                            new User(ClientActivity.userNick),
                            Calendar.getInstance().getTimeInMillis(), isInfo);

                    String jsonMessage = gson.toJson(message);
                    String encryptJsonMessage = securityCap.encrypt(jsonMessage);

                    dataOutputStream.writeUTF(securityCap.encrypt(encryptJsonMessage));
                    dataOutputStream.flush();
                    msgToSend = "";
                }
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
            final String eString = e.toString();

            clientActionListener.onErrorMessage(eString);

        } catch (IOException e) {
            e.printStackTrace();
            final String eString = e.toString();

            clientActionListener.onErrorMessage(eString);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (dataOutputStream != null) {
                try {
                    dataOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (dataInputStream != null) {
                try {
                    dataInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void sendMsg(String msg) {
        msgToSend = msg;
    }

    public void disconnect() {
        sendMsg(ClientActivity.userNick + " disconnected");
        goOut = true;
    }

    public ClientActionListener getClientActionListener() {
        return clientActionListener;
    }

    public void setClientActionListener(ClientActionListener clientActionListener) {
        this.clientActionListener = clientActionListener;
    }
}
