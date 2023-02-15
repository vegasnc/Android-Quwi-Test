package com.example.quwi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.quwi.adapter.ChatListAdapter;
import com.example.quwi.model.ChatModel;

import java.util.ArrayList;

public class ChatListActivity extends AppCompatActivity implements NetworkAPI.GetChannelListInterface, NetworkAPI.GetUserListInterface, NetworkAPI.LogoutInterface {

    View mLoadingView;
    ListView mChatListView;
    ChatListAdapter mChatListAdapter;

    String mUserToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        mChatListAdapter = new ChatListAdapter(this, R.layout.chatitem);

        mLoadingView = findViewById(R.id.prog_loding);
        mChatListView = findViewById(R.id.chat_list_view);
        mChatListView.setAdapter(mChatListAdapter);

        mChatListView.setOnItemClickListener((adapterView, view, i, l) ->
                Toast.makeText(ChatListActivity.this, "This is test", Toast.LENGTH_SHORT).show());

        Intent intent = getIntent();
        String user_id = intent.getStringExtra("user_id");
        mUserToken = intent.getStringExtra("user_token");


        NetworkAPI api = NetworkAPI.getInstance();
        api.mGetChannelListInterface = ChatListActivity.this;
        api.mGetUserListInterface = ChatListActivity.this;
        api.mLogoutInterface = ChatListActivity.this;

        if( mUserToken.isEmpty() ) {
            showReLoginToast();
        } else {
            mLoadingView.setVisibility(View.VISIBLE);
            api.sendGetChatChannelsRequest(mUserToken);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void getChannelListResp(boolean result, Object obj, String partnerIdStr) {
        if( result ) {
            ArrayList<ChatModel> chatModelArrayList = (ArrayList<ChatModel>) obj;
            NetworkAPI.getInstance().sendGetUserList(chatModelArrayList, partnerIdStr, mUserToken);
        } else {
            showReLoginToast();
        }
    }

    private void showReLoginToast() {

        runOnUiThread(() -> {
            mLoadingView.setVisibility(View.GONE);
            Toast.makeText(ChatListActivity.this, "Login again please.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void getUserListResp(boolean result, Object obj) {
        if( result ) {
            ArrayList<ChatModel> chatModelArrayList = (ArrayList<ChatModel>) obj;
            runOnUiThread(() -> {
                mLoadingView.setVisibility(View.GONE);
                mChatListAdapter.setData(chatModelArrayList);
                mChatListView.invalidateViews();
            });
        } else {
            showReLoginToast();
        }
    }

    @Override
    public void getLogoutResp(boolean result) {
        Intent intent = new Intent(ChatListActivity.this, MainActivity.class);
        startActivity(intent);
        ChatListActivity.this.finish();
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void logout() {

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatListActivity.this);
        builder.setMessage(R.string.really_logout)
                .setPositiveButton(R.string.ok_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        NetworkAPI.getInstance().sendLogoutRequest();
                    }
                })
                .setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).show();
    }
}