package com.example.quwi.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.quwi.R;
import com.example.quwi.model.ChatModel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatListAdapter extends ArrayAdapter<String> {

    private final Activity mContext;
    private final int mChatListRes;
    private ArrayList<ChatModel> mChatData;

    public ChatListAdapter(Activity context, int resource) {
        super(context, resource);
        this.mChatListRes = resource;
        this.mContext = context;
        mChatData = new ArrayList<>();
    }

    public void setData(ArrayList<ChatModel> chatModelArrayList) {
        mChatData = chatModelArrayList;
    }

    @Override
    public int getCount() {
        if( mChatData == null )
            return 0;
        return mChatData.size();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mContext.getLayoutInflater();
        View itemView=inflater.inflate(mChatListRes, null,true);

        TextView titleText = (TextView) itemView.findViewById(R.id.txt_username);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.img_icon);
        TextView contentText = (TextView) itemView.findViewById(R.id.txt_content);
        TextView timeText = (TextView) itemView.findViewById(R.id.txt_time);

        ChatModel chatModel = mChatData.get(position);
        titleText.setText(chatModel.getUserName());

        if( chatModel.getAvatarUrl() != null && !chatModel.getAvatarUrl().isEmpty()  ) {
            new Thread(() -> {
                InputStream is = null;
                Drawable drawable = null;
                try {
                    is = (InputStream) new URL(chatModel.getAvatarUrl()).getContent();
                    drawable = Drawable.createFromStream(is, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if( drawable != null ) {
                    Drawable finalDrawable = drawable;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageDrawable(finalDrawable);
                        }
                    });
                }
            }).start();
        }
        contentText.setText(chatModel.getLastMessage());
        String time = chatModel.getCreateUTC();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(time);
            timeText.setText(date.getHours() + ":" + date.getMinutes());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return itemView;

    };


}
