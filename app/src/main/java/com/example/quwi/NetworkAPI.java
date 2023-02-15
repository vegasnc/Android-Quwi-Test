package com.example.quwi;

import android.util.Log;
import android.util.Pair;
import android.widget.Toast;

import com.example.quwi.model.ChatModel;
import com.example.quwi.model.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkAPI {
    private static NetworkAPI gAPI = null;
    public static NetworkAPI getInstance() {
        if( gAPI == null ) {
            gAPI = new NetworkAPI();
        }
        return gAPI;
    }

    private String mPostUrl= "https://api.quwi.com/v2/";
    private OkHttpClient mClient;
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Interface for get user information on MainActivity
    public interface GetLoginRespInterface {
        public void getLoginResp(boolean result, Object obj);
    }
    public GetLoginRespInterface mGetLoginRespInterf;

    // Interface for get user's channel list array on ChatListActivity
    public interface GetChannelListInterface {
        public void getChannelListResp(boolean result, Object obj, String partnerIdStr);
    }
    public GetChannelListInterface mGetChannelListInterface;

    // Interface for get userlist for chat list
    public interface GetUserListInterface {
        public void getUserListResp(boolean result, Object obj);
    }
    public GetUserListInterface mGetUserListInterface;

    // Interface for logout
    public interface LogoutInterface {
        public void getLogoutResp(boolean result);
    }
    public LogoutInterface mLogoutInterface;


    public NetworkAPI() {
        mClient = new OkHttpClient();
    }

    public void sendLoginRequest(String email, String password) {
        // auth/login

        String postBody = "{\n" +
                "\"email\": \"" + email + "\",\n" +
                "\"password\": \"" + password + "\"\n" +
                "}";


        RequestBody body = RequestBody.create(JSON, postBody);

        Request request = new Request.Builder()
                .url(mPostUrl + "auth/login")
                .post(body)
                .build();

        if( mClient != null ) {
            mClient = new OkHttpClient();
        }
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    if( jsonObject.has("app_init") ) {
                        if( mGetLoginRespInterf != null ) {
                            JSONObject userObject = jsonObject.getJSONObject("app_init")
                                    .getJSONObject("user");
                            UserModel userModel = new UserModel(
                                    jsonObject.getString("token"),
                                    userObject.getString("id"),
                                    userObject.getString("email"),
                                    userObject.getString("nick"),
                                    userObject.getString("avatar_url"),
                                    userObject.getString("timezone_offset"));
                            mGetLoginRespInterf.getLoginResp(true, userModel);
                        }
                        call.cancel();
                        return ;
                    }

                    if( mGetLoginRespInterf != null ) {
                        mGetLoginRespInterf.getLoginResp(false, null);
                    }
                    call.cancel();
                    return ;

                } catch (JSONException e) {
                    e.printStackTrace();
                    call.cancel();
                }
            }
        });
    }

    public void sendGetChatChannelsRequest(String token) {
        // chat-channels
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + token)
                .url(mPostUrl + "chat-channels")
                .build();

        if( mClient != null ) {
            mClient = new OkHttpClient();
        }

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    if( jsonObject.has("channels") ) {
                        JSONArray channelArr = jsonObject.getJSONArray("channels");
                        ArrayList<ChatModel> chatModelArrayList = new ArrayList<>();
                        StringBuilder partnerIdStr = new StringBuilder();

                        int cnt = channelArr.length();
                        for(int i = 0; i < cnt; i ++) {
                            if( channelArr.getJSONObject(i).getString("message_last") == "null" )
                                continue;
                            JSONObject entryObj = channelArr.getJSONObject(i).
                                    getJSONObject("message_last");
                            String mail_id = entryObj.getString("id");
                            String mailCreateUTC = entryObj.getString("dta_create_utc");
                            int partner_id = channelArr.getJSONObject(i).getInt("id_partner");
                            partnerIdStr.append(partner_id).append(",");
                            String username = "";
                            String avatarURL = "";
                            String message = entryObj.getString("snippet");
                            ChatModel chatModel = new ChatModel(
                                    mail_id, partner_id, username, avatarURL, mailCreateUTC, message);
                            chatModelArrayList.add(chatModel);
                        }

                        mGetChannelListInterface.getChannelListResp(true, chatModelArrayList, partnerIdStr.toString());
                        call.cancel();
                        return ;
                    }
                    mGetChannelListInterface.getChannelListResp(false, null, "");
                    call.cancel();
                    return ;
                } catch (JSONException e) {
                    call.cancel();
                    mGetChannelListInterface.getChannelListResp(false, null, "");
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendGetUserList(ArrayList<ChatModel> chatModelArrayList, String strUser_id, String token) {
        // users/foreign
        Request request = new Request.Builder()
                .header("Authorization", "Bearer " + token)
                .url(mPostUrl + "users/foreign?ids=" + strUser_id)
                .build();

        if( mClient != null ) {
            mClient = new OkHttpClient();
        }

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String strResponse = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(strResponse);
                    JSONArray userObjArr = jsonObject.getJSONArray("users");

                    Map<Integer, Pair<String, String>> userMap = new HashMap<>();
                    int cntUser = userObjArr.length();
                    for(int k = 0; k < cntUser; k ++) {
                        int id = userObjArr.getJSONObject(k).getInt("id");
                        String avatarUrl = userObjArr.getJSONObject(k).getString("avatar_url");
                        String username = userObjArr.getJSONObject(k).getString("name");

                        if( avatarUrl == "null" || avatarUrl.isEmpty() )
                            avatarUrl = "https://api.quwi.com/files/projects/3139-thumb.png?v=1675523511";
                        Log.e("============", avatarUrl);
                        userMap.put(id, new Pair<>(username, avatarUrl));
                    }

                    int cntChat = chatModelArrayList.size();
                    for(int i = 0; i < cntChat; i ++) {
                        ChatModel model = chatModelArrayList.get(i);
                        model.setUserName(userMap.get(model.getPartnerId()).first);
                        model.setAvatarUrl(userMap.get(model.getPartnerId()).second);
                    }

                    mGetUserListInterface.getUserListResp(true, chatModelArrayList);
                } catch (JSONException e) {
                    call.cancel();
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendLogoutRequest() {
        // users/foreign
        Request request = new Request.Builder()
                .url(mPostUrl + "auth/logout")
                .build();

        if( mClient != null ) {
            mClient = new OkHttpClient();
        }

        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                call.cancel();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mLogoutInterface.getLogoutResp(true);
            }
        });
    }
}
