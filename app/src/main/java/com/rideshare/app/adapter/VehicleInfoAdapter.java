package com.rideshare.app.adapter;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.rideshare.app.R;
import com.rideshare.app.fragement.HomeFragment;
import com.rideshare.app.interfaces.VehicleTypeInterface;
import com.rideshare.app.pojo.VehicleInfo;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.rideshare.app.pojo.Global.clickId;

public class VehicleInfoAdapter extends RecyclerView.Adapter implements DialogInterface.OnCancelListener {
    private VehicleTypeInterface vehicleidclicklistener;
    Context context;
    private List<VehicleInfo> vehicleInfoList = Collections.emptyList();
    TextView ride_now;
    private int checkedPosition = -1;
    RelativeLayout footer;
    LinearLayout linear_rideNow, linear_cnfrmBooking;

    //constructor

    public VehicleInfoAdapter(Context context, List<VehicleInfo> vehicleInfoList, TextView ride_now, RelativeLayout footer, LinearLayout linear_rideNow, LinearLayout linear_cnfrmBooking, VehicleTypeInterface vehicleidclicklistener) {
        this.context = context;
        this.vehicleInfoList = vehicleInfoList;
        this.ride_now = ride_now;
        this.footer = footer;
        this.linear_rideNow = linear_rideNow;
        this.linear_cnfrmBooking = linear_cnfrmBooking;
        this.vehicleidclicklistener = vehicleidclicklistener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vehicle_item_layout, parent, false);
        VehicleInfoHolder vehicleInfoHolder = new VehicleInfoHolder(view);
        return vehicleInfoHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((VehicleInfoHolder) holder).bind(vehicleInfoList.get(position));
    }

    @Override
    public int getItemCount() {
        return vehicleInfoList.size();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
    }

    public class VehicleInfoHolder extends RecyclerView.ViewHolder {
        TextView vehicleName, vehicleDesc, vehicleCost;
        LinearLayout vehicleLayout;
        ImageView vehicleImg;
        double tot_amount;

        public VehicleInfoHolder(View itemView) {
            super(itemView);
            vehicleName = itemView.findViewById(R.id.vehicleName);
            vehicleDesc = itemView.findViewById(R.id.vehicleDesc);
            vehicleCost = itemView.findViewById(R.id.vehicleCost);
            vehicleLayout = itemView.findViewById(R.id.vehicleLayout);
            vehicleImg = itemView.findViewById(R.id.vehicleImg);
        }

        void bind(final VehicleInfo info) {

            tot_amount = Double.parseDouble(info.getRate().replaceAll(",", "")) * Double.parseDouble(info.getDistance().toString().replaceAll(",", ""));

            vehicleName.setText(info.getName());
            vehicleDesc.setText(info.getSpecs());
            vehicleCost.setText("$" + info.getTotalAmount());
            RequestOptions requestOptions = new RequestOptions();
            if (info.getCar_pic().isEmpty() || info.getCar_pic().equalsIgnoreCase("")) {
                Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(R.drawable.car_ride).into(vehicleImg);
            } else {
                Glide.with(getApplicationContext()).setDefaultRequestOptions(requestOptions).load(info.getCar_pic()).into(vehicleImg);
                //Picasso.get().load(info.getCar_pic()).into(vehicleImg);
            }
            if (clickId == info.getVehicle_id()) {
//                vehicleLayout.setBackgroundColor(context.getResources().getColor(R.color.Brown));
                vehicleLayout.setBackground(context.getDrawable(R.drawable.form_outline_background));
                clickId = "";
            } else {
                vehicleLayout.setBackgroundColor(0x00000000);
            }

            vehicleLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickId = info.getVehicle_id();
                    ride_now.setVisibility(View.VISIBLE);
                    if (checkedPosition != getAdapterPosition()) {
                        vehicleidclicklistener.onItemClicked(Integer.parseInt(info.getVehicle_id()), Double.parseDouble(info.getTotalAmount()), Double.parseDouble(info.getHold_amount()));
                        HomeFragment.getNotify();
                    }
                }
            });
        }
    }
}