package com.rideshare.app.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CheckDeviceTokenResponse {
    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    private String device_type,device_token;

    public Boolean getStatus(){
        return status;
    }
    public void setStatus(Boolean status){
        this.status = status;
    }

    public String getMessage(){
        return message;
    }
    public void setMessage(String message){
        this.message = message;
    }

    public String getDeviceType(){
        return device_type;
    }
    public void setDevice_type(String device_type){
        this.device_type = device_type;
    }

    public String getDevice_token(){
        return device_token;
    }
    public void setDevice_token(String device_token){
        this.device_token = device_token;
    }


}
