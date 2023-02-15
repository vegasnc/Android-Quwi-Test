package com.example.quwi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.quwi.model.UserModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements NetworkAPI.GetLoginRespInterface {

    View mBtnLogin;
    View mLoadingView;
    EditText mEdtEmail;
    EditText mEdtPassword;
    private String mUserEmailStr = "", mUserPassStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide login activity's action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mEdtEmail = findViewById(R.id.edt_email);
        mEdtPassword = findViewById(R.id.edt_password);
        mBtnLogin = findViewById(R.id.btn_signin);
        mLoadingView = findViewById(R.id.prog_loding);

        NetworkAPI api = NetworkAPI.getInstance();
        api.mGetLoginRespInterf = MainActivity.this;

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLoadingView.setVisibility(View.VISIBLE);
                mUserEmailStr = mEdtEmail.getText().toString();
                mUserPassStr = mEdtPassword.getText().toString();

                String valErrStr = "";
                if( mUserEmailStr.isEmpty() ) {
                    valErrStr = "Enter email please.";
                } else if( mUserPassStr.isEmpty() ) {
                    valErrStr = "Enter password please.";
                }
                if( !valErrStr.isEmpty() ) {
                    mLoadingView.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, valErrStr, Toast.LENGTH_SHORT).show();
                    return ;
                }

                api.sendLoginRequest(mUserEmailStr, mUserPassStr);
            }
        });


    }

    @Override
    public void getLoginResp(boolean result, Object userModel) {
        if( result ) {
            UserModel model = (UserModel) userModel;
            Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
            intent.putExtra("user_id", model.getUser_id());
            intent.putExtra("user_token", model.getUserToken());
            startActivity(intent);
            MainActivity.this.finish();
        } else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLoadingView.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Login faild.", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
}