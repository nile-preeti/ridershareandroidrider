package com.rideshare.app.fragement;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.pojo.Pass;
import com.rideshare.app.Server.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.paypal.android.sdk.payments.PayPalService;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

//requestFragment
public class RequestFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback {

    View view;
    AppCompatButton confirm, cancel;
    TextView pickup_location, drop_location;
    Double finalfare;
    MapView mapView;
    private Double fare;
    GoogleMap myMap;
    AlertDialog alert;
    private LatLng origin;
    private LatLng destination;
    private String networkAvailable;
    private String tryAgain;
    private String directionRequest;
    TextView textView1, textView2, textView3, textView4, textView5, txt_name, txt_number, txt_fare, title, txt_vehiclename;

    String driver_id;
    private String user_id;
    private String pickup_address;
    private String drop_address;
    String distance;
    private String drivername = "";
    SwipeRefreshLayout swipeRefreshLayout;
    Pass pass;
    TextView calculateFare;
    Snackbar snackbar;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        networkAvailable = getResources().getString(R.string.network);
        tryAgain = getResources().getString(R.string.try_again);
        directionRequest = getResources().getString(R.string.direction_request);
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.request_ride, container, false);
        setHasOptionsMenu(true);
        if (!CheckConnection.haveNetworkConnection(getActivity())) {
            Toast.makeText(getActivity(), networkAvailable, Toast.LENGTH_LONG).show();
        }
        bindView(savedInstanceState);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!CheckConnection.haveNetworkConnection(getActivity())) {
                    Toast.makeText(getActivity(), networkAvailable, Toast.LENGTH_LONG).show();
                } else {
                    if (distance == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_distance), Toast.LENGTH_LONG).show();
                    } else if (pickup_address == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_pickupaddress), Toast.LENGTH_SHORT).show();
                    } else if (drop_address == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_dropaddress), Toast.LENGTH_SHORT).show();
                    } else if (fare == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_fare), Toast.LENGTH_SHORT).show();
                    } else if (origin == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_pickuplocation), Toast.LENGTH_SHORT).show();
                    } else if (destination == null) {
                        Toast.makeText(getActivity(), getString(R.string.invalid_droplocation), Toast.LENGTH_SHORT).show();
                    } else {
                        String o = origin.latitude + "," + origin.longitude;
                        String d = destination.latitude + "," + destination.longitude;
                        AddRide(SessionManager.getKEY(), pickup_address, drop_address, o, d,
                                String.valueOf(finalfare), distance);
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), HomeActivity.class));

            }
        });
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        getActivity().stopService(new Intent(getActivity(), PayPalService.class));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void bindView(Bundle savedInstanceState) {
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.ride_request));
        //((HomeActivity) getActivity()).toolbar.setTitle(getString(R.string.request_ride));
        mapView = (MapView) view.findViewById(R.id.mapview);
        calculateFare = (TextView) view.findViewById(R.id.txt_calfare);
        confirm = (AppCompatButton) view.findViewById(R.id.btn_confirm);
        cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickup);
        drop_location = (TextView) view.findViewById(R.id.txt_drop);
        textView1 = (TextView) view.findViewById(R.id.textView1);
        textView2 = (TextView) view.findViewById(R.id.textView2);
        textView3 = (TextView) view.findViewById(R.id.textView3);
        textView4 = (TextView) view.findViewById(R.id.textView4);
        textView5 = (TextView) view.findViewById(R.id.textView5);
        txt_name = (TextView) view.findViewById(R.id.txt_name);
        txt_number = (TextView) view.findViewById(R.id.txt_number);
        txt_fare = (TextView) view.findViewById(R.id.txt_fare);
        txt_vehiclename = (TextView) view.findViewById(R.id.txt_vehiclename);
        title = (TextView) view.findViewById(R.id.title);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        Typeface book = Typeface.createFromAsset(getContext().getAssets(), "font/AvenirLTStd_Book.otf");
        title.setTypeface(book);
        cancel.setTypeface(book);
        confirm.setTypeface(book);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

       setData();
        overrideFonts(getActivity(), view);
        user_id = SessionManager.getUserId();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }

    //setting data to view
    private void setData() {
        Bundle bundle = getArguments();
        pass = new Pass();
        if (bundle != null) {
            pass = (Pass) bundle.getSerializable("data");
            if (pass != null) {
                origin = pass.getFromPlace().getLatLng();
                destination = pass.getToPlace().getLatLng();
                driver_id = pass.getDriverId();
                fare = Double.valueOf(pass.getFare());
                drivername = pass.getDriverName();
                pickup_address = pass.getFromPlace().getAddress().toString();
                drop_address = pass.getToPlace().getAddress().toString();
                if (drivername != null) {
                    txt_name.setText(drivername);
                }
                pickup_location.setText(pickup_address);
                drop_location.setText(drop_address);
                txt_vehiclename.setText(pass.getVehicleName() + "");
            }
        }
    }

    //on Direction
    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (getActivity() != null) {
            if (direction.isOK()) {
                ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                myMap.addPolyline(DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.RED));
                myMap.addMarker(new MarkerOptions().position(new LatLng(origin.latitude, origin.longitude)).title("Pickup Location").snippet(pickup_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                myMap.addMarker(new MarkerOptions().position(new LatLng(destination.latitude, destination.longitude)).title("Drop Location").snippet(drop_address).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin, 10));

                calculateDistance(Double.valueOf(direction.getRouteList().get(0).getLegList().get(0).getDistance().getValue()) / 1000);

            } else {
                distanceAlert(direction.getErrorMessage());
                //calculateFare.setVisibility(View.GONE);
                dismiss();
            }


        }


    }

    //direction failure
    @Override
    public void onDirectionFailure(Throwable t) {
        distanceAlert(t.getMessage() + "\n" + t.getLocalizedMessage() + "\n");
        //  calculateFare.setVisibility(View.GONE);
        dismiss();
    }

    //map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        setData();
        //your code here
        new Handler().postDelayed (this::requestDirection, 2000);


    }

    //requestDirection
    public void requestDirection() {

        snackbar = Snackbar.make(view, getString(R.string.fare_calculating), Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
        GoogleDirection.withServerKey(getString(R.string.google_android_map_api_key))
                .from(origin)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .alternativeRoute(true)
                .execute(this);

        confirm.setEnabled(false);
    }

    //Fonts
    private void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                ((TextView) v).setTypeface(Typeface.createFromAsset(context.getAssets(), "font/AvenirLTStd_Medium.otf"));
            }
        } catch (Exception e) {
        }
    }

    //adding Ride
    public void AddRide(String key, String pickup_adress, String drop_address, String pickup_location, String drop_locatoin, String amount, String distance) {
        final RequestParams params = new RequestParams();
        params.put("driver_id", driver_id);
        params.put("user_id", user_id);
        params.put("pickup_adress", pickup_adress);
        params.put("drop_address", drop_address);
        params.put("pikup_location", pickup_location);
        params.put("drop_locatoin", drop_locatoin);
        params.put("amount", amount);
        params.put("distance", distance);
        Server.setHeader(key);
        Server.post("api/user/addRide/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {
                        Toast.makeText(getActivity(), getString(R.string.ride_has_been_requested), Toast.LENGTH_LONG).show();
                        ((HomeActivity) getActivity()).changeFragment(new HomeFragment(), "Home");
                    } else {
                        Toast.makeText(getActivity(), tryAgain, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), tryAgain, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    swipeRefreshLayout.setRefreshing(false);

                }

            }
        });
    }

    //calculating distance
    public void calculateDistance(Double aDouble) {
        distance = String.valueOf(aDouble);
        if (aDouble != null) {
            if (fare != null && fare != 0.0) {
                DecimalFormat dtime = new DecimalFormat("##.##");
                Double ff = aDouble * fare;

                try {

                    if (dtime.format(ff).contains(",")) {
                        String value = dtime.format(ff).replaceAll(",", ".");
                        finalfare = Double.valueOf(value);
                    } else {

                        finalfare = Double.valueOf(dtime.format(ff));
                    }
                    dismiss();

                } catch (Exception e) {

                }

                txt_fare.setText(finalfare + " " + SessionManager.getUnit());

            } else {
                txt_fare.setText(SessionManager.getUnit());
            }
        }
        confirm.setEnabled(true);


    }

//distacnce alert dialog
    public void distanceAlert(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(getString(R.string.INVALID_DISTANCE));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        alertDialog.setIcon(drawable);


        alertDialog.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        alert = alertDialog.create();
        alert.show();
    }

    private void dismiss() {
        if (snackbar != null) {
            snackbar.dismiss();

        }
    }
}
