package com.rideshare.app.Server.session;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.rideshare.app.acitivities.LoginActivity;
import com.rideshare.app.pojo.User;

public class SessionManager {

    static SharedPreferences pref;
    static SessionManager app;
    private static final String PREF_NAME = "taxiapp";
    public static final String KEY_NAME = "name";
    public static final String AVATAR = "avatar";
    public static final String KEY_MOBILE = "mobile";
    public static final String KEY_PAYPALID = "paypal_id";
    public static final String KEY_VEHICLE = "vehicle";
    public static final String GCM_TOKEN = "gcm_token";
    public static final String KEY_EMAIL = "email";
    public static final String FARE_UNIT = "unit";
    public static final String COST = "cost";
    public static final String LOGIN_AS = "login_as";
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY = "key";
    public static final String USER = "user";
    public static final String USER_ID = "user_id";
    public static final String ACTIVE_RIDE_ID="active_ride_id";
    public static final String OLD_PASSWORD="old_password";
    public static final String LOCATION_PERMISSION="location_permission";
    public static final String IS_DRIVER_ARRIVED="is_driver_arrived";
    public static final String IS_CARD_SAVED="is_card_saved";
    public static final String DEVICE_ID="device_id";

    public SessionManager() {
    }

    public static void initialize(Context context) {
        if (pref == null)
            pref = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
    }

    public static void setOldPassword(String password)
    {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(OLD_PASSWORD, password);
        prefsEditor.commit();
    }

    public static String getOldPassword()
    {
        return pref.getString(OLD_PASSWORD, null);
    }


    public static void setIsDriverArrived(Boolean arrived)
    {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(IS_DRIVER_ARRIVED, arrived);
        prefsEditor.commit();
    }
    public static boolean getIsDriverArrived()
    {
        return pref.getBoolean(IS_DRIVER_ARRIVED, false);
    }

    public static void setLocationPermission(Boolean permission)
    {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(LOCATION_PERMISSION, permission);
        prefsEditor.commit();
    }
    public static boolean getLocationPermission()
    {
        return pref.getBoolean(LOCATION_PERMISSION, false);
    }
    public static void setGcmToken(String gcmToken) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(GCM_TOKEN, gcmToken);
        prefsEditor.commit();
    }




    public String getGcmToken() {
        return pref.getString(GCM_TOKEN, null);
    }

    public static void setStatus(Boolean staus) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_LOGIN, staus);
        editor.commit();
    }

    public static Boolean getStatus() {
        return pref.getBoolean(IS_LOGIN, false);
    }



    public static void setIsCardSaved(Boolean card) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(IS_CARD_SAVED, card);
        editor.commit();
    }

    public static Boolean getIsCardSaved() {
        return pref.getBoolean(IS_CARD_SAVED, false);
    }


    public static void setDeviceId(String id) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(DEVICE_ID, id);
        editor.commit();
    }

    public static String getDeviceId() {
        return pref.getString(DEVICE_ID,"");
    }


    public static void setUserId(String userid) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(USER_ID, userid);
        editor.commit();
    }

    public static String getUserId() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(USER_ID, null);
    }
    public static void setUserName(String name) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public static String getUserName() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_NAME, null);
    }

    public static void setActiveRideId(String ride_id) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(ACTIVE_RIDE_ID, ride_id);
        editor.apply();
    }

    public static String getActiveRideId() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(ACTIVE_RIDE_ID, null);
    }

    public static void setUserEmail(String email) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_EMAIL, email);
        editor.commit();
    }

    public static String getUserEmail() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_EMAIL, null);
    }

    public static void setUserMobile(String mobile) {
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(KEY_MOBILE, mobile);
        editor.commit();
    }

    public static String getUserMobile() {
        //return getUser() == null ? "" : getUser().getUser_id();
        return pref.getString(KEY_MOBILE, null);
    }

    public static void setUser(String user) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(USER, user);
        prefsEditor.commit();
    }

    public static User getUser() {
        Gson gson = new Gson();
        return gson.fromJson(pref.getString(USER, null), User.class);
    }

    public static void setKEY(String k) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(KEY, k);
        prefsEditor.commit();
    }

    public static String getKEY() {
        //return getUser() == null ? "" : getUser().getKey();
        return pref.getString(KEY, null);
    }

    public static void setUnit(String unit) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(FARE_UNIT, unit);
        prefsEditor.commit();
    }

    public static String getUnit() {
        return pref.getString(FARE_UNIT, null);
    }

    public void setpaypalId(String k) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(KEY_PAYPALID, k);
        prefsEditor.commit();
    }

    public String getPaypalId() {
        return getUser().getPaypal_id();
    }

    public static void setAvatar(String avatar) {
    }

    public static String getAvatar() {
        return getUser() == null ? "" : getUser().getAvatar();
    }

    /*--------------------------------------------------------------------------------------------------------------------------------------------*/
    public static void setCost(String unit) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(COST, unit);
        prefsEditor.apply();
      //  prefsEditor.commit();
    }

    public static void logoutUser(Context _context) {
        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putBoolean(IS_LOGIN, false);
        prefsEditor.clear();
        prefsEditor.apply();
       // prefsEditor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        _context.startActivity(i);
    }


    public static String getName() {
        return getUser() == null ? "" : getUser().getName();
    }


}