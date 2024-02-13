package com.rideshare.app.adapter;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.custom.Utils;
import com.rideshare.app.fragement.AcceptedDetailFragment;
import com.rideshare.app.fragement.PaymentFragment;
import com.rideshare.app.fragement.RideSummery;
import com.rideshare.app.pojo.PendingRequestPojo;
import com.rideshare.app.pojo.spend.PendingPojo;

/**
 * Created by android on 8/3/17.
 */

public class AcceptedRequestAdapter extends RecyclerView.Adapter<AcceptedRequestAdapter.Holder> {
    List<PendingPojo> list;
    Utils utils = new Utils();

    public AcceptedRequestAdapter(List<PendingPojo> list) {
        this.list = list;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.acceptedrequest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final PendingPojo pojo = list.get(position);
        holder.from_add.setText(pojo.getPickupAdress());
        holder.to_add.setText(pojo.getDropAddress());
        holder.drivername.setText(pojo.getDriverLastname());
        holder.time.setText(pojo.getTime());

        if (pojo.getStatus().equalsIgnoreCase("Completed") && (pojo.getPaymentStatus().equals("") || (pojo.getPaymentStatus().equalsIgnoreCase("pending")))) {
            holder.payButton.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
        } else {
            holder.payButton.setVisibility(View.GONE);
            holder.view.setVisibility(View.GONE);
        }
        holder.payButton.setOnClickListener(e -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", pojo);
            PaymentFragment paymentFragment = new PaymentFragment();
            paymentFragment.setArguments(bundle);
            ((HomeActivity) holder.itemView.getContext()).changeFragment(paymentFragment, "Payment Method");
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                if (pojo.getStatus().equalsIgnoreCase("Completed")) {
                    bundle.putSerializable("data", pojo);
                    RideSummery rideFragment = new RideSummery();
                    rideFragment.setArguments(bundle);
                    ((HomeActivity) holder.itemView.getContext()).changeFragment(rideFragment, "Passenger Information");
                } else {
                    bundle.putSerializable("data", pojo);
                    AcceptedDetailFragment detailFragment = new AcceptedDetailFragment();
                    detailFragment.setArguments(bundle);
                    ((HomeActivity) holder.itemView.getContext()).changeFragment(detailFragment, "Passenger Information");
                }
            }
        });
//        BookFont(holder, holder.f);

//        MediumFont(holder, holder.date);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setData(List<PendingPojo> pendingRequestPojos) {
        this.list = pendingRequestPojos;
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder {


        TextView drivername, from_add, to_add, date, time;
        TextView f, t, dn, dt;
        View view;
        Button payButton;

        public Holder(View itemView) {
            super(itemView);

            f = (TextView) itemView.findViewById(R.id.from);
            t = (TextView) itemView.findViewById(R.id.to);

            dn = (TextView) itemView.findViewById(R.id.drivername);
            dt = (TextView) itemView.findViewById(R.id.datee);


            drivername = (TextView) itemView.findViewById(R.id.txt_drivername);
            from_add = (TextView) itemView.findViewById(R.id.txt_from_add);
            to_add = (TextView) itemView.findViewById(R.id.txt_to_add);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            payButton = itemView.findViewById(R.id.btn_pay_complete_ride);
            payButton.setVisibility(View.GONE);
            view = itemView.findViewById(R.id.view);
            view.setVisibility(View.GONE);
        }
    }

    public void BookFont(Holder holder, TextView view1) {
        Typeface font1 = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(Holder holder, TextView view) {
        Typeface font = Typeface.createFromAsset(holder.itemView.getContext().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }
}
