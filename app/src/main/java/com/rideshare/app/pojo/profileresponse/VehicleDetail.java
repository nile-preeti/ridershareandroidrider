
package com.rideshare.app.pojo.profileresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VehicleDetail {

    @SerializedName("model_name")
    @Expose
    private String modelName;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("model_id")
    @Expose
    private String modelId;
    @SerializedName("brand_id")
    @Expose
    private String brandId;
    @SerializedName("year")
    @Expose
    private String year;
    @SerializedName("color")
    @Expose
    private String color;
    @SerializedName("vehicle_no")
    @Expose
    private String vehicleNo;
    @SerializedName("vehicle_type")
    @Expose
    private String vehicleType;

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

}
