package com.rideshare.app.pojo.spend;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PendingPojoResponse {

    @SerializedName("status")
    @Expose
    private Boolean status;
    @SerializedName("total_record")
    @Expose
    private Integer totalRecord;
    @SerializedName("total_earning")
    @Expose
    private String totalEarning;
    @SerializedName("data")
    @Expose
    private List<PendingPojo> data = null;


    public String getTotalEarning() {
        return totalEarning;
    }

    public void setTotalEarning(String totalEarning) {
        this.totalEarning = totalEarning;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(Integer totalRecord) {
        this.totalRecord = totalRecord;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public List<PendingPojo> getData() {
        return data;
    }

    public void setData(List<PendingPojo> data) {
        this.data = data;
    }

}