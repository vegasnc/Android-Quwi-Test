package com.example.quwi.model;

public class ChatModel {
    String mMail_id;
    int mPartner_id;
    String mUserName;
    String mAvatarUrl;
    String mCreateUTC;
    String mMessage;
    public ChatModel(String mail_id, int partner_id, String userName, String avatarUrl,
                     String createUTC, String lastChat) {
        mMail_id = mail_id;
        mPartner_id = partner_id;
        mUserName = userName;
        mAvatarUrl = avatarUrl;
        mCreateUTC = createUTC;
        mMessage = lastChat;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public void setAvatarUrl(String avatarUrl) {
        mAvatarUrl = avatarUrl;
    }

    public String getMailId() {
        return mMail_id;
    }

    public int getPartnerId() {
        return mPartner_id;
    }

    public String getUserName() {
        return mUserName;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getCreateUTC() {
        return mCreateUTC;
    }

    public String getLastMessage() {
        return mMessage;
    }
}
