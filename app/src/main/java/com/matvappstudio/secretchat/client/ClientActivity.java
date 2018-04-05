package com.matvappstudio.secretchat.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.matvappstudio.secretchat.MessageListAdapter;
import com.matvappstudio.secretchat.R;
import com.matvappstudio.secretchat.model.Message;
import com.matvappstudio.secretchat.model.User;
import com.matvappstudio.secretchat.server.ServerActivity;

/**
 * Created by Alexandr
 */
public class ClientActivity extends AppCompatActivity implements ClientActionListener {

    public static final String TAG = ClientActivity.class.getSimpleName();

    public static final int SocketServerPORT = 8080;
    public static final String SERVER_ADDRESS_KEY = "server_address";

    public static String userNick;

    private RecyclerView messageList;
    private EditText editTextChatBox;
    private ImageButton btnSend;

    private MessageListAdapter messageListAdapter;
    private ChatClientThread chatClientThread = null;


    public static void start(@NonNull Activity activity, @NonNull String nick, @NonNull String address) {
        Intent intent = new Intent(activity, ClientActivity.class);
        intent.putExtra(ServerActivity.USER_NICK_KEY, nick);
        intent.putExtra(SERVER_ADDRESS_KEY, address);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void finish() {
        if (chatClientThread == null) {
            return;
        }
        chatClientThread.disconnect();
        super.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        messageList = findViewById(R.id.reyclerview_message_list);
        editTextChatBox = findViewById(R.id.edittext_chatbox);
        btnSend = findViewById(R.id.btn_send);

        userNick = getIntent().getStringExtra(ServerActivity.USER_NICK_KEY);
        String textAddress = getIntent().getStringExtra(SERVER_ADDRESS_KEY);

        messageListAdapter = new MessageListAdapter(new User(userNick));
        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(messageListAdapter);

        btnSend.setOnClickListener(buttonSendOnClickListener);




        chatClientThread = new ChatClientThread(textAddress, SocketServerPORT);
        chatClientThread.setClientActionListener(ClientActivity.this);
        chatClientThread.start();

    }

    OnClickListener buttonSendOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (editTextChatBox.getText().toString().equals("")) {
                return;
            }

            if (chatClientThread == null) {
                return;
            }

            chatClientThread.sendMsg(editTextChatBox.getText().toString());
            editTextChatBox.setText("");
        }

    };

    @Override
    public void onSaveMessage(final Message message) {

        ClientActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                messageListAdapter.addItem(message);
                messageList.scrollToPosition(messageListAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    public void onErrorMessage(final String message) {
        ClientActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ClientActivity.this, message, Toast.LENGTH_SHORT).show();
                finish();
            }
        });


    }

}