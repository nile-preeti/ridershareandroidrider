package com.rideshare.app.pojo.spend;

import com.rideshare.app.pojo.VehicleInfo;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class VehicleCategory  implements Serializable {
    String category_name;
    private List<VehicleInfo> vehicleInfoList = Collections.emptyList();

    public VehicleCategory(String category_name, List<VehicleInfo> vehicleInfoList) {
        this.category_name = category_name;
        this.vehicleInfoList = vehicleInfoList;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public List<VehicleInfo> getVehicleInfoList() {
        return vehicleInfoList;
    }

    public void setVehicleInfoList(List<VehicleInfo> vehicleInfoList) {
        this.vehicleInfoList = vehicleInfoList;
    }
}
