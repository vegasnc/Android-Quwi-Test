package com.example.quwi.model;

import android.text.SpannableString;

public class UserModel {
    String mUserToken;
    String mUser_id;
    String mEmail;
    String mNick;
    String mAvatarUrl;
    String mTimezoneOffset;
    public UserModel(String token, String user_id, String email,
                     String nick, String avatarUrl, String timezoneOffset) {
        mUserToken = token;
        mUser_id = user_id;
        mEmail = email;
        mNick = nick;
        mAvatarUrl = avatarUrl;
        mTimezoneOffset = timezoneOffset;
    }

    public String getUser_id() {
        return mUser_id;
    }

    public String getUserToken() {
        return mUserToken;
    }

}
