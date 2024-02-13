package com.rideshare.app.fragement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rideshare.app.pojo.spend.PendingPojo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.GeoPoint;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.GPSTracker;
import com.rideshare.app.pojo.Tracking;
import com.rideshare.app.Server.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.paypal.android.sdk.payments.PayPalAuthorization;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalFuturePaymentActivity;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.squareup.picasso.Picasso;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rideshare.app.fragement.HomeFragment.MediaRecorderReady;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

/**
 * Created by android on 14/3/17.
 */

//Accept details
public class AcceptedDetailFragment extends FragmentManagePermission
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {
    private View view;
    AppCompatButton trackRide;
    ImageView backBtn;
    private String mobile = "";
    AppCompatButton btn_cancel, btn_payment, btn_complete;
    LinearLayout linearChat;
    TextView title, drivername, mobilenumber, pickup_location, drop_location, fare, timeandate, payment_status, pause_recording_txt,
            txtAmount, txtDistance, txtStatus, txtDateTime;
    private AlertDialog alert;
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final String CONFIG_ENVIRONMENT = Server.ENVIRONMENT;
    private static PayPalConfiguration config;
    PayPalPayment thingToBuy;
    TableRow mobilenumber_row;
    PendingPojo pojo;
    String permissions[] = {PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private com.google.android.gms.maps.MapView mapView;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private SwipeRefreshLayout swipeRefreshLayout;
    AcceptedRequestFragment fragment = new AcceptedRequestFragment();
    LinearLayout play_recording_img, stop_recording_img, pause_recording_img;
    private ImageView imageViewMap;
    private String STATIC_MAP_API_ENDPOINT = "http://maps.googleapis.com/maps/api/staticmap?size=230x200&path=", path;
    MediaPlayer mediaPlayer;
    private GoogleMap mMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.accepted_detail_fragmnet, container, false);
//        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.passenger_info));
//        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        setHasOptionsMenu(true);
        BindView();
//        showStaticMap();
//        configPaypal();
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setClickable(false);
        mapView.setActivated(false);
        mapView.setEnabled(false);
        mapView.onResume();
        trackRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    //showing static map
    public void showStaticMap() {
        try {

            String marker_me = "color:orange|label:1|Brisbane";
            String marker_dest = "color:orange|label:7|San Francisco,USA";
            marker_me = URLEncoder.encode(marker_me, "UTF-8");

            marker_dest = URLEncoder.encode(marker_dest, "UTF-8");
            path = "weight:3|color:blue|geodesic:true|Brisbane,Australia|Hong Kong|Moscow,Russia|London,UK|Reyjavik,Iceland|New York,USA|San Francisco,USA";
            path = URLEncoder.encode(path, "UTF-8");


            STATIC_MAP_API_ENDPOINT = STATIC_MAP_API_ENDPOINT + path + "&markers=" + marker_me + "&markers=" + marker_dest + "&key=" + R.string.google_android_map_api_key;

            Log.d("STATICMAPS", STATIC_MAP_API_ENDPOINT);


            String temp = "http://maps.googleapis.com/maps/api/staticmap?center=-15,-47&zoom=11&size=600x200&key=" + R.string.google_android_map_api_key;

            Picasso.with(requireContext()).load(temp).error(R.drawable.ic_map_error).into(imageViewMap);

//            AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>(){
//                @Override
//                protected Bitmap doInBackground(Void... params) {
//                    Bitmap bmp = null;
//                    HttpClient httpclient = new DefaultHttpClient();
//                    HttpGet request = new HttpGet(STATIC_MAP_API_ENDPOINT);
//
//                    InputStream in = null;
//                    try {
//                        HttpResponse response = httpclient.execute(request);
//                        in = response.getEntity().getContent();
//                        bmp = BitmapFactory.decodeStream(in);
//                        in.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return bmp;
//                }
//                protected void onPostExecute(Bitmap bmp) {
//                    if (bmp!=null) {
//
//                        imageViewMap.setImageBitmap(bmp);
//
//                    }
//
//                }
//            };
//
//            setImageFromUrl.execute();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void BindView() {

        btn_complete = (AppCompatButton) view.findViewById(R.id.btn_complete);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mobilenumber_row = (TableRow) view.findViewById(R.id.mobilenumber_row);
        linearChat = (LinearLayout) view.findViewById(R.id.linear_chat);
        title = (TextView) view.findViewById(R.id.title);
        drivername = (TextView) view.findViewById(R.id.driver_name);
        mobilenumber = (TextView) view.findViewById(R.id.txt_mobilenumber);
        pickup_location = (TextView) view.findViewById(R.id.txt_pickuplocation);
        drop_location = (TextView) view.findViewById(R.id.txt_droplocation);
        fare = (TextView) view.findViewById(R.id.txt_basefare);
        timeandate = (TextView) view.findViewById(R.id.timeandate);
        trackRide = (AppCompatButton) view.findViewById(R.id.btn_trackride);
        btn_payment = (AppCompatButton) view.findViewById(R.id.btn_payment);
        btn_cancel = (AppCompatButton) view.findViewById(R.id.btn_cancel);
        payment_status = (TextView) view.findViewById(R.id.txt_paymentstatus);
        play_recording_img = view.findViewById(R.id.play_recording_img);
        stop_recording_img = view.findViewById(R.id.stop_recording_img);
        pause_recording_img = view.findViewById(R.id.pause_recording_img);
        pause_recording_txt = view.findViewById(R.id.pause_recording_txt);
        pickup_location.setSelected(true);
        drop_location.setSelected(true);
        backBtn = view.findViewById(R.id.backBtn);
        mapView = view.findViewById(R.id.map_accept_detail_fragment);
        imageViewMap = view.findViewById(R.id.img_map);

        txtAmount = view.findViewById(R.id.txt_amount_1);
        txtDistance = view.findViewById(R.id.txt_distance);
        txtStatus = view.findViewById(R.id.txt_status);
        txtDateTime = view.findViewById(R.id.txt_date_and_time);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().popBackStack();
                if (getActivity() != null) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                }
                try {

                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        pause_recording_txt.setText("Pause");
                        pause_recording_img.setVisibility(View.GONE);
                        stop_recording_img.setVisibility(View.GONE);
                        play_recording_img.setVisibility(View.VISIBLE);
                        MediaRecorderReady();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        Bundle bundle = getArguments();
        if (bundle != null) {
            pojo = (PendingPojo) bundle.getSerializable("data");
            title.setText(pojo.getVehicleTypeName());
            pickup_location.setText(pojo.getPickupAdress() + " ");

            drop_location.setText(pojo.getDropAddress());
            drivername.setText(pojo.getDriverLastname());
            //  Log.d("audio", pojo.getAudio().get(0).getAudio());
            fare.setText("$ " + pojo.getAmount());
            timeandate.setText(pojo.getTime());

            txtAmount.setText(pojo.getAmount().trim());
            txtDistance.setText(pojo.getDistance() + " miles.");
            txtStatus.setText(pojo.getStatus());
            txtDateTime.setText(pojo.getTime());
            payment_status.setText(pojo.getPaymentStatus());
            //+ " " + SessionManager.getUnit());
            mobilenumber.setText(pojo.getDriverMobile());
            mobile = pojo.getDriverMobile();
            mobilenumber_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    askCompactPermission(PermissionUtils.Manifest_CALL_PHONE, new PermissionResult() {
                        @Override
                        public void permissionGranted() {

                            if (mobile != null && !mobile.equals("")) {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:" + mobile));
                                startActivity(callIntent);
                            }
                        }

                        @Override
                        public void permissionDenied() {

                        }

                        @Override
                        public void permissionForeverDenied() {

                        }
                    });
                }
            });

            play_recording_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mediaPlayer = new MediaPlayer();

                    try {
                        String file = pojo.getAudio().get(0).getAudio();
                        Log.d("file", file);
                        mediaPlayer.setDataSource(String.valueOf(file));
                        stop_recording_img.setVisibility(View.VISIBLE);
                        play_recording_img.setVisibility(View.GONE);
                        pause_recording_img.setVisibility(View.VISIBLE);
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.start();
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            stop_recording_img.setVisibility(View.GONE);
                            play_recording_img.setVisibility(View.VISIBLE);
                            pause_recording_img.setVisibility(View.GONE);

                        }
                    });
                }
            });

            pause_recording_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        pause_recording_txt.setText("Resume");
                    } else {
                        pause_recording_txt.setText("Pause");
                        mediaPlayer.start();
                    }
                }
            });

            stop_recording_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {

                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            mediaPlayer.release();
                            pause_recording_txt.setText("Pause");
                            pause_recording_img.setVisibility(View.GONE);
                            stop_recording_img.setVisibility(View.GONE);
                            play_recording_img.setVisibility(View.VISIBLE);
                            MediaRecorderReady();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            if (pojo.getStatus().equalsIgnoreCase("PENDING")) {
                btn_cancel.setVisibility(View.VISIBLE);
                play_recording_img.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);

            }
            if (pojo.getStatus().equalsIgnoreCase("CANCELLED")) {
                btn_complete.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);
                play_recording_img.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);
                payment_status.setText(pojo.getPaymentStatus());
            }
            if (pojo.getStatus().equalsIgnoreCase("COMPLETED")) {
                btn_payment.setVisibility(View.GONE);
                trackRide.setVisibility(View.GONE);
                btn_cancel.setVisibility(View.GONE);
                btn_complete.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);

//                if (pojo.getAudio().size() == 0) {
//                    play_recording_img.setVisibility(View.GONE);
//                } else {
//                    play_recording_img.setVisibility(View.VISIBLE);
//
//                }
                payment_status.setText(pojo.getPaymentStatus());
            }
            if (pojo.getStatus().equalsIgnoreCase("ACCEPTED")) {
                play_recording_img.setVisibility(View.GONE);
                mapView.setVisibility(View.GONE);
                if (pojo.getPaymentStatus().equals("") && pojo.getPaymentStatus().equals("")) {

                    btn_cancel.setVisibility(View.GONE);
                    trackRide.setVisibility(View.GONE);
                    btn_payment.setVisibility(View.GONE);
                } else {
                    btn_complete.setVisibility(View.GONE);
                    trackRide.setVisibility(View.GONE);
                    mobilenumber_row.setVisibility(View.GONE);
                }
                if (!pojo.getPaymentStatus().equals("PAID") && pojo.getPaymentStatus().equals("OFFLINE")) {

                    btn_complete.setVisibility(View.GONE);
                } else {
                }
            }

            if (pojo.getPaymentStatus().equals("") && pojo.getPaymentStatus().equals("")) {
                payment_status.setText(getString(R.string.unpaid));

            } else {
                payment_status.setText(pojo.getPaymentStatus());

            }
            if (!pojo.getPaymentStatus().equals("PAID") && pojo.getPaymentMode().equals("OFFLINE")) {
                payment_status.setText(R.string.cash_on_hand);

            } else {
                payment_status.setText(pojo.getPaymentStatus());
            }
        }

//        SetCustomFont setCustomFont = new SetCustomFont();
//        setCustomFont.overrideFonts(getActivity(), view);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        isStarted();

       /* linearChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("name", pojo.getDriver_name());
                b.putString("id", pojo.getRide_id());
                b.putString("user_id", pojo.getDriver_id());
                ChatFragment chatFragment = new ChatFragment();
                chatFragment.setArguments(b);
                ((HomeActivity) getActivity()).changeFragment(chatFragment, "Messages");
            }
        });*/


        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getActivity())) {

                    new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.payment_method)).setItems(R.array.payment_mode, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                RequestParams params = new RequestParams();
                                params.put("ride_id", pojo.getRideId());
                                params.put("payment_mode", "OFFLINE");
                                Server.setContetntType();
                                Server.setHeader(SessionManager.getKEY());
                                Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
                                    @Override
                                    public void onStart() {
                                        swipeRefreshLayout.setRefreshing(true);
                                    }

                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                        super.onSuccess(statusCode, headers, response);
                                        pojo.setPaymentMode("OFFLINE");
                                        if (pojo.getPaymentMode().equals("OFFLINE")) {
                                            payment_status.setText(R.string.cash_on_hand);
                                        } else {
                                            payment_status.setText(pojo.getPaymentStatus());
                                        }

                                        btn_payment.setVisibility(View.GONE);
                                        trackRide.setVisibility(View.GONE);
                                        Toast.makeText(getActivity(), getString(R.string.payment_update), Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                        super.onFailure(statusCode, headers, responseString, throwable);

                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                        if (getActivity() != null) {
                                            swipeRefreshLayout.setRefreshing(false);
                                        }
                                    }
                                });

                            } else {
                                MakePayment();
                            }
                        }
                    }).create().show();

                    //MakePayment();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialogCreate(getString(R.string.ride_request_cancellation), getString(R.string.want_to_cancel), "CANCELLED");
            }
        });


        btn_complete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialogCreate(getString(R.string.ride_request_cancellation), getString(R.string.want_to_accept), "COMPLETED");
                    }
                });
    }




    @Override
    public void onDetach() {
        super.onDetach();
        try {

            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                /*pause_recording_txt.setText("Pause");
                pause_recording_img.setVisibility(View.GONE);
                stop_recording_img.setVisibility(View.GONE);
                play_recording_img.setVisibility(View.VISIBLE);*/
                MediaRecorderReady();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //updating Payment
    public void Updatepayemt(String ride_id, String payment_status) {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("payment_status", payment_status);
        params.put("payment_mode", "PAYPAL");
        Server.setContetntType();
        Server.setHeader(SessionManager.getKEY());

        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(getActivity(), getString(R.string.payment_update), Toast.LENGTH_LONG).show();
                ((HomeActivity) getActivity()).onBackPressed();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                Toast.makeText(getActivity(), getString(R.string.error_payment), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.server_not_respond), Toast.LENGTH_LONG).show();

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

//alert dialog
    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new AlertDialog.Builder(getActivity())
                .setIcon(drawable)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // sendStatus(pojo.getRide_id(), status);
                        cancelRequest(pojo.getRideId());

                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    //cancel Alert
    public void cancelAlert(String title, String message, final String status) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(title);

        alertDialog.setMessage(message);
        alertDialog.setCancelable(true);
        Drawable drawable = ContextCompat.getDrawable(getActivity(), R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        alertDialog.setIcon(drawable);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendStatus(pojo.getRideId(), status);
            }
        });

        alertDialog.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.cancel();
            }
        });
        alert = alertDialog.create();
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.show();
    }

    //sending ride Status
    public void sendStatus(String ride_id, final String status) {

        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        params.put("status", status);

        Server.setHeader(SessionManager.getKEY());
        Server.setContetntType();
        Server.post("api/user/rides", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
                    Bundle bundle = null;
                    //if (response.has("status") && response.getString("status").equals("success")) {
                    if (status.equalsIgnoreCase("COMPLETED")) {
                        Toast.makeText(getActivity(), getString(R.string.ride_request_completed), Toast.LENGTH_LONG).show();
                        bundle = new Bundle();
                        bundle.putString("status", "COMPLETED");
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.ride_request_cancelled), Toast.LENGTH_LONG).show();
                        bundle = new Bundle();
                        bundle.putString("status", "CANCELLED");
                    }
                    acceptedRequestFragment.setArguments(bundle);
                    ((HomeActivity) getActivity()).changeFragment(acceptedRequestFragment, "Accepted Request");
                   /* } else {
                        String error = response.getString("data");
                        Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
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

    //paypal configrations
    public void configPaypal() {
        config = new PayPalConfiguration()
                .environment(CONFIG_ENVIRONMENT)
                .clientId(Server.PAYPAL_KEY)
                .merchantName(getString(R.string.merchant_name))
                .merchantPrivacyPolicyUri(
                        Uri.parse(getString(R.string.merchant_privacy)))
                .merchantUserAgreementUri(
                        Uri.parse(getString(R.string.merchant_user_agreement)));
    }

    //making payment
    public void MakePayment() {

        if (pojo.getAmount() != null && !pojo.getAmount().equals("")) {
            Intent intent = new Intent(getActivity(), PayPalService.class);
            intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
            getActivity().startService(intent);
            thingToBuy = new PayPalPayment(new java.math.BigDecimal(String.valueOf(pojo.getAmount())),
                    getString(R.string.paypal_payment_currency), "Ride Payment", PayPalPayment.PAYMENT_INTENT_SALE);
            Intent payment = new Intent(getActivity(),
                    PaymentActivity.class);

            payment.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);
            payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

            startActivityForResult(payment, REQUEST_CODE_PAYMENT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                //  String.valueOf(finalfare)
                PaymentConfirmation confirm = data
                        .getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        System.out.println(confirm.toJSONObject().toString(4));
                        System.out.println(confirm.getPayment().toJSONObject()
                                .toString(4));
                        Updatepayemt(pojo.getRideId(), "PAID");

                    } catch (JSONException e) {

                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.payment_hbeen_cancelled), Toast.LENGTH_LONG).show();
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                //  Log.d("payment", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        } else if (requestCode == REQUEST_CODE_FUTURE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PayPalAuthorization auth = data
                        .getParcelableExtra(PayPalFuturePaymentActivity.EXTRA_RESULT_AUTHORIZATION);
                if (auth != null) {
                    try {
                        Log.i("FuturePaymentExample", auth.toJSONObject()
                                .toString(4));

                        String authorization_code = auth.getAuthorizationCode();
                        Log.d("FuturePaymentExample", authorization_code);

                        /*sendAuthorizationToServer(auth);
                        Toast.makeText(getActivity(),
                                "Future Payment code received from PayPal",
                                Toast.LENGTH_LONG).show();*/
                        Log.e("paypal", "future Payment code received from PayPal  :" + authorization_code);

                    } catch (JSONException e) {
                        Toast.makeText(getActivity(), "failure Occurred", Toast.LENGTH_LONG).show();
                        Log.e("FuturePaymentExample",
                                "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), getString(R.string.payment_hbeen_cancelled), Toast.LENGTH_LONG).show();

                Log.d("FuturePaymentExample", "The user canceled.");
            } else if (resultCode == PayPalFuturePaymentActivity.RESULT_EXTRAS_INVALID) {

                Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                Log.d("FuturePaymentExample",
                        "Probably the attempt to previously start the PayPalService had an invalid PayPalConfiguration. Please see the docs.");
            }
        }

    }

    //Gps enabled
    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;

        } else {
            return false;
        }
    }

    //turning on Gps
    public void turnonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result
                            .getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.

                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and setting the result in onActivityResult().
                                status.startResolutionForResult(getActivity(), 1000);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {

                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

//tracking driver
    private void isStarted() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Tracking/" + pojo.getRideId());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.hasChildren()) {
                    Tracking tracking = dataSnapshot.getValue(Tracking.class);

                    if (tracking != null && getActivity() != null) {
                        switch (tracking.getStatus()) {
                            case "accepted":
                                trackRide.setText(getString(R.string.Track_Driver));

                                trackRide.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        askCompactPermissions(permissions, new PermissionResult() {
                                            @Override
                                            public void permissionGranted() {
                                                if (GPSEnable()) {

                                                    Bundle bundle = new Bundle();
                                                    bundle.putSerializable("data", pojo);
                                                    MapView mapView = new MapView();
                                                    mapView.setArguments(bundle);
                                                    ((HomeActivity) getActivity()).changeFragment(mapView, "MapView");


                                                } else {
                                                    turnonGps();
                                                }
                                            }

                                            @Override
                                            public void permissionDenied() {

                                            }

                                            @Override
                                            public void permissionForeverDenied() {

                                            }
                                        });
                                    }
                                });
                                break;
                            case "started":
                                trackRide.setText(getString(R.string.Track_Ride));

                                trackRide.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        askCompactPermissions(permissions, new PermissionResult() {
                                            @Override
                                            public void permissionGranted() {
                                                if (GPSEnable()) {

                                                    try {
                                                        String[] latlong = pojo.getPikupLocation().split(",");
                                                        double latitude = Double.parseDouble(latlong[0]);
                                                        double longitude = Double.parseDouble(latlong[1]);
                                                        String[] latlong1 = pojo.getDropLocatoin().split(",");
                                                        double latitude1 = Double.parseDouble(latlong1[0]);
                                                        double longitude1 = Double.parseDouble(latlong1[1]);

                                                        Point origin = Point.fromLngLat(longitude, latitude);
                                                        Point destination = Point.fromLngLat(longitude1, latitude1);

                                                        fetchRoute(origin, destination);


                                                    } catch (Exception e) {
                                                        Toast.makeText(getActivity(), e.toString() + " ", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    turnonGps();
                                                }
                                            }

                                            @Override
                                            public void permissionDenied() {

                                            }

                                            @Override
                                            public void permissionForeverDenied() {

                                            }
                                        });
                                    }
                                });
                                break;
                        }
                    }


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

//fetching route
    private void fetchRoute(Point origin, Point destination) {
        NavigationRoute.builder(getActivity())
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        DirectionsRoute directionsRoute = response.body().routes().get(0);
                        startNavigation(directionsRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable t) {

                    }
                });
    }

    //starting navigation
    private void startNavigation(DirectionsRoute directionsRoute) {
        NavigationLauncherOptions.Builder navigationLauncherOptions = NavigationLauncherOptions.builder();
        navigationLauncherOptions.shouldSimulateRoute(false);
        navigationLauncherOptions.enableOffRouteDetection(true);
        navigationLauncherOptions.snapToRoute(true);
        navigationLauncherOptions.directionsRoute(directionsRoute);
        NavigationLauncher.startNavigation(getActivity(), navigationLauncherOptions.build());


    }

    //canceling ride
    private void cancelRequest(String ride_id) {

        String url = Server.BASE_URL.concat("accept_ride");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    Log.e("Response", "onResponse = \n " + response);

                    if (data.has("status") && data.getBoolean("status")) {
                        Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response ", "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                try {
                    params.put("driver_id", SessionManager.getUserId());
                    params.put("ride_id", ride_id);
                    params.put("status", "CANCELLED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth = "Bearer " + SessionManager.getKEY();
                headers.put("Authorization", auth);
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if(!pojo.getStatus().isEmpty()){
            if (mGoogleApiClient != null) {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                    mGoogleApiClient.disconnect();
                }
            }
            return;
        }
        try {
            mMap = googleMap;
            mMap.getUiSettings().setScrollGesturesEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setZoomGesturesEnabled(false);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            LatLng point1 = getLocationFromAddress(requireContext(), pojo.getPikupLocation());
            LatLng point2 = getLocationFromAddress(requireContext(), pojo.getDropLocatoin());

            double latitude = point1.latitude;
            double longitude = point1.longitude;
            double latitude1 = point2.latitude;
            double longitude1 = point2.longitude;

            Log.e("PJ", longitude + ", " + latitude);
            LatLng coordinates = new LatLng(latitude, longitude);

            double dummy_radius = distance(latitude, longitude, latitude1, longitude1);

            double circleRad = dummy_radius * 3000;//multiply by 1000 to make units in KM

            float zoomLevel = getZoomLevel(circleRad);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoomLevel));


            if (pojo != null && mMap != null) {
                mMap.addMarker(
                        new MarkerOptions()
                                .position(new LatLng(latitude, longitude)).snippet(pojo.getPickupAdress()).
                                title("Pickup Location").icon(BitmapDescriptorFactory
                                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                mMap.addMarker(
                        new MarkerOptions().snippet(pojo.getDropAddress())
                                .position(new LatLng(latitude1, longitude1)).title("Drop Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                Polyline line = mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(latitude, longitude),
                                new LatLng(latitude1, longitude1))
                        .width(5)
                        .color(Color.BLUE));
            }
        } catch (Exception ex) {
            Log.e("PJ", "Exception " + ex.toString());
        }
    }


    //getting location from address
    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    public GeoPoint getLocationFromAddress(String strAddress) throws IOException {

        Geocoder coder = new Geocoder(requireContext());
        List<Address> address;
        GeoPoint p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));

            return p1;
        } catch (Exception ex) {

        }
        return null;
    }

    //set zoom level
    private int getZoomLevel(double radius) {
        double scale = radius / 500;
        return ((int) (16 - Math.log(scale) / Math.log(2)));
    }

    //distance
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
