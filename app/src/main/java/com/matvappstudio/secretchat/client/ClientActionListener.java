package com.matvappstudio.secretchat.client;

import com.matvappstudio.secretchat.model.Message;

/**
 * Created by Alexandr.
 */
public interface ClientActionListener {
    void onSaveMessage(Message message);
    void onErrorMessage(String message);
}
