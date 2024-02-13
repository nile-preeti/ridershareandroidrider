package com.rideshare.app.pojo.last_ride;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastRideResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private LastRideData data;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LastRideData getData() {
        return data;
    }

    public void setData(LastRideData data) {
        this.data = data;
    }

}