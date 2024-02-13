package com.rideshare.app.fragement;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.pojo.spend.PendingPojo;

public class RideSummery extends Fragment {
    private View view;
    private TextView totalAmount, ride_time, tv_drivenBy,tv_for_category,tv_for_vehicle, tv_rider_name, tv_total_amount, tv_trip_fare, tv_sub_total,
            tv_booking_fee, tv_tax_charge, tv_cancellation_fee, textView13;
    private PendingPojo pojo;
    private LinearLayout fragment;

    public RideSummery() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_ride_summery, container, false);
        ((HomeActivity) getActivity()).fontToTitleBar("Ride Summary");
        setHasOptionsMenu(true);
        BindView();
        textView13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(new BookingCharges(), "Booking");

//                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                fragmentTransaction.replace(R.id.fragment, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
            }
        });
        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }

    private void BindView() {
        totalAmount = view.findViewById(R.id.totalAmount);
        ride_time = view.findViewById(R.id.ride_time);
        tv_drivenBy = view.findViewById(R.id.tv_drivenBy);
        tv_for_category = view.findViewById(R.id.tv_for_category);
        tv_for_vehicle = view.findViewById(R.id.tv_for_vehicle);
        tv_rider_name = view.findViewById(R.id.tv_rider_name);
        tv_total_amount = view.findViewById(R.id.tv_total_amount);
        tv_trip_fare = view.findViewById(R.id.tv_trip_fare);
        tv_sub_total = view.findViewById(R.id.tv_sub_total);
        tv_booking_fee = view.findViewById(R.id.tv_booking_fee);
        tv_cancellation_fee = view.findViewById(R.id.tv_cancellation_fee);
        tv_tax_charge = view.findViewById(R.id.tv_tax_charge);
        textView13 = view.findViewById(R.id.textView13);
        fragment = view.findViewById(R.id.fragment);

        Bundle bundle = getArguments();
        if (bundle != null) {
            pojo = (PendingPojo) bundle.getSerializable("data");
            totalAmount.setText("$" + pojo.getAmount());
            ride_time.setText(pojo.getTime());
            tv_rider_name.setText("Thanks for riding, " + pojo.getUserName());
            tv_for_category.setText("For Category: " + pojo.getCategoryName()+",");
            tv_for_vehicle.setText("Vehicle: " + pojo.getVehicleName() + ",");
            tv_drivenBy.setText("Driven by " + pojo.getDriverLastname());
//            tv_drivenBy.setText("<font >For Category: " + pojo.getCategoryName() + ",\nVehicle: " + pojo.getVehicleName() + ", Driven by " + pojo.getDriverName());
            tv_total_amount.setText("$" + pojo.getAmount());
            tv_trip_fare.setText("$" + pojo.getTripFare());
            tv_sub_total.setText("$" + pojo.getSubtotal());
            tv_booking_fee.setText("$" + pojo.getBookingFee());
            tv_cancellation_fee.setText("$" + pojo.getCancellationCharge());
            tv_tax_charge.setText("$" + pojo.getTaxCharge());
        }
    }

    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//                    drawer_close();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    //   fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }

    }
}