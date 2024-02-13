package com.rideshare.app.pojo.spend;

import java.io.Serializable;
import java.util.List;


import com.rideshare.app.pojo.Audio;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PendingPojo implements Serializable {

    @SerializedName("ride_id")
    @Expose
    private String rideId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("driver_id")
    @Expose
    private String driverId;
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
    @SerializedName("user_mobile")
    @Expose
    private String userMobile;
    @SerializedName("user_avatar")
    @Expose
    private Object userAvatar;
    @SerializedName("driver_avatar")
    @Expose
    private String driverAvatar;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("driver_mobile")
    @Expose
    private String driverMobile;
    @SerializedName("driver_name")
    @Expose
    private String driverName;
    @SerializedName("driver_lastname")
    @Expose
    private String driverLastname;
    @SerializedName("vehicle_type_name")
    @Expose
    private String vehicleTypeName;
    @SerializedName("audio")
    @Expose
    private List<Audio> audio = null;

    @SerializedName("trip_fare")
    @Expose
    private String tripFare;
    @SerializedName("subtotal")
    @Expose
    private String subtotal;
    @SerializedName("booking_fee")
    @Expose
    private String bookingFee;
    @SerializedName("cancellation_charge")
    @Expose
    private String cancellationCharge;
    @SerializedName("tax_charge")
    @Expose
    private String taxCharge;
    @SerializedName("txn_id")
    @Expose
    private String txnId;
    @SerializedName("vehicle_name")
    @Expose
    private String vehicleName;
    @SerializedName("category_name")
    @Expose
    private String categoryName;


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

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public Object getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(Object userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getDriverAvatar() {
        return driverAvatar;
    }

    public void setDriverAvatar(String driverAvatar) {
        this.driverAvatar = driverAvatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDriverMobile() {
        return driverMobile;
    }

    public void setDriverMobile(String driverMobile) {
        this.driverMobile = driverMobile;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    public List<Audio> getAudio() {
        return audio;
    }

    public void setAudio(List<Audio> audio) {
        this.audio = audio;
    }

    public String getTripFare() {
        return tripFare;
    }

    public void setTripFare(String tripFare) {
        this.tripFare = tripFare;
    }

    public String getBookingFee() {
        return bookingFee;
    }

    public void setBookingFee(String bookingFee) {
        this.bookingFee = bookingFee;
    }

    public String getCancellationCharge() {
        return cancellationCharge;
    }

    public void setCancellationCharge(String cancellationCharge) {
        this.cancellationCharge = cancellationCharge;
    }

    public String getTaxCharge() {
        return taxCharge;
    }

    public void setTaxCharge(String taxCharge) {
        this.taxCharge = taxCharge;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getTxnId() {
        return txnId;
    }

    public void setTxnId(String txnId) {
        this.txnId = txnId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDriverLastname() {
        return driverLastname;
    }

    public void setDriverLastname(String driverLastname) {
        this.driverLastname = driverLastname;
    }
}
