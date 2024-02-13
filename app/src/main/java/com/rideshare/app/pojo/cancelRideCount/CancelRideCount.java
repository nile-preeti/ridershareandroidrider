package com.rideshare.app.pojo.cancelRideCount;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CancelRideCount {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("count_ride")
    @Expose
    private String countRide;

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

    public String getCountRide() {
        return countRide;
    }

    public void setCountRide(String countRide) {
        this.countRide = countRide;
    }
}
