package com.rideshare.app.pojo.last_ride;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LastRideData {

    @SerializedName("ride_id")
    @Expose
    private String rideId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
    @SerializedName("vehicle_type_id")
    @Expose
    private String vehicleTypeId;
    @SerializedName("pickup_adress")
    @Expose
    private String pickupAdress;
    @SerializedName("drop_address")
    @Expose
    private String dropAddress;
    @SerializedName("pikup_location")
    @Expose
    private String pikupLocation;
    @SerializedName("pickup_lat")
    @Expose
    private String pickupLat;
    @SerializedName("pickup_long")
    @Expose
    private String pickupLong;
    @SerializedName("drop_locatoin")
    @Expose
    private String dropLocatoin;
    @SerializedName("drop_lat")
    @Expose
    private String dropLat;
    @SerializedName("drop_long")
    @Expose
    private String dropLong;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("cancelled_by")
    @Expose
    private Object cancelledBy;
    @SerializedName("cancelled_count")
    @Expose
    private String cancelledCount;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("pay_driver")
    @Expose
    private String payDriver;
    @SerializedName("payment_mode")
    @Expose
    private String paymentMode;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("total_rating")
    @Expose
    private String totalRating;
    @SerializedName("total_driver_ride")
    @Expose
    private String totalDriverRide;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("feedback")
    @Expose
    private String feedback;
    @SerializedName("total_time")
    @Expose
    private String totalTime;
    @SerializedName("total_distance")
    @Expose
    private String totalDistance;
    @SerializedName("total_arrival_distance")
    @Expose
    private String totalArrivalDistance;
    @SerializedName("total_arrival_time")
    @Expose
    private String totalArrivalTime;

    @SerializedName("is_destination_ride")
    @Expose
    private String isDestinationRide;
    @SerializedName("is_technical_issue")
    @Expose
    private String isTechnicalIssue;

    public String getIsTechnicalIssue() {
        return isTechnicalIssue;
    }

    public void setIsTechnicalIssue(String isTechnicalIssue) {
        this.isTechnicalIssue = isTechnicalIssue;
    }

    public String getIsDestinationRide() {
        return isDestinationRide;
    }

    public void setIsDestinationRide(String isDestinationRide) {
        this.isDestinationRide = isDestinationRide;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(String vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getPickupAdress() {
        return pickupAdress;
    }

    public void setPickupAdress(String pickupAdress) {
        this.pickupAdress = pickupAdress;
    }

    public String getDropAddress() {
        return dropAddress;
    }

    public void setDropAddress(String dropAddress) {
        this.dropAddress = dropAddress;
    }

    public String getPikupLocation() {
        return pikupLocation;
    }

    public void setPikupLocation(String pikupLocation) {
        this.pikupLocation = pikupLocation;
    }

    public String getPickupLat() {
        return pickupLat;
    }

    public void setPickupLat(String pickupLat) {
        this.pickupLat = pickupLat;
    }

    public String getPickupLong() {
        return pickupLong;
    }

    public void setPickupLong(String pickupLong) {
        this.pickupLong = pickupLong;
    }

    public String getDropLocatoin() {
        return dropLocatoin;
    }

    public void setDropLocatoin(String dropLocatoin) {
        this.dropLocatoin = dropLocatoin;
    }

    public String getDropLat() {
        return dropLat;
    }

    public void setDropLat(String dropLat) {
        this.dropLat = dropLat;
    }

    public String getDropLong() {
        return dropLong;
    }

    public void setDropLong(String dropLong) {
        this.dropLong = dropLong;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(Object cancelledBy) {
        this.cancelledBy = cancelledBy;
    }

    public String getCancelledCount() {
        return cancelledCount;
    }

    public void setCancelledCount(String cancelledCount) {
        this.cancelledCount = cancelledCount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPayDriver() {
        return payDriver;
    }

    public void setPayDriver(String payDriver) {
        this.payDriver = payDriver;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(String totalRating) {
        this.totalRating = totalRating;
    }

    public String getTotalDriverRide() {
        return totalDriverRide;
    }

    public void setTotalDriverRide(String totalDriverRide) {
        this.totalDriverRide = totalDriverRide;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(String totalTime) {
        this.totalTime = totalTime;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getTotalArrivalDistance() {
        return totalArrivalDistance;
    }

    public void setTotalArrivalDistance(String totalArrivalDistance) {
        this.totalArrivalDistance = totalArrivalDistance;
    }

    public String getTotalArrivalTime() {
        return totalArrivalTime;
    }

    public void setTotalArrivalTime(String totalArrivalTime) {
        this.totalArrivalTime = totalArrivalTime;
    }

}