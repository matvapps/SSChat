package com.matvappstudio.secretchat.login;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.florent37.viewanimator.ViewAnimator;
import com.matvappstudio.secretchat.R;
import com.matvappstudio.secretchat.client.ClientActivity;
import com.matvappstudio.secretchat.server.ServerActivity;
import com.matvappstudio.secretchat.utils.Utility;

public class LoginActivity extends AppCompatActivity {

    public static final String USER_TYPE_KEY = "user_type";

    public static final int USER_SERVER_TYPE = 22;
    public static final int USER_CLIENT_TYPE = 23;


    private Button btnLogin;
    private EditText nickEdtxt;
    private EditText addressEdtxt;

    private View nickEdtxtBox;
    private View addressEdtxtBox;

    private int currentType;

    public static void start(@NonNull Activity activity, @NonNull int userType) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.putExtra(USER_TYPE_KEY, userType);
        activity.startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        currentType = getIntent().getIntExtra(USER_TYPE_KEY, 0);

        View.OnClickListener submitNickServerClickListener;
        View.OnClickListener submitNickClientClickListener;
        final View.OnClickListener submitAddressClickListener;


        btnLogin = findViewById(R.id.btn_login);
        nickEdtxt = findViewById(R.id.edittext_nick);
        addressEdtxt = findViewById(R.id.edittext_address);
        nickEdtxtBox = findViewById(R.id.edittext_nick_box);
        addressEdtxtBox = findViewById(R.id.edittext_address_box);

        submitNickServerClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nickEdtxt.getText().toString().isEmpty())
                    Toast.makeText(LoginActivity.this, R.string.enter_nick_str, Toast.LENGTH_SHORT).show();
                else
                    ServerActivity.start(LoginActivity.this, nickEdtxt.getText().toString());

            }
        };

        submitAddressClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addressEdtxt.getText().toString().isEmpty())
                    Toast.makeText(LoginActivity.this, R.string.input_server_address, Toast.LENGTH_SHORT).show();
                else {
                    ClientActivity.start(LoginActivity.this,
                            nickEdtxt.getText().toString(),
                            addressEdtxt.getText().toString());
                }
            }
        };

        submitNickClientClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (nickEdtxt.getText().toString().isEmpty())
                    Toast.makeText(LoginActivity.this, R.string.enter_nick_str, Toast.LENGTH_SHORT).show();
                else {
                    nickEdtxt.setEnabled(false);
                    // Show address edtxt
                    ViewAnimator
                            .animate(nickEdtxtBox)
//                            .startDelay(50)
                            .duration(400)
                            .interpolator(new DecelerateInterpolator())
                            .translationY(0, -Utility.dpToPixel(32, LoginActivity.this))

                            .andAnimate(addressEdtxtBox)
                            .duration(400)
                            .interpolator(new DecelerateInterpolator())
                            .translationY(0, Utility.dpToPixel(32, LoginActivity.this))

                            .andAnimate(btnLogin)
                            .duration(400)
                            .interpolator(new DecelerateInterpolator())
                            .translationY(0, Utility.dpToPixel(32, LoginActivity.this))

                            .start();


                    btnLogin.setOnClickListener(submitAddressClickListener);
                    btnLogin.setText(R.string.btn_start_text);
                }
            }
        };



        switch (currentType) {
            case USER_SERVER_TYPE:
                btnLogin.setOnClickListener(submitNickServerClickListener);
                break;
            case USER_CLIENT_TYPE:
                btnLogin.setOnClickListener(submitNickClientClickListener);
                break;
            default:
                break;

        }

    }
}
