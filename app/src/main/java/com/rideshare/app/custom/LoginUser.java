package com.rideshare.app.custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rideshare.app.Server.Server;
import com.rideshare.app.Server.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginUser {


    public static void login(Context context, Activity activity, String email, String password, String token) {
        final ProgressDialog loading = ProgressDialog.show(activity, "", "Please wait...", false, false);
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("utype", "1");
        params.put("gcm_token", token);
        Log.e("TOKEN", token);

        Server.post("login", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                loading.cancel();
                try {
                    if (response.has("status") && response.getBoolean("status")) {
                        CheckConnection checkConnection = new CheckConnection(activity);
                        checkConnection.isAnonymouslyLoggedIn();

                            SessionManager.setKEY(response.getString("token"));
                            SessionManager.setStatus(response.getBoolean("status"));
                            SessionManager.setUserId(response.getJSONObject("data").getString("user_id"));
                            SessionManager.setUserEmail(response.getJSONObject("data").getString("email"));
                            SessionManager.setUserName(response.getJSONObject("data").getString("name"));
                            SessionManager.setUserMobile(response.getJSONObject("data").getString("mobile"));




                    }
                } catch (JSONException e) {
                    loading.cancel();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading.cancel();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                loading.cancel();

            }

            @Override
            public void onFinish() {
                super.onFinish();
                loading.cancel();

            }
        });
    }
}
