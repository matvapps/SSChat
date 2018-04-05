package com.matvappstudio.secretchat.server;

import com.matvappstudio.secretchat.model.Message;

import java.security.PublicKey;

/**
 * Created by Alexandr.
 */
public interface ServerActionListener {
    void onSendMessageForEveryOne(String message);
    void onSaveMessage(Message message);
    void onKeyReceived(PublicKey recvPublicKey);
}
