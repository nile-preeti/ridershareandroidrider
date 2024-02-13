package com.rideshare.app.custom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.multidex.MultiDexApplication;

import com.rideshare.app.BuildConfig;
import com.rideshare.app.R;
import com.rideshare.app.Server.session.SessionManager;
import com.rideshare.app.acitivities.AddPaymentMethodActivity;
import com.rideshare.app.acitivities.RegisterActivity;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.pojo.AppVersionUpdateResponse;
import com.rideshare.app.pojo.CheckAppVersionResponse;
import com.rideshare.app.pojo.CheckDeviceTokenResponse;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;


/**
 * Created by android on 15/3/17.
 */

public class MyApplication extends MultiDexApplication {
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 5000;

    Timer timer;
    public static TimerTask timerTask;
    Handler hand = new Handler(Looper.getMainLooper());
    Boolean userLoggedIn = false;
    Boolean userPaymentAdded = false;
    String userDeviceToken;

    @Override
    public void onCreate() {
        super.onCreate();
        // Fabric.with(this, new Crashlytics());
        Mapbox.getInstance(getApplicationContext(), String.valueOf(R.string.mapboxkey));
        SessionManager.initialize(getApplicationContext());

        Log.d("loginstatus", SessionManager.getStatus().toString());
        // Check for update and show dialog if available
        //checkAppVersionApi();
        appVersionUpdateApi();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e("TIMER", "CheckDeviceToken");
                hand.post(new Runnable() {
                    @Override
                    public void run() {
                        //  HomeFragment.newInstance().getrideStatus_details();
                        //getCompletedRideDetails();
                        userLoggedIn = SessionManager.getStatus();
                        userPaymentAdded = SessionManager.getIsCardSaved();
                        if (userLoggedIn && userPaymentAdded) {
                            userDeviceToken = SessionManager.getDeviceId();
                            checkDeviceTokenApi();
                        }

//                        if(userLoggedIn && !userPaymentAdded){
//                            Intent intent = new Intent(MyApplication.this, AddPaymentMethodActivity.class);
//                            startActivity(intent);
//                        }
                    }
                });

            }
        };

        timer = new Timer();
        timer.scheduleAtFixedRate(timerTask, 0, 10 * 1000);

       /* handler.postDelayed(new Runnable() {
            public void run() {
                getCompletedRideDetails();
                Log.d("MyApplication", "run: getCompletedRideDetails.");
            handler.postDelayed(runnable, delay);
            }
        }, delay);*/
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    getCompletedRideDetails();
                    Log.d("MyApplication", "run: getCompletedRideDetails.");
                }
            }
        }).start();*/

       /* Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                getCompletedRideDetails();
                Log.d("MyApplication", "run: getCompletedRideDetails.");            }
        };
        mainHandler.post(myRunnable);
        Log.d("MyApplication", "run: .");*/


    }

    public void checkDeviceTokenApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<CheckDeviceTokenResponse> call =
                    apiService.checkDeviceToken("Bearer " + SessionManager.getKEY());
            call.enqueue(new Callback<CheckDeviceTokenResponse>() {
                @Override
                public void onResponse(Call<CheckDeviceTokenResponse> call, retrofit2.Response<CheckDeviceTokenResponse> response) {
                    CheckDeviceTokenResponse jsonResponse = response.body();
                    Log.d("response", response.toString());
                    if (jsonResponse != null) {
                        Log.d("TakeResponse", jsonResponse.toString());
                        if (jsonResponse.getStatus()) {
//                            Log.d("token",auth_token.toString());
                            Log.d("deviceID", jsonResponse.getDevice_token());
                            Log.d("deviceID", userDeviceToken);
//                            if (!jsonResponse.getDevice_token().toString().equals(userDeviceToken)) {
//                                SessionManager.logoutUser(getApplicationContext());
//                            }
                            //progressDialog.cancel();
                            //Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                        } else {
                            //progressDialog.cancel();
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<CheckDeviceTokenResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    //   progressDialog.cancel();
                }
            });
        } else {
            //progressDialog.cancel();
            Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            // progressDialog.dismiss();
        }
    }

    public void appVersionUpdateApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
            Map<String, String> param = new HashMap();
            param.put("device_type", "android");
            param.put("app_version", BuildConfig.VERSION_NAME.toString());//
            param.put("user_type", "1");

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<AppVersionUpdateResponse> call =
                    apiService.updateAppVersionApi("Bearer " + SessionManager.getKEY(), param);
            call.enqueue(new Callback<AppVersionUpdateResponse>() {
                @Override
                public void onResponse(Call<AppVersionUpdateResponse> call, retrofit2.Response<AppVersionUpdateResponse> response) {
                    AppVersionUpdateResponse jsonResponse = response.body();
                    Log.d("response", response.toString());
                    if (jsonResponse != null) {
                        Log.d("UpdateResponse", jsonResponse.toString());
                        if (jsonResponse.getStatus()) {
                            Log.d("UpdateResponse", jsonResponse.getMessage().toString());
                        } else {
                            //progressDialog.cancel();
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<AppVersionUpdateResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    //   progressDialog.cancel();
                }
            });
        } else {
            //progressDialog.cancel();
            Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            // progressDialog.dismiss();
        }
    }


}
