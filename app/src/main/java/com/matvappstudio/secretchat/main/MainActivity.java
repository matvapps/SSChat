package com.matvappstudio.secretchat.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import com.github.florent37.viewanimator.ViewAnimator;
import com.matvappstudio.secretchat.R;
import com.matvappstudio.secretchat.login.LoginActivity;
import com.matvappstudio.secretchat.utils.Utility;

/**
 * Created by Alexandr
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button createChat;
    Button connToChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createChat = findViewById(R.id.btn_create_chat);
        connToChat = findViewById(R.id.btn_conn_to_chat);

        ViewAnimator
                .animate(createChat)
                .duration(600)
                .interpolator(new DecelerateInterpolator())
                .translationY(Utility.dpToPixel(-40, this))

                .andAnimate(connToChat)
                .duration(600)
                .interpolator(new DecelerateInterpolator())
                .translationY(Utility.dpToPixel(40, this))

                .start();

        createChat.setOnClickListener(this);
        connToChat.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_conn_to_chat: {
                LoginActivity.start(this, LoginActivity.USER_CLIENT_TYPE);
                break;
            }
            case R.id.btn_create_chat: {
                LoginActivity.start(this, LoginActivity.USER_SERVER_TYPE);
                break;
            }
        }

    }
}
