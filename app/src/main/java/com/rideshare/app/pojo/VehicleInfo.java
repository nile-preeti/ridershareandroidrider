package com.rideshare.app.pojo;

import java.io.Serializable;

public class VehicleInfo implements Serializable {

    String name;
    String time;
    String specs;
    String rate;
    String distance;
    String car_pic;
    String vehicle_id;
    String totalAmount;
    String category_name;
    String hold_amount;
    String cancellation_charge;

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getCar_pic() {
        return car_pic;
    }

    public void setCar_pic(String car_pic) {
        this.car_pic = car_pic;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getVehicle_id() {
        return vehicle_id;
    }

    public void setVehicle_id(String vehicle_id) {
        this.vehicle_id = vehicle_id;
    }

    public String getHold_amount() {
        return hold_amount;
    }

    public void setHold_amount(String hold_amount) {
        this.hold_amount = hold_amount;
    }

    public String getCancellation_charge() {
        return cancellation_charge;
    }

    public void setCancellation_charge(String cancellation_charge) {
        this.cancellation_charge = cancellation_charge;
    }
}
