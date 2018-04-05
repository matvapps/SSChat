package com.matvappstudio.secretchat.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.gson.Gson;
import com.matvappstudio.secretchat.utils.AESSecurityCap;
import com.matvappstudio.secretchat.MessageListAdapter;
import com.matvappstudio.secretchat.R;
import com.matvappstudio.secretchat.model.Message;
import com.matvappstudio.secretchat.model.User;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.PublicKey;
import java.util.Calendar;
import java.util.Enumeration;

/**
 * Created by Alexandr
 */
public class ServerActivity extends AppCompatActivity implements ServerActionListener {

    public static final String TAG = ServerActivity.class.getSimpleName();

    public static final int SocketServerPORT = 8080;
    public static final String USER_NICK_KEY = "user_nick";

    private EditText editTextChatBox;
    private ImageButton btnSend;

    private RecyclerView messageList;
    private MessageListAdapter messageListAdapter;


    private ChatServerThread chatServerThread;

    public static AESSecurityCap securityCap;
    public static String userNick;


    public static void start(@NonNull Activity activity, @NonNull String nick) {
        Intent intent = new Intent(activity, ServerActivity.class);
        intent.putExtra(USER_NICK_KEY, nick);
        activity.startActivity(intent);
        activity.finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        editTextChatBox = findViewById(R.id.edittext_chatbox);
        btnSend = findViewById(R.id.btn_send);
        messageList = findViewById(R.id.reyclerview_message_list);


        userNick = getIntent().getStringExtra(USER_NICK_KEY);

        messageListAdapter = new MessageListAdapter(new User(userNick));

        messageList.setLayoutManager(new LinearLayoutManager(this));
        messageList.setAdapter(messageListAdapter);


        onSaveMessage(new Message(getIpAddress(),
                new User(""),
                0,
                true));


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editTextChatBox.getText().toString().isEmpty()) {

                    Gson gson = new Gson();

                    Message message = new Message(editTextChatBox.getText().toString(),
                            new User(userNick),
                            Calendar.getInstance().getTimeInMillis(), false);

                    String jsonMessage = gson.toJson(message);

                    onSaveMessage(message);
                    try {
                        String encryptJsonMessage = ServerActivity.securityCap.encrypt(jsonMessage);
                        broadcastMsg(encryptJsonMessage);
                    } catch (Exception ex) {
                        Log.d(TAG, "No client for now");
                    }

                    editTextChatBox.setText("");
                }
            }
        });

        securityCap = new AESSecurityCap();

        chatServerThread = new ChatServerThread(securityCap.getPublickey(), SocketServerPORT);
        chatServerThread.setListener(this);
        chatServerThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (chatServerThread.getServerSocket() != null) {
            try {
                chatServerThread.getServerSocket().close();

                Gson gson = new Gson();

                Message message = new Message("Server stopped, bye)",
                        new User(userNick),
                        Calendar.getInstance().getTimeInMillis(), true);

                String jsonMessage = gson.toJson(message);

                broadcastMsg(securityCap.encrypt(jsonMessage));
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    private void broadcastMsg(String msg) {

        for (int i = 0; i < chatServerThread.getUserList().size(); i++) {
            chatServerThread.getUserList().get(i).getChatThread().sendMsg("\n" + msg);
        }

    }

    private String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress.nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "LocalAddress: "
                                + inetAddress.getHostAddress();
                        if (enumInetAddress.hasMoreElements()) {
                            ip += "\n";
                        }
                    }

                }

            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }

        return ip;
    }

    @Override
    public void onSendMessageForEveryOne(String message) {
        broadcastMsg(message);
    }

    @Override
    public void onSaveMessage(final Message message) {
        ServerActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                messageListAdapter.addItem(message);
                messageList.scrollToPosition(messageListAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    public void onKeyReceived(PublicKey recvPublicKey) {
        securityCap.setReceiverPublicKey(recvPublicKey);
    }
}