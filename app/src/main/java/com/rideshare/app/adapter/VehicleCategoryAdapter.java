package com.rideshare.app.adapter;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rideshare.app.R;
import com.rideshare.app.interfaces.VehicleTypeInterface;
import com.rideshare.app.pojo.VehicleInfo;
import com.rideshare.app.pojo.spend.VehicleCategory;

import java.util.Collections;
import java.util.List;

public class VehicleCategoryAdapter extends RecyclerView.Adapter<VehicleCategoryAdapter.VehicleCategoryHolder> implements DialogInterface.OnCancelListener {

    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<VehicleCategory> itemList;

    private VehicleTypeInterface vehicleidclicklistener;
    Context context;
    TextView ride_now;
    private int checkedPosition = -1;
    RelativeLayout footer;
    LinearLayout linear_rideNow, linear_cnfrmBooking;

    public VehicleCategoryAdapter(Context context, List<VehicleCategory> itemList, TextView ride_now, RelativeLayout footer,
                                  LinearLayout linear_rideNow,
                                  LinearLayout linear_cnfrmBooking,
                                  VehicleTypeInterface vehicleidclicklistener
    ) {
        this.context = context;
        this.itemList = itemList;
        this.ride_now = ride_now;
        this.footer = footer;
        this.linear_rideNow = linear_rideNow;
        this.linear_cnfrmBooking = linear_cnfrmBooking;
        this.vehicleidclicklistener = vehicleidclicklistener;
    }


    @NonNull
    @Override
    public VehicleCategoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.vehicle_category_layout, viewGroup, false);
        return new VehicleCategoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleCategoryHolder holder, int position) {
        VehicleCategory vehicleCategory = itemList.get(position);
        holder.vehicleCategory.setText(vehicleCategory.getCategory_name());
        LinearLayoutManager layoutManager = new LinearLayoutManager(holder.vehicleCategoryRecyclerView.getContext(), LinearLayoutManager.VERTICAL, false);

        layoutManager.setInitialPrefetchItemCount(vehicleCategory.getVehicleInfoList().size());

        VehicleInfoAdapter vehicleInfoAdapter = new VehicleInfoAdapter(context, vehicleCategory.getVehicleInfoList(), ride_now, footer, linear_rideNow,
                linear_cnfrmBooking, vehicleidclicklistener);
        holder.vehicleCategoryRecyclerView.setLayoutManager(layoutManager);
        holder.vehicleCategoryRecyclerView.setAdapter(vehicleInfoAdapter);
        holder.vehicleCategoryRecyclerView.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    @Override
    public void onCancel(DialogInterface dialog) {

    }

    public class VehicleCategoryHolder extends RecyclerView.ViewHolder {
        TextView vehicleCategory;
        RecyclerView vehicleCategoryRecyclerView;

        public VehicleCategoryHolder(View itemView) {
            super(itemView);
            vehicleCategory = itemView.findViewById(R.id.vehicle_Category_Type);
            vehicleCategoryRecyclerView = itemView.findViewById(R.id.vehicle_Category_recyclerView);
        }
    }
}
