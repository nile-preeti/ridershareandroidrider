package com.rideshare.app.fragement;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.solver.state.Dimension;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.model.Direction;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.rideshare.app.adapter.VehicleCategoryAdapter;
import com.rideshare.app.custom.AutoPayment;
import com.rideshare.app.custom.Constants;
import com.rideshare.app.custom.GetDumaWorkManager;
import com.rideshare.app.custom.Utils;
import com.rideshare.app.custom.VolleySingleton;
import com.rideshare.app.pojo.CallingDriverResponse;
import com.rideshare.app.pojo.Global;
import com.rideshare.app.pojo.UpdateLoginLogoutResponse;
import com.rideshare.app.pojo.google.DistanceMatrixResponse;
import com.rideshare.app.pojo.payment.CardDetail;
import com.rideshare.app.pojo.spend.PendingPojo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
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
import com.rideshare.app.Server.session.SessionManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.Duration;
import com.google.maps.model.EncodedPolyline;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.acitivities.MapTrackingActivity;
import com.rideshare.app.acitivities.TrackingActivity;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.DirectionsJSONParser;
import com.rideshare.app.custom.GPSTracker;
import com.rideshare.app.interfaces.VehicleTypeInterface;
import com.rideshare.app.pojo.NearbyData;
import com.rideshare.app.pojo.Pass;
import com.rideshare.app.pojo.VehicleInfo;
import com.rideshare.app.pojo.cancelRideCount.CancelRideCount;
import com.rideshare.app.pojo.changepassword.ChangePasswordResponse;
import com.rideshare.app.pojo.last_ride.LastRideData;
import com.google.maps.model.TravelMode;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rideshare.app.pojo.spend.VehicleCategory;
import com.rideshare.app.tracker.TimerService;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.rideshare.app.Server.session.SessionManager.getKEY;
import static com.rideshare.app.Server.session.SessionManager.getUserEmail;
import static com.rideshare.app.Server.session.SessionManager.getUserName;
import static com.rideshare.app.acitivities.MapTrackingActivity.MY_PERMISSIONS_REQUEST_LOCATION;
import static com.rideshare.app.custom.MyApplication.timerTask;

import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.rideshare.app.Server.session.SessionManager.initialize;
import static com.rideshare.app.pojo.Global.txnId;

//HoomeFragment
public class HomeFragment extends FragmentManagePermission implements OnMapReadyCallback, DirectionCallback, Animation.AnimationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static HomeFragment instance;
    private static final String TAG = "HomeFragment";
    private static String driver_id, distanceVal = "";
    static String total_rating;
    static int total_driver_ride;
    private String cost;
    private String unit;
    private boolean isRideCancelled = false;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude, currentLongitude;
    private static double destlat, destlng, sourceLat, sourceLng;
    private View rootView;
    private Boolean flag = false, apiIsRunning = false;
    private static boolean feedbackShown = false, tipSkipClicked = false, securityPopupShown = false;
    static GoogleMap myMap;
    static ImageView current_location, clear, driver_pic, car_pic, min_max;
    static ImageView driver_pics;
    EditText input_comment, input_tip_amount;
    String auth_token;
    String vehicleServiceId;
    // PlaceDetectionClient mPlaceDetectionClient;
    Button track;
    private static RelativeLayout header, footer, mainHeaderLayout, mainFooter, tip_layout;
    Animation animFadeIn, animFadeOut, animSlideUp, animeZoomIn, animeZoomOut;
    static TextView pickup_location, drop_location, txtDriverRating, txt_ride_amount, txt_cancellation_charge, txt_total, txt_total_title, txt_cancel_title, txt_tip_for, tip_pay_btn, tip_skip_btn, txt_surge_percentage;
    private View line;
    RelativeLayout relative_drop, center_location, linear_pickup;
    private static Button btnPayNow;
    static TextView txt_info, txt_cost, ride_now, vehicleNo, driverName, doneBtn, skipBtn;
    static TextView cnfrmBooking, cancelBooking, txtDriverName, txtstartDriverName, btnStartRecording, btnStopRecording, distance, time, txtAmount, speed;
    static LinearLayout linear_rideNow, linear_cnfrmBooking, linear_afterridecnfrm, completeRideLayout, startRideLayout, driver_details_layout;
    LinearLayout callDriverLayout, cancelRideLayout;
    ConstraintLayout paymentNowLayout;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    private String drivername;
    //MapView mMapView;
    SupportMapFragment mMapView;
    Pass pass;
    Place pickup, drop;
    ProgressBar progressBar;
    private PlacesClient placesClient;
    static Marker marker, marker1, marker2, destMarker;
    public String initialVal = "1";
    LatLng origin, dest;
    static Polyline polyline;
    public String vehicleTypeId;
    int vehicleId;
    RecyclerView vehicle_recyclerView;
    //    VehicleInfoAdapter adapter;
    private static VehicleCategoryAdapter adapter;
    LinearLayoutManager layoutManager;
    private List<VehicleInfo> data_lists = new ArrayList<>();
    private List<VehicleCategory> data_header_lists = new ArrayList<>();
    private static Context context = null;
    ArrayList markerPoints = new ArrayList();
    static Marker mCurrLocationMarker, driverDestinationMarker = null;
    public boolean firstTime = true;
    private LastRideData lastRideData;
    private String status = "";

    static int ride_id = 0;
    int req_ride_id = 0;
    Handler handler = new Handler();
    Runnable runnable;
    int delay = 10000;
    //Declare timer
    private CountDownTimer cTimer = null;

    private GoogleMap.OnCameraIdleListener onCameraIdleListener;
    LatLng latLng;
    static double lat, lng, driverLat, driverLong;
    Location mLastLocation;
    static String userEmail = "", driverEmail = "", userName = "";
    static String driverNames = "", amount = "", paymentStatus = "";
    private static HashMap<String, Marker> mMarkers = new HashMap<>();
    static Location locationCt;
    public static boolean check = true, checkTwo = true;
    String contactNo = "";
    double rate, holdAmount;
    static File AudioSavePathInDevice = null;
    static MediaRecorder mediaRecorder;
    static Random random;
    static String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    static String profileUrl, carPicUrl;
    MediaPlayer mediaPlayer, musicPlayer;
    RatingBar ratingBar;
    static String rating = "", comment = "", vehicleNumber;

    static String ratingvalue;
    static boolean checkStatus = true;
    public boolean failedRideStatus = false;

    int speedIs10MetersPerMinute = 30;
    static String distanceInMeters, distanceInKm, estimatedDriveTimeInMinutes, distanceInMiles;
    double distanceInMile;

    static DatabaseReference ref;

    static float bearing;

    ImageView cross, maximize, minimize;

    VideoView homeVideoView;
    Uri videoUri;
    String videoPath;
    MediaController mc;
    private boolean mIsActivityDone;
    private String locationType = "";
    private String cancelRideCharge = "";
    private ActivityResultLauncher<Intent> locationLauncher;

    private int height = 150;
    private int width = 220;
    private static Bitmap smallMarker;
    private static long mLastClickTime, lasthitTIme;
    ProgressBar acceptRideProgressbar;
    List<CardDetail> cardDetails;
    private RelativeLayout timerLayout;
    public static Handler timeHandler;
    public static Runnable timeRunnable;
    public static int seconds = 0;
    String timerTime = "00:00";
    TextView ride_timer;
    public static boolean isRunning = false;
    public static Intent serviceIntent;
    public static boolean canRun = true;

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  MapsInitializer.initialize(this.getActivity());
    }

    //BroadCast reciver
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver1", "Got message: " + message);
            getLastRide1();
            //getrideStatus_details();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter("custom-event-name"));

            rootView = inflater.inflate(R.layout.home_fragment, container, false);
            ((HomeActivity) getActivity()).fontToTitleBar("Book a Ride");
//            ((HomeActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));
            context = getActivity();
            instance = this;

            serviceIntent = new Intent(context, TimerService.class);
            timeHandler = new Handler();

            BitmapDrawable bitmapdraw = (BitmapDrawable) instance.getResources().getDrawable(R.drawable.car_ride);
            Bitmap b = bitmapdraw.getBitmap();
            smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            videoAlert = new Dialog(requireActivity(), R.style.BottomOptionsDialogTheme);
            videoAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
            videoAlert.setContentView(R.layout.video_view);
            mc = new MediaController(getActivity());

            videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.last_final_video;
            videoUri = Uri.parse(videoPath);

            Log.d("getPackageName", videoPath);

            updateLoginLogotApi();
            checkPermission();
            setHasOptionsMenu(true);
            bindView(savedInstanceState);
            if (!CheckConnection.haveNetworkConnection(getActivity())) {
                Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            }
            if (Build.VERSION.SDK_INT < 22) setStatusBarTranslucent(false);
            else setStatusBarTranslucent(true);
            configureCameraIdle();

            initialize(getActivity());
            userEmail = getUserEmail();
            userName = getUserName();
            Log.d("email", userEmail);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!GPSEnable()) {
                        tunonGps();
                    } else {
                        getcurrentlocation();
                    }
                }
                askCompactPermissions(permissionAsk, new PermissionResult() {
                    @Override
                    public void permissionGranted() {
                        if (!GPSEnable()) {
                            tunonGps();
                        } else {
                            getcurrentlocation();
                        }
                    }

                    @Override
                    public void permissionDenied() {
                    }

                    @Override
                    public void permissionForeverDenied() {
                        openSettingsApp(getActivity());
                    }
                });
            } else {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getcurrentlocation();
                }
            }

            locationLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Here, no request code
                        Intent data = result.getData();
                        switch (locationType) {
                            case "location_pickup":
                                setPickupLocation(data);
                                break;
                            case "location_drop":
                                setDropLocation(data);
                                break;
                        }
                    }
                }
            });
            pickup_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clear.performClick();
                    if (marker != null) {
                        marker.remove();
                    }
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(getActivity());
                    locationType = "location_pickup";
                    locationLauncher.launch(intent);
                }
            });

            drop_location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    failedRideStatus = false;
                    footer.setVisibility(View.GONE);
                    footer.setAnimation(animFadeOut);
                    drop_location.setFocusable(true);

                    if (!Places.isInitialized()) {
                        Log.e("LOC1", "initialized places api");
                        Places.initialize(requireActivity().getApplicationContext(), getResources().getString(R.string.google_android_map_api_key));
                    }
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(requireActivity());

                    locationType = "location_drop";
                    locationLauncher.launch(intent);
                }
            });


            initialize(getApplicationContext());
            Log.d("token", getKEY());
            track = rootView.findViewById(R.id.track);
            track.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), MapTrackingActivity.class);
                    startActivity(intent);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

        String active_ride = "";
        if (SessionManager.getActiveRideId() != null) {
            active_ride = SessionManager.getActiveRideId();
        }

        if (!active_ride.isEmpty()) {
            ride_id = Integer.parseInt(active_ride);
//            getrideStatus_details();
            getLastRide1();
        }

        if (checkStatus) {
            if (ride_id != 0) {
//                getrideStatus_details();
                getLastRide1();
            }
        }
        if (savedInstanceState != null) {
            seconds = savedInstanceState.getInt("seconds");
        }
        runTimer();
        //checkDeviceTokenApi();
        return rootView;
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.help);
        item.setVisible(true);
    }

    //work manager
    private void setupWorkManager() {
        Constraints constraint = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(GetDumaWorkManager.class, 20, TimeUnit.SECONDS).setConstraints(constraint).build();

        WorkManager.getInstance(requireContext())

                .enqueueUniquePeriodicWork("get_ride_status", ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }

    public static HomeFragment getInstance() {
        return instance;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                getcurrentlocation();
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    // setDropLocation
    private void setDropLocation(Intent data) {
        drop = Autocomplete.getPlaceFromIntent(data);
        drop_location.setText(drop.getAddress());
//        drop_location.setText(drop.getName());
        destlat = drop.getLatLng().latitude;
        destlng = drop.getLatLng().longitude;

        Log.d(TAG, "destlat : " + destlat + "\n destlng : " + destlng);

        data_lists.clear();
        data_header_lists.clear();
        adapter.notifyDataSetChanged();

        clear.setVisibility(View.VISIBLE);
        clear.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.white));
        footer.setVisibility(View.VISIBLE);
        mainFooter.setVisibility(View.VISIBLE);
        footer.startAnimation(animFadeIn);
        linear_rideNow.setVisibility(View.VISIBLE);
        linear_cnfrmBooking.setVisibility(View.GONE);
        completeRideLayout.setVisibility(View.GONE);
        startRideLayout.setVisibility(View.GONE);
        ride_now.setVisibility(View.GONE);
        cnfrmBooking.setEnabled(true);

        cnfrmBooking.setText("Confirm Booking");
        cnfrmBooking.setVisibility(View.GONE);
        if (marker2 != null) {
            marker2.remove();
        }
        if (polyline != null) {
            polyline.remove();
        }
        BitmapDescriptor defaultIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
        BitmapDescriptor customIcon1 = BitmapDescriptorFactory.fromResource(R.drawable.taxi_old);
        BitmapDescriptor customIcon = bitmapDescriptorFromVector(getActivity(), R.drawable.dest_icon);

        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(new LatLng(destlat, destlng));
        markerOptions.icon(customIcon);
        marker2 = myMap.addMarker(markerOptions);

        /********************************setting origin and dest point in one frame********************************/
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        //the include method will calculate the min and max bound.
        if (marker != null) {
            builder.include(marker.getPosition());
//                builder.include(marker1.getPosition());
            builder.include(marker2.getPosition());
        }
        LatLngBounds bounds = builder.build();

               /* CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 300);
                myMap.animateCamera(cu);*/

        if (firstTime) {
            firstTime = false;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 200);
            myMap.moveCamera(cameraUpdate);
            myMap.animateCamera(cameraUpdate, 1000, null);
        }


        /********************************setting origin and dest point in one frame********************************/


        origin = new LatLng(currentLatitude, currentLongitude);
        dest = new LatLng(destlat, destlng);

        /********************************drawing line between two points********************************/
        // Getting URL to the Google Directions API
                /*String url = getDirectionsUrl(origin, dest);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);*/

        setCurrentToDestinationPolyLine();
        // setTrackingPolyLine();
        /********************************drawing line between two points********************************/
                /*if (Build.VERSION.SDK_INT < 22)
                    setStatusBarTranslucent(false);
                else
                    setStatusBarTranslucent(true);
                configureCameraIdle();*/
        if (!CheckConnection.haveNetworkConnection(getActivity())) {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        } else {
            Log.e("VEHICLE", currentLatitude + " " + currentLongitude + " " + destlat + " " + destlng);

//                    try {
//                        GeoPoint pickupPoint = getLocationFromAddress(pickup_location.getText().toString());
//                        currentLongitude = pickupPoint.getLongitude();
//                        currentLatitude = pickupPoint.getLatitude();
//                    }
//                    catch (Exception ex)
//                    {
//
//                    }
            data_lists.clear();
            data_header_lists.clear();
            vehicle_lists_details(currentLatitude, currentLongitude, destlat, destlng);
        }
    }

    // Set Pickup Location
    private void setPickupLocation(Intent data) {
        current_location.setColorFilter(ContextCompat.getColor(requireActivity(), R.color.white));
        pickup = Autocomplete.getPlaceFromIntent(data);
        pickup_location.setText(pickup.getAddress());
//        pickup_location.setText(pickup.getName());

        //new change on 24-12-2021
        currentLatitude = pickup.getLatLng().latitude;
        currentLongitude = pickup.getLatLng().longitude;

        sourceLat = pickup.getLatLng().latitude;
        sourceLng = pickup.getLatLng().longitude;
        data_lists.clear();
        data_header_lists.clear();

        //new change 24-12-2021
        BitmapDescriptor customIcon2 = bitmapDescriptorFromVector(getActivity(), R.drawable.origin_icon);//for svg
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(new LatLng(currentLatitude, currentLongitude));
        markerOptions1.icon(customIcon2);
        marker = myMap.addMarker(markerOptions1);
        marker.setVisible(true);

        myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 8));
        // Zoom in the Google Map
        myMap.animateCamera(CameraUpdateFactory.zoomTo(8));
    }

    //downloding
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(6);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);
            }

            // Drawing polyline in the Google Map for the i-th route
            myMap.addPolyline(lineOptions);
        }
    }

    //getting directions
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&units=metric&mode=driving&key=" + R.string.google_android_map_api_key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelTimer();
        stopPlaying();

        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mMessageReceiver);
        if (ride_id != 0 && status.equalsIgnoreCase("pending")) {
            deleteRequest(String.valueOf(ride_id));
        }
        if (mMapView != null) {
            mMapView.onDestroy();
        }

        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
                mGoogleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("seconds", seconds);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        buildGoogleApiClient();
        if (mMapView != null) {

            mMapView.onStart();
            if (currentLatitude != null && !currentLatitude.equals(0.0) && currentLongitude != null && !currentLongitude.equals(0.0)) {
                NearBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
            }
        }
    }

    public void reinitializeData() {
        try {
            setCurrentLocation();
            buildGoogleApiClient();
            if (mMapView != null) {

                mMapView.onStart();
                if (currentLatitude != null && !currentLatitude.equals(0.0) && currentLongitude != null && !currentLongitude.equals(0.0)) {
                    NearBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("CallingOnResume", "On resume is called.");
        //setDisablePickUpAndDropLoc(true);
        ((HomeActivity) requireActivity()).setProfile();
        if (paymentStatus.equalsIgnoreCase("COMPLETED")) {
            //ride_id = 0;
        }
        if (!tipSkipClicked) getLastRide1();
//        if (checkStatus) {
//            if (ride_id != 0) {
//                getrideStatus_details();
//            }
//        }
        if (mMapView != null) {
            mMapView.onResume();
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient.connect();
        }

        //for timer
        seconds = (int) Global.getTimerSecondsPassed(getActivity());
        isRunning = seconds != 0 && Global.getWasTimerRunning(getActivity());

        if (!canRun) {
            timerLayout.setVisibility(View.GONE);
        }

        if (isRunning && !canRun) {
            timeHandler.removeCallbacks(timeRunnable);
            timeHandler.postDelayed(timeRunnable, 1000);
        }

        canRun = true;

        long minutes = TimeUnit.SECONDS.toMinutes(seconds);
        String mins = String.valueOf(minutes).length() == 2 ? minutes + "" : "0" + minutes;
        timerTime = mins + ":" + (String.valueOf(seconds - TimeUnit.MINUTES.toSeconds(minutes)).length() == 2 ? (seconds - TimeUnit.MINUTES.toSeconds(minutes)) : "0" + (seconds - TimeUnit.MINUTES.toSeconds(minutes)));

        if (ride_timer == null) {
            ride_timer = rootView.findViewById(R.id.ride_timer);
        }
        ride_timer.setText(timerTime);
        Global.setIsRunningInBackground(getActivity(), false);
        getActivity().stopService(serviceIntent);
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
        if (mMapView != null) {
            mMapView.onPause();
        }

        // for timer
//        startTimerService();
//        timeHandler.removeCallbacks(timeRunnable);
//        Global.setIsRunningInBackground(getActivity(), true);
//        canRun = false;
    }

    private void startTimerService() {
        try {
            if (isRunning) {
                Global.setTimerSecondsPassed(getActivity(), seconds);
//                Intent serviceIntent = new Intent(getActivity(), TimerService.class);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    getActivity().startForegroundService(serviceIntent);
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cancelTimer();
    }


    //multipal marker
    public void multipleMarker(List<NearbyData> list) {
        if (list != null) {
            for (NearbyData location : list) {
                Double latitude = null;
                Double longitude = null;
                try {
                    latitude = Double.valueOf(location.getLatitude());
                    longitude = Double.valueOf(location.getLongitude());

                    int height = 150;
                    int width = 220;
                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.car_ride);
                    Bitmap b = bitmapdraw.getBitmap();
                    Bitmap carMarker = Bitmap.createScaledBitmap(b, width, height, false);

                    BitmapDescriptor defaultIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                    BitmapDescriptor customIcon = BitmapDescriptorFactory.fromResource(R.drawable.taxi_old);//for png/jpeg/jpg
                    BitmapDescriptor customIcon1 = bitmapDescriptorFromVector(getActivity(), R.drawable.taxi_new);//for svg
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(new LatLng(latitude, longitude));
                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(carMarker)).title(location.getName());
                    myMap.addMarker(markerOptions).setTag(location);
                    //   marker1 = myMap.addMarker(markerOptions);
                    // marker1.setTag(location);
                } catch (NumberFormatException e) {

                }

                CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 14);
                myMap.animateCamera(camera);
            }
        }
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    //binding view
    public void bindView(Bundle savedInstanceState) {
        progressBar = rootView.findViewById(R.id.progressBar);
        MapsInitializer.initialize(this.getActivity());
        current_location = rootView.findViewById(R.id.current_location);
        clear = rootView.findViewById(R.id.clear);
        ratingBar = rootView.findViewById(R.id.rating);
        acceptRideProgressbar = rootView.findViewById(R.id.progress_accept_ride);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingvalue = String.valueOf((Float) ratingBar.getRating());
            }
        });

        //tip layout views
        tip_layout = rootView.findViewById(R.id.tip_layout);
        txt_tip_for = rootView.findViewById(R.id.txt_tip_for);
        tip_pay_btn = rootView.findViewById(R.id.tip_pay_btn);
        tip_skip_btn = rootView.findViewById(R.id.tip_skip_btn);
        input_tip_amount = rootView.findViewById(R.id.input_tip_amount);


        paymentNowLayout = rootView.findViewById(R.id.layout_payment_now_footer);
        txt_ride_amount = rootView.findViewById(R.id.tv_amount_pay_now);
        txt_cancellation_charge = rootView.findViewById(R.id.tv_cancellation_amount_pay_now);
        txt_total = rootView.findViewById(R.id.tv_total_amount_pay_now);
        txt_cancel_title = rootView.findViewById(R.id.tv_cancellation_charge_title);
        txt_total_title = rootView.findViewById(R.id.tv_total_title);
        line = rootView.findViewById(R.id.view_line);
        btnPayNow = rootView.findViewById(R.id.btnPayNow_pay_now);
        txt_info = rootView.findViewById(R.id.txt_info);
        ride_now = rootView.findViewById(R.id.ride_now);
        cnfrmBooking = rootView.findViewById(R.id.cnfrmBooking);
        cancelBooking = rootView.findViewById(R.id.cancelBooking);
        distance = rootView.findViewById(R.id.distance);
        speed = rootView.findViewById(R.id.speed);
        time = rootView.findViewById(R.id.time);
        txt_cost = rootView.findViewById(R.id.txt_cost);
        mMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        linear_rideNow = rootView.findViewById(R.id.linear_rideNow);
        linear_cnfrmBooking = rootView.findViewById(R.id.linear_cnfrmBooking);
        min_max = rootView.findViewById(R.id.min_max);
        minimize = rootView.findViewById(R.id.minimize);
        maximize = rootView.findViewById(R.id.maximize);
        cross = rootView.findViewById(R.id.cross);
        min_max.setVisibility(View.GONE);
        driver_details_layout = rootView.findViewById(R.id.driver_details_layout);
        header = rootView.findViewById(R.id.header);
        mainHeaderLayout = rootView.findViewById(R.id.mainHeaderLayout);
        mainFooter = rootView.findViewById(R.id.mainFooter);
        footer = rootView.findViewById(R.id.footer);
        //surge amount
        txt_surge_percentage = rootView.findViewById(R.id.txt_surge_percentage);
        //surge amount
        pickup_location = rootView.findViewById(R.id.pickup_location);
        drop_location = rootView.findViewById(R.id.drop_location);
        linear_pickup = rootView.findViewById(R.id.linear_pickup);
        relative_drop = rootView.findViewById(R.id.relative_drop);
        txtDriverRating = rootView.findViewById(R.id.txtDriverRating);
        center_location = rootView.findViewById(R.id.center_location);
        vehicle_recyclerView = rootView.findViewById(R.id.vehicle_recyclerView);
        vehicleNo = rootView.findViewById(R.id.vehicleNo);
        driver_pic = rootView.findViewById(R.id.taxi_driver_pic);
        driver_pics = rootView.findViewById(R.id.driver_pic);
        car_pic = rootView.findViewById(R.id.car_pic);
        driverName = rootView.findViewById(R.id.driverName);
        linear_afterridecnfrm = rootView.findViewById(R.id.linear_afterridecnfrm);
        completeRideLayout = rootView.findViewById(R.id.completeRideLayout);
        startRideLayout = rootView.findViewById(R.id.startRideLayout);
        txtDriverName = rootView.findViewById(R.id.txtDriverName);
        txtstartDriverName = rootView.findViewById(R.id.txtstartDriverName);
        txtAmount = rootView.findViewById(R.id.txtAmount);
        btnStartRecording = rootView.findViewById(R.id.btnStartRecording);
        btnStopRecording = rootView.findViewById(R.id.btnStopRecording);
        doneBtn = rootView.findViewById(R.id.doneBtn);
        skipBtn = rootView.findViewById(R.id.skipBtn);
        callDriverLayout = rootView.findViewById(R.id.callDriverLayout);
//        cancelRideLayout = rootView.findViewById(R.id.cancelRideLayout);
        input_comment = rootView.findViewById(R.id.input_comment);
        timerLayout = rootView.findViewById(R.id.timerLayout);
        ride_timer = rootView.findViewById(R.id.ride_timer);
        /*mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);*/
        mMapView.getMapAsync(this);
        mMapView.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), getString(R.string.google_android_map_api_key));
        pass = new Pass();
        // load animations
        animFadeIn = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_in);
        animFadeOut = AnimationUtils.loadAnimation(getActivity(), R.anim.fade_out);
        animSlideUp = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
        animeZoomIn = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_in);
        animeZoomOut = AnimationUtils.loadAnimation(getActivity(), R.anim.zoom_out);
        animFadeIn.setAnimationListener(this);
        animFadeOut.setAnimationListener(this);
        animSlideUp.setAnimationListener(this);
        animeZoomIn.setAnimationListener(this);
        animeZoomOut.setAnimationListener(this);
        applyfonts();
        placesClient = Places.createClient(getActivity());

        header.setVisibility(View.VISIBLE);
        header.startAnimation(animFadeIn);
        driver_details_layout.setVisibility(View.GONE);

        if (!drop_location.getText().toString().equals("")) {
            clear.setVisibility(View.VISIBLE);
        }

        //to minimize and maximize accept ride layout
        min_max.setOnClickListener(e -> {
            if (driver_details_layout.getVisibility() == View.VISIBLE) {
                min_max.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.maximize_icon, null));
                driver_details_layout.setVisibility(View.GONE);
            } else if (driver_details_layout.getVisibility() == View.GONE) {
                min_max.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.minimize_icon, null));
                driver_details_layout.setVisibility(View.VISIBLE);
            }
        });

        minimize.setOnClickListener(e -> {
            minimize.setVisibility(View.GONE);
            maximize.setVisibility(View.VISIBLE);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 400);
            vehicle_recyclerView.setLayoutParams(lp);

        });

        maximize.setOnClickListener(e -> {
            maximize.setVisibility(View.GONE);
            minimize.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1200);
            vehicle_recyclerView.setLayoutParams(lp);
        });

        cross.setOnClickListener(e -> {
            data_lists.clear();
            data_header_lists.clear();
            linear_rideNow.setVisibility(View.GONE);
            drop_location.setText("");
        });

        tip_pay_btn.setOnClickListener(e -> {

            if (input_tip_amount.getText().toString().isEmpty()) {
                Toast.makeText(context, "Please enter valid tip amount.", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    if (Double.parseDouble(input_tip_amount.getText().toString()) == 0) {
                        Toast.makeText(context, "Please enter valid tip amount.", Toast.LENGTH_SHORT).show();
                    } else {

                        Double.parseDouble(input_tip_amount.getText().toString());
                        PendingPojo pendingRequestPojo = new PendingPojo();
                        pendingRequestPojo.setAmount(input_tip_amount.getText().toString());
                        pendingRequestPojo.setRideId(String.valueOf(ride_id));
                        pendingRequestPojo.setPaymentStatus("tip_amount");
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("data", pendingRequestPojo);
                        PaymentFragment paymentFragment = new PaymentFragment();
                        paymentFragment.setArguments(bundle);
                        footer.setVisibility(View.GONE);
                        paymentNowLayout.setVisibility(ConstraintLayout.GONE);
                        if (context != null)
                            ((HomeActivity) context).changeFragment(paymentFragment, "Payment Method");

                        tip_layout.setVisibility(View.GONE);
                        footer.setVisibility(View.GONE);
                        header.setVisibility(View.VISIBLE);
                        mainHeaderLayout.setVisibility(View.VISIBLE);
                        timerTask.cancel();
                        ride_id = 0;
                        feedbackShown = true;
                        SessionManager.setActiveRideId(null);
                        replaceFragment();
                    }

                } catch (NumberFormatException ex) {
                    Toast.makeText(context, "Please enter valid tip amount.", Toast.LENGTH_SHORT).show();
                }
            }

        });

        tip_skip_btn.setOnClickListener(e -> {
            header.setVisibility(View.VISIBLE);
            mainHeaderLayout.setVisibility(View.VISIBLE);
            timerTask.cancel();
            ride_id = 0;
            feedbackShown = true;
            tipSkipClicked = true;
            SessionManager.setActiveRideId(null);
            tip_layout.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);

            reinitializeData();
//            replaceFragment();
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data_lists.clear();
                data_header_lists.clear();
                Log.e("CANCEL", "clear was called.");
                drop_location.setText("");
                if (footer.getVisibility() == View.VISIBLE) {
                    footer.startAnimation(animFadeOut);
                    footer.setVisibility(View.GONE);
                    clear.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    if (marker2 != null) {
                        marker2.remove();
                    }
                    if (polyline != null) {
                        polyline.remove();
                    }
                }
            }
        });

        current_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    setCurrentLocation();
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    askCompactPermissions(permissionAsk, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            setCurrentLocation();
//                            if (pickup_location.getText().toString().trim().equals("")) {
//                                setCurrentLocation();
//                            } else {
//                                pickup_location.setText("");
//                                current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
//                            }
                        }

                        @Override
                        public void permissionDenied() {

                        }

                        @Override
                        public void permissionForeverDenied() {
                            Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
                            openSettingsApp(getActivity());
                        }
                    });
                } else {
                    if (!GPSEnable()) {
                        tunonGps();
                    } else {
                        setCurrentLocation();
//                        if (pickup_location.getText().toString().trim().equals("")) {
//                            setCurrentLocation();
//                        } else {
//                            pickup_location.setText("");
//                            current_location.setColorFilter(ContextCompat.getColor(getActivity(), R.color.black));
//                        }
                    }
                }
            }
        });

        center_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CameraUpdate camera = CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15);
                //  myMap.animateCamera(camera);

                if (currentLatitude != null && currentLongitude != null) {
                    myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
                    // Zoom in the Google Map
                    myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                } else {
                    askCompactPermissions(permissionAsk, new PermissionResult() {
                        @Override
                        public void permissionGranted() {
                            if (!GPSEnable()) {
                                tunonGps();
                            } else {
                                getcurrentlocation();
                            }
                        }

                        @Override
                        public void permissionDenied() {
                        }

                        @Override
                        public void permissionForeverDenied() {
                            Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
                            openSettingsApp(getActivity());
                        }
                    });
                }
            }
        });

        ride_now.setOnClickListener(v -> {
            feedbackShown = false;
            securityPopupShown = false;
            tipSkipClicked = false;
            if (CheckConnection.haveNetworkConnection(requireContext())) {
                if (pickup_location.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please enter valid pickup location", Toast.LENGTH_SHORT).show();
                } else if (drop_location.getText().toString().equalsIgnoreCase("") || drop_location.getText().toString().equalsIgnoreCase("enter drop location")) {
                    Toast.makeText(context, "Please enter valid drop location", Toast.LENGTH_SHORT).show();
                } else {
                    //shifted this line inside getCardLists
//                    confirm_ride_details(currentLatitude, currentLongitude, destlat, destlng);
                    getCardLists();
                }
                Log.d("pradnya ", "tracker1");
//                    final Runnable r = new Runnable() {
//                        public void run() {
//                            //Do thing after 20 sec
//                            replaceFragment();
//                            if (ride_id != 0 && (status.equalsIgnoreCase("pending")||
//                                    status.equalsIgnoreCase("cancelled")
//                                    ||status.equalsIgnoreCase("failed"))) {
//                                setDisablePickUpAndDropLoc(true);
//                                getRideStatus();
//
//                            }
//                        }
//                    };handler.postDelayed(r, 200000);
                startWorkManager();
//                cancelRideIfNoDriver();
            } else {
                Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            }
        });

        callDriverLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + "+67 " + contactNo));
                startActivity(intent);
//                callDriver(contactNo);
//                if (isPermissionGranted()) {
//                     call_action();
//                }
            }
        });


//        cancelRideLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.e("CANCEL", "cancel ride layout was called.");
//                setDisablePickUpAndDropLoc(true);
//                if (failedRideStatus) {
//                    myMap.clear();
//                    if (marker != null) {
//                        marker.remove();
//                    }
//                    if (marker2 != null) {
//                        marker2.remove();
//                    }
//                    if (mMarkers != null) {
//                        mMarkers.clear();
//                    }
//                    if (mCurrLocationMarker != null) {
//                        mCurrLocationMarker.remove();
//                    }
//                    if (polyline != null) {
//                        polyline.remove();
//                    }
//
//                    if (driverMarker != null) {
//                        driverMarker.remove();
//                    }
//                    drop_location.setText("");
//                    SessionManager.setActiveRideId(null);
//                    mainHeaderLayout.setVisibility(View.VISIBLE);
//                    header.setVisibility(View.VISIBLE);
//                    footer.setVisibility(View.GONE);
//                    mainFooter.setVisibility(View.GONE);
//                    linear_cnfrmBooking.setVisibility(View.GONE);
//                    linear_afterridecnfrm.setVisibility(View.GONE);
//                    completeRideLayout.setVisibility(View.GONE);
//                    startRideLayout.setVisibility(View.GONE);
//                    cnfrmBooking.setVisibility(View.GONE);
//                    driver_details_layout.setVisibility(View.GONE);
//                    // timerTask.cancel();
//                    check = false;
//                    checkTwo = false;
//                    checkStatus = false;
//                    ride_id = 0;
//                } else {
//
//                    AlertDialogCreate(getString(R.string.ride_request_cancellation), getString(R.string.want_to_cancel), "CANCELLED");
//                }
//
//            }
//        });

        /*cnfrmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getrideStatus_details();
                acceptRequest(String.valueOf(ride_id));
                Log.d("pradnya ", "tracker2");
            }
        });*/


        cancelBooking.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.e("CANCEL", "cancel booking was called.");
                isRideCancelled = true;
                setDisablePickUpAndDropLoc(true);
                if (failedRideStatus) {
                    myMap.clear();
                    if (marker != null) {
                        marker.remove();
                    }
                    if (marker2 != null) {
                        marker2.remove();
                    }
                    if (mMarkers != null) {
                        mMarkers.clear();
                    }
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    if (polyline != null) {
                        polyline.remove();
                    }
                    if (driverMarker != null) {
                        driverMarker.remove();
                    }
                    drop_location.setText("");
                    SessionManager.setActiveRideId(null);
                    mainHeaderLayout.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.GONE);
                    mainFooter.setVisibility(View.GONE);
                    linear_cnfrmBooking.setVisibility(View.GONE);
                    linear_afterridecnfrm.setVisibility(View.GONE);
                    completeRideLayout.setVisibility(View.GONE);
                    startRideLayout.setVisibility(View.GONE);
                    cnfrmBooking.setVisibility(View.GONE);
                    driver_details_layout.setVisibility(View.GONE);
                    // timerTask.cancel();
                    check = false;
                    checkTwo = false;
                    checkStatus = false;
                    ride_id = 0;

                } else {
                    if (status.equalsIgnoreCase("ACCEPTED"))
                        AlertDialogCreate(getString(R.string.ride_request_cancellation), "If you cancel the confirmed ride after 5 min than $" + cancelRideCharge + " cancellation charge will be added in your next ride. Do you want to cancel the ride?", "CANCELLED");
                    else {
                        AlertDialogCreate(getString(R.string.ride_request_cancellation), getString(R.string.want_to_cancel), "CANCELLED");
                    }

                }
            }
        });

        skipBtn.setOnClickListener(e -> {
            drop_location.setText("");
            completeRideLayout.setVisibility(View.GONE);
            mainFooter.setVisibility(View.GONE);
            footer.setVisibility(View.GONE);
            header.setVisibility(View.VISIBLE);
            mainHeaderLayout.setVisibility(View.VISIBLE);
            timerTask.cancel();
            ride_id = 0;
            SessionManager.setActiveRideId(null);
            feedbackShown = true;
            txt_tip_for.setText("Add tip for " + driverNames);
            tip_layout.setVisibility(View.GONE);
            replaceFragment();
            setDisablePickUpAndDropLoc(true);
            setCurrentLocation();
        });


        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*mainHeaderLayout.setVisibility(View.VISIBLE);
                header.setVisibility(View.VISIBLE);
                footer.setVisibility(View.GONE);
                //mainFooter.setVisibility(View.GONE);
                linear_cnfrmBooking.setVisibility(View.GONE);

                linear_afterridecnfrm.setVisibility(View.GONE);
                completeRideLayout.setVisibility(View.GONE);
                cnfrmBooking.setVisibility(View.GONE);*/

                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    if (ratingvalue != null && !ratingvalue.isEmpty() && !ratingvalue.equalsIgnoreCase("0.0")) {
                        submitFeedBack();
                    } else {
                        Toast.makeText(getActivity(), "Please give rating", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }

            }
        });


        layoutManager = new LinearLayoutManager(getActivity());
        vehicle_recyclerView.setLayoutManager(layoutManager);
        vehicle_recyclerView.setItemAnimator(new DefaultItemAnimator());
        vehicle_recyclerView.setHasFixedSize(true);
//        adapter = new VehicleInfoAdapter(getActivity(), data_lists, ride_now, footer, linear_rideNow, linear_cnfrmBooking, new VehicleTypeInterface() {
//            @Override
//            public void onItemClicked(int id, double amount) {
//                vehicleId = id;
//                rate = amount;
//                //vehicle_lists_details(currentLatitude, currentLongitude, destlat, destlng);
//                Log.e("Vehicle_id", String.valueOf(id));
//                Log.d("Saurabh Check ", "hellllooooooooooooo");
//            }
//        });
        adapter = new VehicleCategoryAdapter(getActivity(), data_header_lists, ride_now, footer, linear_rideNow, linear_cnfrmBooking, new VehicleTypeInterface() {
            @Override
            public void onItemClicked(int id, double amount, double hold_amount) {

                vehicleId = id;
                rate = amount;
                holdAmount = hold_amount;
                //vehicle_lists_details(currentLatitude, currentLongitude, destlat, destlng);
                Log.e("Vehicle_id", String.valueOf(id));
            }
        });
        vehicle_recyclerView.setAdapter(adapter);
    }

    //starting work manager
    private void startWorkManager() {

        WorkRequest myWorkRequest = new OneTimeWorkRequest.Builder(GetDumaWorkManager.class).setInitialDelay(2, TimeUnit.MINUTES).addTag("CANCEL_RIDE").setInputData(new Data.Builder().putString("ride_id", String.valueOf(ride_id)).build()).build();

        WorkManager.getInstance(requireContext()).enqueue(myWorkRequest);

    }

    //ending ride
    public void endRide() {
        status = "COMPLETED";
        myMap.clear();
        if (marker != null) {
            marker.remove();
        }

        if (marker2 != null) {
            marker2.remove();
        }
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if (polyline != null) {
            polyline.remove();
        }
        linear_cnfrmBooking.setVisibility(View.GONE);
        linear_afterridecnfrm.setVisibility(View.GONE);

        footer.setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
        mainHeaderLayout.setVisibility(View.VISIBLE);
        timerTask.cancel();
        ride_id = 0;
        feedbackShown = true;
        SessionManager.setActiveRideId(null);
        replaceFragment();
//        reinitializeData();
    }

    //calering fargment
    public void clearFragment() {
        if (failedRideStatus) {
            myMap.clear();
            if (marker != null) {
                marker.remove();
            }
            if (marker2 != null) {
                marker2.remove();
            }
            if (mMarkers != null) {
                mMarkers.clear();
            }
            if (mCurrLocationMarker != null) {
                mCurrLocationMarker.remove();
            }
            if (polyline != null) {
                polyline.remove();
            }
            drop_location.setText("");
            SessionManager.setActiveRideId(null);
            mainHeaderLayout.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            footer.setVisibility(View.GONE);
            mainFooter.setVisibility(View.GONE);
            linear_cnfrmBooking.setVisibility(View.GONE);
            linear_afterridecnfrm.setVisibility(View.GONE);
            completeRideLayout.setVisibility(View.GONE);
            startRideLayout.setVisibility(View.GONE);
            cnfrmBooking.setVisibility(View.GONE);
            driver_details_layout.setVisibility(View.GONE);
            // timerTask.cancel();
            check = false;
            checkTwo = false;
            checkStatus = false;
            ride_id = 0;

            replaceFragment();
        }

    }

    //vechical list
    public void vehicle_lists_details(Double originLat, Double originLong, Double destLat, Double destLong) {
        RequestParams params = new RequestParams();
        //  params.put("pickup_address", pickup_location.getText().toString());
        //  params.put("drop_address", drop_location.getText().toString());
        params.put("pickup_lat", originLat);
        params.put("pickup_long", originLong);
        params.put("drop_lat", destLat);
        params.put("drop_long", destLong);
        Server.setHeader(getKEY());
        Server.post("vehicle-category", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    //new change on 27-12-2021
                    checkStatus = true;
                    if (response.has("status") && response.getBoolean("status")) {
                        //show surge amount
                        try {
                            if (response.getInt("flag") == 1) {
                                txt_surge_percentage.setVisibility(View.VISIBLE);
                                txt_surge_percentage.setText(String.format("Due to high demand on this location, fare is increased by %s%% of original", response.getString("surge_percentage")));
                            } else {
                                txt_surge_percentage.setVisibility(View.GONE);
                            }
                        } catch (Exception ex) {

                        }
                        JSONArray jsonArray = response.getJSONArray("data");
                        distanceVal = response.getString("distance");
                        Log.d("vehicle_reponse", jsonArray.toString() + ", " + originLat + ", " + originLong + ", " + destLat + ", " + destLong);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            JSONArray vehiclesArray = jsonObject.getJSONArray("vehicles");
                            data_lists = new ArrayList<>();
                            for (int j = 0; j < vehiclesArray.length(); j++) {
                                VehicleInfo lists = new VehicleInfo();
                                JSONObject vehiclesObject = vehiclesArray.getJSONObject(j);
                                lists.setName(vehiclesObject.getString("vehicle_name"));
                                lists.setRate(vehiclesObject.getString("per_mile_rate"));
                                lists.setTotalAmount(vehiclesObject.getString("total_amount"));
                                lists.setSpecs(vehiclesObject.getString("short_description"));
                                lists.setDistance(vehiclesObject.getString("per_mile_rate"));
//                                lists.setCar_pic(vehiclesObject.getString("car_pic"));
                                lists.setCar_pic(vehiclesObject.getString("vehicle_image"));
                                lists.setVehicle_id(vehiclesObject.getString("vehicle_id"));
                                lists.setCategory_name(vehiclesObject.getString("category_name"));
                                lists.setHold_amount(vehiclesObject.getString("hold_amount"));
                                lists.setCancellation_charge(vehiclesObject.getString("cancellation_charge"));
//                                Log.d("car_pic", vehiclesObject.getString("car_pic"));
                                data_lists.add(lists);
                            }
                            VehicleCategory vehicleCategory = new VehicleCategory(jsonObject.getString("category_name"), data_lists);
                            data_header_lists.add(vehicleCategory);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        data_lists.clear();
                        data_header_lists.clear();
                        adapter.notifyDataSetChanged();

                        drop_location.setText("");
                        if (footer.getVisibility() == View.VISIBLE) {
                            footer.startAnimation(animFadeOut);
                            footer.setVisibility(View.GONE);
                            clear.setVisibility(View.GONE);
                            if (marker2 != null) {
                                marker2.remove();
                            }
                            if (polyline != null) {
                                polyline.remove();
                            }
                        }

                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }


                }//try ends here

                catch (JSONException e) {

                    e.printStackTrace();
                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                data_lists.clear();
                data_header_lists.clear();
                adapter.notifyDataSetChanged();

                drop_location.setText("");
                if (footer.getVisibility() == View.VISIBLE) {
                    footer.startAnimation(animFadeOut);
                    footer.setVisibility(View.GONE);
                    clear.setVisibility(View.GONE);
                    if (marker2 != null) {
                        marker2.remove();
                    }
                    if (polyline != null) {
                        polyline.remove();
                    }
                }
                Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_LONG).show();
                //   Log.d("vehicle_reponse", errorResponse.toString());
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(getActivity(), "Something went wrong...", Toast.LENGTH_LONG).show();
                data_lists.clear();
                data_header_lists.clear();
                adapter.notifyDataSetChanged();

                drop_location.setText("");
                if (footer.getVisibility() == View.VISIBLE) {
                    footer.startAnimation(animFadeOut);
                    footer.setVisibility(View.GONE);
                    clear.setVisibility(View.GONE);
                    if (marker2 != null) {
                        marker2.remove();
                    }
                    if (polyline != null) {
                        polyline.remove();
                    }
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    //calling actions
    public void call_action() {
        String phnum = contactNo;
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phnum));
        startActivity(callIntent);
    }

    //clearing footer
    private void clearFooterPopup() {

        footer.setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
        mainHeaderLayout.setVisibility(View.VISIBLE);
        timerTask.cancel();
        ride_id = 0;
        SessionManager.setActiveRideId(null);
    }

    //submiting feedback
    private void submitFeedBack() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Submitting feedback", false, false);
        comment = input_comment.getText().toString();
        loading.show();
        Log.d("rating", String.valueOf(ratingvalue));
        Log.d("comment", comment);

        Map<String, String> feedBack = new HashMap<>();
        feedBack.put("ride_id", String.valueOf(ride_id));
        feedBack.put("rating", String.valueOf(ratingvalue));
        feedBack.put("comment", comment);
        feedBack.put("driver_id", driver_id);

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<ChangePasswordResponse> call = apiService.giveFeedBack("Bearer " + getKEY(), feedBack);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    loading.cancel();
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    completeRideLayout.setVisibility(View.GONE);
                    footer.setVisibility(View.GONE);
                    mainFooter.setVisibility(View.GONE);
                    header.setVisibility(View.VISIBLE);
                    mainHeaderLayout.setVisibility(View.VISIBLE);
                    timerTask.cancel();
                    ride_id = 0;
                    SessionManager.setActiveRideId(null);
                    txt_tip_for.setText("Add tip for " + driverNames);
                    tip_layout.setVisibility(View.GONE);
                    feedbackShown = true;
                    drop_location.setText("");
                    replaceFragment();
                    setDisablePickUpAndDropLoc(true);
                    setCurrentLocation();
                } else {
                    loading.cancel();
                    timerTask.cancel();
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                loading.cancel();
                timerTask.cancel();
                Log.d("Failed", "RetrofitFailed");
            }


        });
    }

    //replacing fragment
    public void replaceFragment() {
        try {
            Fragment frg = null;
            frg = requireActivity().getSupportFragmentManager().findFragmentByTag(getString(R.string.home));
            final FragmentTransaction ft = requireActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null);
            ft.detach(frg);
            ft.attach(frg);
            ft.commit();
            reinitializeData();
        } catch (Exception ex) {

        }

//        requireActivity().getSupportFragmentManager().beginTransaction()
//                .detach(HomeFragment.this).attach(HomeFragment.this).commit();
//        setCurrentLocation();
    }

    //permissions
    public boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG", "Permission is granted");
                return true;
            } else {
                Log.v("TAG", "Permission is revoked");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG", "Permission is granted");
            return true;
        }
    }

    //seting pickup locations
    public void setDisablePickUpAndDropLoc(boolean status) {
        clear.setEnabled(status);
        current_location.setEnabled(status);
        pickup_location.setEnabled(status);
        drop_location.setEnabled(status);
    }

    //confirm ride details
    public void confirm_ride_details(Double originLat, Double originLong, Double destLat, Double destLong) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Finding Driver      ", false, false);

        RequestParams params = new RequestParams();
        params.put("pickup_lat", originLat);
        params.put("pickup_long", originLong);
        params.put("distance", distanceVal);
        params.put("amount", rate);
        params.put("drop_lat", destLat);
        params.put("drop_long", destLong);
        params.put("pickup_adress", pickup_location.getText().toString());
        params.put("drop_address", drop_location.getText().toString());
        params.put("pikup_location", pickup_location.getText().toString());
        params.put("drop_locatoin", drop_location.getText().toString());
        params.put("user_id", SessionManager.getUserId());
        params.put("vehicle_type_id", vehicleId);
        params.put("txn_id", txnId);
        params.put("card_id", getDefaultCard());
        Server.setHeader(getKEY());
        Log.e("KEY0", getKEY());
        Server.post("postRideToDriver", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                loading.cancel();
                setDisablePickUpAndDropLoc(false);
                super.onSuccess(statusCode, headers, response);
                Log.d("accepted details : ", response.toString());
                try {
                    if (response.has("status") && response.getBoolean("status")) {
                        footer.setVisibility(View.VISIBLE);
                        linear_rideNow.setVisibility(View.GONE);
                        linear_afterridecnfrm.setVisibility(View.GONE);
                        linear_cnfrmBooking.setVisibility(View.VISIBLE);
                        completeRideLayout.setVisibility(View.GONE);
                        startRideLayout.setVisibility(View.GONE);
                        cnfrmBooking.setVisibility(View.GONE);
                        driver_details_layout.setVisibility(View.GONE);

                        //Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject = response.getJSONObject("ride_detail");
                        if (!jsonObject.getString("vehicle_no").equalsIgnoreCase("")) {
                            vehicleNumber = jsonObject.getString("vehicle_no");
                            vehicleNo.setText(vehicleNumber);
                        } else {
                            vehicleNo.setVisibility(View.GONE);
                        }
                        driver_id = jsonObject.getString("driver_id");
                        total_rating = jsonObject.getString("total_rating");
                        //total_driver_ride = Integer.parseInt(jsonObject.getString("total_driver_ride"));
                        txtDriverRating.setText(String.valueOf(total_rating));

                        if (jsonObject.getString("profile_pic") != null) {
                            profileUrl = jsonObject.getString("profile_pic");
                            if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase(" ")) {
                                Glide.with(getActivity()).load(R.drawable.user_default).into(driver_pic);
                            } else {
                                Glide.with(getActivity()).load(jsonObject.getString("profile_pic")).into(driver_pic);
                            }
                        }

                        if (jsonObject.getString("car_pic") != null) {
                            carPicUrl = jsonObject.getString("car_pic");
                            if (carPicUrl.trim().isEmpty() || carPicUrl.trim().equalsIgnoreCase(" ")) {
                                Glide.with(getActivity()).load(R.drawable.car_pic).into(car_pic);
                            } else {
                                Glide.with(getActivity()).load(jsonObject.getString("car_pic")).into(car_pic);
                            }
                        }

                        //For example spead is 10 meters per minute.
                        speedIs10MetersPerMinute = 30;
                        distanceInMeters = jsonObject.getString("total_arrival_distance");
                        estimatedDriveTimeInMinutes = jsonObject.getString("total_arrival_time");
                        // distanceInKm = Math.round(distanceInMeters);
                        // estimatedDriveTimeInMinutes = distanceInMeters / speedIs10MetersPerMinute;
                        // distanceInMiles = (float) (distanceInKm * 0.621);
                        // finalDriverRating = total_rating / total_driver_ride;
                        Log.d("estimatedDriveTimeInMinutes", String.valueOf(estimatedDriveTimeInMinutes));
                        Log.d("distanceInMiles", String.valueOf(distanceInMiles));

                        Log.d("estimatedDriveTimeInMinutes", estimatedDriveTimeInMinutes);


                        //time.setText(new DecimalFormat("##.##").format(estimatedDriveTimeInMinutes) + " Min");
                        // distance.setText(new DecimalFormat("##.##").format(distanceInMiles) + " Miles");
                        //changes here in new build 20/06/23
                        //distanceInMile = Double.parseDouble(distanceInMeters)* 0.00062137;
                        distance.setText(distanceInMiles + "miles");
                        time.setText(estimatedDriveTimeInMinutes);
                        //distance.setText(distanceInMiles + " Miles");
                        // distance.setText(round3(distanceInMiles, 1) + " Miles");
                        if (jsonObject.getString("driver_lastname") != null) {
                            driverName.setText(jsonObject.getString("driver_lastname"));
                        }
                        Log.d("mobile ", contactNo);
                        ride_id = jsonObject.getInt("ride_id");
                        SessionManager.setActiveRideId(String.valueOf(ride_id));
                        req_ride_id = jsonObject.getInt("ride_id");
                        driverNames = driverName.getText().toString();
                        driverEmail = jsonObject.getString("email");
                        Log.d("msg d", driverNames);
                        Log.d("msg e", driverEmail);
                        Log.d("msg r", String.valueOf(ride_id));

                        getrideStatus_details();
                        acceptRequest(String.valueOf(ride_id));

                    } else {
                        stopTimer();
                        setDisablePickUpAndDropLoc(true);
                        // Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();
//                        Toast.makeText(getActivity(),
//                                "No driver available near to your Pick up location. Please try again after sometime.",
//                                Toast.LENGTH_SHORT).show();

                        Toast.makeText(getActivity(), response.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    loading.cancel();
                    setDisablePickUpAndDropLoc(true);
                    e.printStackTrace();
                }
            }


            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("Failed: ", "" + statusCode);
                Log.d("Error : ", "" + throwable);
                Log.d("accepted details : ", responseString);
                setDisablePickUpAndDropLoc(true);
                //Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
                loading.cancel();
            }

           /* @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
                loading.cancel();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                loading.cancel();
            }*/

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public static float round3(float d, int decimalPlace) {
        return BigDecimal.valueOf(d).setScale(decimalPlace, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    //geting status of ride
    public void getrideStatus_details() {
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        Server.setHeader(getKEY());
        Server.post("get_ride_status", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //progressBar.setVisibility(View.VISIBLE);
            }

            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getBoolean("status")) {
                        Log.d("Response", "Ride Status " + response.toString());
                        //Toast.makeText(getActivity(), getString(R.string.ride_has_been_requested), Toast.LENGTH_LONG).show();
                        if (response.getJSONObject("data").getString("status").equalsIgnoreCase("NOT_CONFIRMED") || response.getJSONObject("data").getString("status").equalsIgnoreCase("pending")) {
                            Global.putAlertValue("VideoAlertStatus", true, getActivity());
                            if (response.getJSONObject("data").getString("status").equalsIgnoreCase("NOT_CONFIRMED")) {
                                setDisablePickUpAndDropLoc(false);
                                cnfrmBooking.setText("Waiting for driver to confirm.....");
                            } else {
                                setDisablePickUpAndDropLoc(pickup_location.isEnabled());
                                cnfrmBooking.setText("Waiting for driver to confirm.....");
                            }

                            //progressBar.setVisibility(View.VISIBLE);
                            acceptRideProgressbar.setVisibility(View.VISIBLE);
                            cancelRideIfNoDriver();
                            cnfrmBooking.setVisibility(View.VISIBLE);
                            footer.setVisibility(View.VISIBLE);
                            linear_cnfrmBooking.setVisibility(View.VISIBLE);
                            linear_afterridecnfrm.setVisibility(View.GONE);
                            min_max.setVisibility(View.GONE);
                            completeRideLayout.setVisibility(View.GONE);
                            paymentNowLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            amount = response.getJSONObject("data").getString("amount");
//                            (requireActivity()).runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    handler.postDelayed(new Runnable() {
//                                        public void run() {
//                                            Log.e("RIDE_STATUS","getrideStatus_details is calling");
//                                            getrideStatus_details();
//                                            handler.postDelayed(runnable, delay);
//                                        }
//                                    }, delay);
//
//                                }
//                            });
                            //distanceInMile = Double.parseDouble(distanceInMeters)* 0.00062137;
                            //distance.setText(distanceInMile + "miles");
                            distance.setText(distanceInMeters);
                            time.setText(estimatedDriveTimeInMinutes);
                            vehicleNo.setText(vehicleNumber);
                            driverName.setText(driverNames);
                            if (profileUrl != null) {
                                try {
                                    if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase(" ")) {
                                        Glide.with(getActivity()).load(R.drawable.user_default).into(driver_pic);
                                    } else {
                                        Glide.with(getActivity()).load(profileUrl).into(driver_pic);
                                    }
                                } catch (Exception ex) {

                                }
                            }

                            if (carPicUrl != null) {
                                try {
                                    if (carPicUrl.trim().isEmpty() || carPicUrl.trim().equalsIgnoreCase(" ")) {
                                        Glide.with(getActivity()).load(R.drawable.car_pic).into(car_pic);
                                    } else {
                                        Glide.with(getActivity()).load(carPicUrl).into(car_pic);
                                    }
                                } catch (Exception ex) {

                                }
                            }
                            // pickup_location.setText(pickup.getAddress());
                            txtDriverRating.setText(String.valueOf(total_rating));

//                            startLocationService();

                            getCancellationCount(String.valueOf(ride_id));
                        } else if (response.getJSONObject("data").getString("status").equalsIgnoreCase("accepted")) {

                            if (Global.getAlertValue("VideoAlertStatus", true, getActivity())) {
                                showVideoAlert();
                                homeVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        startRideTimer();
                                        Global.putAlertValue("VideoAlertStatus", false, getActivity());
                                        videoAlert.dismiss();
                                    }
                                });
                            }

                            status = "ACCEPTED";

                            progressBar.setVisibility(View.GONE);
                            acceptRideProgressbar.setVisibility(View.GONE);
                            handler.removeCallbacks(runnable);
                            footer.setVisibility(View.VISIBLE);
                            min_max.setVisibility(View.VISIBLE);
                            linear_cnfrmBooking.setVisibility(View.VISIBLE);
                            linear_afterridecnfrm.setVisibility(View.VISIBLE);
                            driver_details_layout.setVisibility(View.VISIBLE);
                            completeRideLayout.setVisibility(View.GONE);
                            paymentNowLayout.setVisibility(View.GONE);
                            cnfrmBooking.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            //distanceInMeters = response.getJSONObject("data").getString("total_arrival_distance");
                            //distanceInMeters =
                            paymentStatus = response.getJSONObject("data").getString("payment_status");
                            Log.e("Payment", response.getJSONObject("data").getString("payment_status"));
                            estimatedDriveTimeInMinutes = response.getJSONObject("data").getString("total_arrival_time");
                            //distanceInMile = Double.parseDouble(distanceInMeters)* 0.00062137;
                            //distance.setText(distanceInMile + "miles");
                            distance.setText(distanceInMeters);
                            time.setText(estimatedDriveTimeInMinutes);
                            vehicleNo.setText(vehicleNumber);
                            driverEmail = response.getJSONObject("data").getString("email");
                            amount = response.getJSONObject("data").getString("amount");
//                            driverNames = response.getJSONObject("data").getString("driver_name");
                            driverNames = response.getJSONObject("data").getString("driver_lastname");
                            total_rating = response.getJSONObject("data").getString("total_rating");
                            ride_id = response.getJSONObject("data").getInt("ride_id");
                            txtDriverRating.setText(String.valueOf(total_rating));
                            driverName.setText(driverNames);

                            contactNo = response.getJSONObject("data").getString("mobile");
                            ride_id = response.getJSONObject("data").getInt("ride_id");
                            SessionManager.setActiveRideId(String.valueOf(ride_id));
                            req_ride_id = response.getJSONObject("data").getInt("ride_id");
                            driverNames = driverName.getText().toString();
                            driverEmail = response.getJSONObject("data").getString("email");

                            driverLat = Double.parseDouble(response.getJSONObject("data").getString("driver_latitude"));
                            driverLong = Double.parseDouble(response.getJSONObject("data").getString("driver_longitude"));
                            sourceLat = Double.parseDouble(response.getJSONObject("data").getString("pickup_lat"));
                            sourceLng = Double.parseDouble(response.getJSONObject("data").getString("pickup_long"));

                            myMap.clear();

                            if (marker != null) {
                                marker.remove();
                            }
                            if (marker2 != null) {
                                marker2.remove();
                            }

                            check = true;
                            checkStatus = false;
                            loginToFirebase(driverEmail);
                            try {
//                                userCurrentLocation();
                                userCurrentLocationMarkerAccept();
                                // userDestinationLocationMarker();
//                                setDriverInitialMarker(driverLat, driverLong);
                                setDriverToUserTrackingPolyline(driverLat, driverLong, 0);
                                // setDestinationTrackingPolyLine();

                            } catch (Exception ex) {
                                Log.e("LOCATION_EXCEPTION", ex.toString());
                            }
                            if (response.getJSONObject("data").getString("profile_pic") != null) {
                                profileUrl = response.getJSONObject("data").getString("profile_pic");
                                if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase(" ")) {
                                    Glide.with(requireActivity()).load(R.drawable.user_default).into(driver_pic);
                                } else {
                                    Glide.with(requireActivity()).load(response.getJSONObject("data").getString("profile_pic")).into(driver_pic);
                                }
                            }

                            if (carPicUrl != null) {
                                try {
                                    if (carPicUrl.trim().isEmpty() || carPicUrl.trim().equalsIgnoreCase(" ")) {
                                        Glide.with(getActivity()).load(R.drawable.car_pic).into(car_pic);
                                    } else {
                                        Glide.with(getActivity()).load(carPicUrl).into(car_pic);
                                    }
                                } catch (Exception ex) {

                                }
                            }

                            cnfrmBooking.setVisibility(View.GONE);
                        } else if (response.getJSONObject("data").getString("status").equalsIgnoreCase("CANCELLED")) {
                            acceptRideProgressbar.setVisibility(View.GONE);
                            stopRideTimer();
                            stopTimer();
                            //new change on 19-01-2022
                            // deleteRequest(String.valueOf(ride_id));
                            ride_id = 0;
                            failedRideStatus = true;
                            progressBar.setVisibility(View.GONE);
                            handler.removeCallbacks(runnable);
                            // Toast.makeText(getActivity(), "Ride has been cancelled", Toast.LENGTH_SHORT).show();
                            footer.setVisibility(View.GONE);
                            linear_cnfrmBooking.setVisibility(View.GONE);
                            linear_afterridecnfrm.setVisibility(View.GONE);
                            completeRideLayout.setVisibility(View.GONE);
                            paymentNowLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            cnfrmBooking.setVisibility(View.GONE);
                            // cnfrmBooking.setText("Driver has cancelled this ride.....");
                            //cnfrmBooking.setText("No rider found in your area. Please Try Again.");
                            // Toast.makeText(getActivity(), "No rider found in your area. Please Try Again.", Toast.LENGTH_SHORT).show();
                            cnfrmBooking.setEnabled(false);
                            clearFragment();
                        } else if (response.getJSONObject("data").getString("status").equalsIgnoreCase("failed")) {
                            //new change on 18-01-2022
                            // deleteRequest(String.valueOf(ride_id));
                            //=========================
                            stopRideTimer();
                            ride_id = 0;
                            progressBar.setVisibility(View.GONE);
                            handler.removeCallbacks(runnable);
                            footer.setVisibility(View.GONE);
                            linear_cnfrmBooking.setVisibility(View.GONE);
                            linear_afterridecnfrm.setVisibility(View.GONE);
                            completeRideLayout.setVisibility(View.GONE);
                            paymentNowLayout.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            cnfrmBooking.setVisibility(View.GONE);
                            //cnfrmBooking.setText("No rider found in your area. Please Try Again.");
//                            Toast.makeText(getActivity(), "No Driver found in your area. Please Try Again.", Toast.LENGTH_SHORT).show();
                            cnfrmBooking.setEnabled(false);
                            failedRideStatus = true;
                            //===========18-01-2022===========
                            setDisablePickUpAndDropLoc(true);
                            clearFragment();
                        } else if (response.getJSONObject("data").getString("status").equalsIgnoreCase("START_RIDE")) {
                            stopRideTimer();
                            status = "START_RIDE";
                            cancelTimer();
                            sourceLat = Double.parseDouble(response.getJSONObject("data").getString("pickup_lat"));
                            sourceLng = Double.parseDouble(response.getJSONObject("data").getString("pickup_long"));
                            destlat = Double.parseDouble(response.getJSONObject("data").getString("drop_lat"));
                            destlng = Double.parseDouble(response.getJSONObject("data").getString("drop_long"));
                            myMap.clear();
                            if (marker != null) {
                                marker.remove();
                            }
                            if (marker2 != null) {
                                marker2.remove();
                            }
                            if (mCurrLocationMarker != null) {
                                mCurrLocationMarker.remove();
                            }
                            //checkTwo=true;
                            check = false;
                            loginToFirebase(response.getJSONObject("data").getString("email"));
                            userCurrentLocation();
                            try {
//                                userCurrentLocationMarker(sourceLat,sourceLng,0);
                                userDestinationLocationMarker();
                                setDestinationTrackingPolyLine(0);
                            } catch (Exception ex) {
                                Log.e("LOCATION_EXCEPTION", ex.toString());
                            }
                            header.setVisibility(View.VISIBLE);
                            min_max.setVisibility(View.GONE);
                            mainHeaderLayout.setVisibility(View.VISIBLE);
                            Log.e("main_header", "start_ride");
                            footer.setVisibility(View.VISIBLE);
                            linear_cnfrmBooking.setVisibility(View.GONE);
                            linear_afterridecnfrm.setVisibility(View.GONE);
                            paymentNowLayout.setVisibility(View.GONE);
                            completeRideLayout.setVisibility(View.GONE);
                            cnfrmBooking.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.VISIBLE);
                            driverNames = response.getJSONObject("data").getString("driver_lastname");
                            amount = response.getJSONObject("data").getString("amount");
                            txtstartDriverName.setText("Your ride is going with " + driverNames);
                            txtAmount.setText("Amount to be paid $ " + amount);

                            btnStartRecording.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    new androidx.appcompat.app.AlertDialog.Builder(context).setMessage("Start recording is on please do not kill your app").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startRecording();
                                            btnStartRecording.setVisibility(View.GONE);
                                            btnStopRecording.setVisibility(View.VISIBLE);
                                        }
                                    }).show();

                                }
                            });

                            btnStopRecording.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    stopRecording();
                                    btnStartRecording.setVisibility(View.VISIBLE);
                                    btnStopRecording.setVisibility(View.GONE);
                                }
                            });
                        } else if (response.getJSONObject("data").getString("status").equalsIgnoreCase("COMPLETED") && (response.getJSONObject("data").getString("is_technical_issue").equalsIgnoreCase("Yes") || response.getJSONObject("data").getString("is_technical_issue").equalsIgnoreCase("No"))) {
                            if (response.getJSONObject("data").getString("is_technical_issue").equalsIgnoreCase("No")) {
                                if (!response.getJSONObject("data").getString("payment_status").equalsIgnoreCase("COMPLETED")) {
//                                    if (!securityPopupShown)
                                    try {
                                        if (!securityAlert.isShowing()) {
                                            showSecurityAlert();
                                        }
                                    } catch (Exception ex) {
                                        showSecurityAlert();
                                    }
                                }
                            }
                            if (response.getJSONObject("data").getString("is_technical_issue").equalsIgnoreCase("Yes")) {
                                endRide();
                            }
                            //New Code Start
//                            if (response.getJSONObject("data").getString("is_technical_issue").equalsIgnoreCase("Yes")) {
//                                SessionManager.setActiveRideId(null);
//                                myMap.clear();
//                                if (marker != null) {
//                                    marker.remove();
//                                }
//                                if (marker2 != null) {
//                                    marker2.remove();
//                                }
//                                if (mCurrLocationMarker != null) {
//                                    mCurrLocationMarker.remove();
//                                }
//                                if (polyline != null) {
//                                    polyline.remove();
//                                }
//                                if (driverMarker != null) {
//                                    driverMarker.remove();
//                                }
//                                header.setVisibility(View.GONE);
//                                min_max.setVisibility(View.GONE);
//                                mainHeaderLayout.setVisibility(View.GONE);
//                                Log.e("main_header", "complete_ride");
//                                footer.setVisibility(View.VISIBLE);
//                                linear_cnfrmBooking.setVisibility(View.GONE);
//                                linear_afterridecnfrm.setVisibility(View.GONE);
//                                // completeRideLayout.setVisibility(View.VISIBLE);
//                                paymentNowLayout.setVisibility(View.VISIBLE);
//                                cnfrmBooking.setVisibility(View.GONE);
//                                startRideLayout.setVisibility(View.GONE);
//                                String ride_amount = response.getJSONObject("data").getString("total_amount");
//                                Log.d("cancellation_charge_Price", response.getJSONObject("data").getString("cancellation_charge"));
//                                String cancellationCharge = "0.0";
//                                if (!response.getJSONObject("data").getString("cancellation_charge").equals("null") && !response.getJSONObject("data").getString("cancellation_charge").equalsIgnoreCase("0.00")) {
//                                    txt_cancel_title.setVisibility(View.VISIBLE);
//                                    txt_total.setVisibility(View.VISIBLE);
//                                    txt_cancellation_charge.setVisibility(View.VISIBLE);
//                                    line.setVisibility(View.VISIBLE);
//                                    txt_total_title.setVisibility(View.VISIBLE);
//                                    cancellationCharge = response.getJSONObject("data").getString("cancellation_charge");
//                                } else {
//                                    txt_cancel_title.setVisibility(View.GONE);
//                                    txt_total.setVisibility(View.GONE);
//                                    txt_cancellation_charge.setVisibility(View.GONE);
//                                    line.setVisibility(View.GONE);
//                                    txt_total_title.setVisibility(View.GONE);
//                                }
//                                txtDriverName.setText("How was your experience with " + driverNames);
//                                driver_id = response.getJSONObject("data").getString("driver_id");
//                                ride_id = Integer.parseInt(response.getJSONObject("data").getString("ride_id"));
//                                profileUrl = response.getJSONObject("data").getString("profile_pic");
//                                double rideAmount = Double.parseDouble(ride_amount);
//                                double cancelCharge = 0;
//                                try {
//                                    cancelCharge = Double.parseDouble(cancellationCharge);
//                                } catch (Exception ex) {
//                                    cancelCharge = 0;
//                                }
//                                double total = rideAmount + cancelCharge;
//                                txt_ride_amount.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(rideAmount));
//                                txt_cancellation_charge.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(cancelCharge));
//                                txt_total.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(total));
//                                if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase(" ")) {
//                                    Glide.with(context).load(R.drawable.user_default).into(driver_pics);
//                                } else {
//                                    Glide.with(context).load(profileUrl).into(driver_pics);
//                                }
//                                Log.d("pradnya", "success");
//                                check = false;
//                                checkTwo = false;
//                                //Changes on 04/10/2021
//                                btnPayNow.setOnClickListener(e -> {
//                                    PendingPojo pendingRequestPojo = new PendingPojo();
//                                    pendingRequestPojo.setAmount(ride_amount);
//                                    pendingRequestPojo.setRideId(String.valueOf(ride_id));
//                                    pendingRequestPojo.setPaymentStatus(paymentStatus);
//                                    pendingRequestPojo.setTxnId(txnId);
//                                    Bundle bundle = new Bundle();
//                                    bundle.putSerializable("data", pendingRequestPojo);
//                                    PaymentFragment paymentFragment = new PaymentFragment();
//                                    paymentFragment.setArguments(bundle);
//                                    footer.setVisibility(View.GONE);
//                                    paymentNowLayout.setVisibility(ConstraintLayout.GONE);
//                                    if (context != null)
//                                        ((HomeActivity) context).changeFragment(paymentFragment, "Payment Method");
//                                    timerTask.cancel();
//                                });
//                            }
                            //New Code End

                        } else if (response.getJSONObject("data").getString("status").equalsIgnoreCase("COMPLETED") && response.getJSONObject("data").getString("payment_status").equalsIgnoreCase("COMPLETED")) {
                            Log.e("TIMER", "success");
                            SessionManager.setActiveRideId(null);
                            status = "COMPLETED";
                            myMap.clear();
                            if (marker != null) {
                                marker.remove();
                            }
                            if (marker2 != null) {
                                marker2.remove();
                            }
                            if (mCurrLocationMarker != null) {
                                mCurrLocationMarker.remove();
                            }
                            if (polyline != null) {
                                polyline.remove();
                            }
                            if (driverMarker != null) {
                                driverMarker.remove();
                            }
                            min_max.setVisibility(View.GONE);

                            Log.e("main_header", "complete_payment_ride");
                            linear_cnfrmBooking.setVisibility(View.GONE);
                            linear_afterridecnfrm.setVisibility(View.GONE);
                            if (!feedbackShown) {
                                footer.setVisibility(View.VISIBLE);
                                completeRideLayout.setVisibility(View.VISIBLE);
                                header.setVisibility(View.GONE);
                                mainHeaderLayout.setVisibility(View.GONE);
                            } else {
                                footer.setVisibility(View.GONE);
                                completeRideLayout.setVisibility(View.GONE);
                                header.setVisibility(View.VISIBLE);
                                mainHeaderLayout.setVisibility(View.VISIBLE);
                            }
                            paymentNowLayout.setVisibility(View.GONE);
                            cnfrmBooking.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            String ride_amount = response.getJSONObject("data").getString("total_amount");
                            if(Utils.isNullOrEmpty(driverNames)){
                                txtDriverName.setText("How was your experience with Boss.");
                            }else{
                                txtDriverName.setText("How was your experience with " + driverNames);
                            }
                            driver_id = response.getJSONObject("data").getString("driver_id");
                            ride_id = Integer.parseInt(response.getJSONObject("data").getString("ride_id"));
                            profileUrl = response.getJSONObject("data").getString("profile_pic");
                            txt_ride_amount.setText(ride_amount);

                            if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase(" ")) {
                                Glide.with(context).load(R.drawable.user_default).into(driver_pics);
                            } else {
                                Glide.with(context).load(profileUrl).into(driver_pics);
                            }
                            Log.d("pradnya", "success");
                            check = false;
                            checkTwo = false;

                            //Changes on 04/10/2021

                        } else if (response.getJSONObject("data").getString("status").equalsIgnoreCase("COMPLETED") && !response.getJSONObject("data").getString("payment_status").equalsIgnoreCase("COMPLETED")) {
                            Log.e("TIMER", "success");

                            SessionManager.setActiveRideId(null);
                            myMap.clear();
                            if (marker != null) {
                                marker.remove();
                            }

                            if (marker2 != null) {
                                marker2.remove();
                            }
                            if (mCurrLocationMarker != null) {
                                mCurrLocationMarker.remove();
                            }
                            if (polyline != null) {
                                polyline.remove();
                            }
                            if (driverMarker != null) {
                                driverMarker.remove();
                            }
                            header.setVisibility(View.GONE);
                            min_max.setVisibility(View.GONE);
                            mainHeaderLayout.setVisibility(View.GONE);
                            Log.e("main_header", "complete_ride");
                            footer.setVisibility(View.VISIBLE);
                            linear_cnfrmBooking.setVisibility(View.GONE);
                            linear_afterridecnfrm.setVisibility(View.GONE);
                            // completeRideLayout.setVisibility(View.VISIBLE);
                            paymentNowLayout.setVisibility(View.VISIBLE);
                            cnfrmBooking.setVisibility(View.GONE);
                            startRideLayout.setVisibility(View.GONE);
                            String ride_amount = response.getJSONObject("data").getString("total_amount");
                            Log.d("cancellation_charge_Price", response.getJSONObject("data").getString("cancellation_charge"));
                            String cancellationCharge = "0.0";
                            if (!response.getJSONObject("data").getString("cancellation_charge").equals("null") && !response.getJSONObject("data").getString("cancellation_charge").equalsIgnoreCase("0.00")) {

                                txt_cancel_title.setVisibility(View.VISIBLE);
                                txt_total.setVisibility(View.VISIBLE);
                                txt_cancellation_charge.setVisibility(View.VISIBLE);
                                line.setVisibility(View.VISIBLE);
                                txt_total_title.setVisibility(View.VISIBLE);
                                cancellationCharge = response.getJSONObject("data").getString("cancellation_charge");
                            } else {
                                txt_cancel_title.setVisibility(View.GONE);
                                txt_total.setVisibility(View.GONE);
                                txt_cancellation_charge.setVisibility(View.GONE);
                                line.setVisibility(View.GONE);
                                txt_total_title.setVisibility(View.GONE);
                            }
                            if(Utils.isNullOrEmpty(driverNames)){
                                txtDriverName.setText("How was your experience with Boss.");
                            }else{
                                txtDriverName.setText("How was your experience with " + driverNames);
                            }
                            driver_id = response.getJSONObject("data").getString("driver_id");
                            ride_id = Integer.parseInt(response.getJSONObject("data").getString("ride_id"));
                            profileUrl = response.getJSONObject("data").getString("profile_pic");
                            double rideAmount = Double.parseDouble(ride_amount);
                            double cancelCharge = 0;
                            try {
                                cancelCharge = Double.parseDouble(cancellationCharge);
                            } catch (Exception ex) {
                                cancelCharge = 0;
                            }
                            double total = rideAmount + cancelCharge;
                            txt_ride_amount.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(rideAmount));
                            txt_cancellation_charge.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(cancelCharge));
                            txt_total.setText(DecimalFormat.getCurrencyInstance(Locale.US).format(total));

                            if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase(" ")) {
                                Glide.with(context).load(R.drawable.user_default).into(driver_pics);
                            } else {
                                Glide.with(context).load(profileUrl).into(driver_pics);
                            }
                            Log.d("pradnya", "success");
                            check = false;
                            checkTwo = false;

                            //Changes on 04/10/2021
                            btnPayNow.setOnClickListener(e -> {
                                PendingPojo pendingRequestPojo = new PendingPojo();
                                pendingRequestPojo.setAmount(ride_amount);
                                pendingRequestPojo.setRideId(String.valueOf(ride_id));
                                pendingRequestPojo.setPaymentStatus(paymentStatus);
                                pendingRequestPojo.setTxnId(txnId);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("data", pendingRequestPojo);
                                PaymentFragment paymentFragment = new PaymentFragment();
                                paymentFragment.setArguments(bundle);
                                footer.setVisibility(View.GONE);
                                paymentNowLayout.setVisibility(ConstraintLayout.GONE);
                                if (context != null)
                                    ((HomeActivity) context).changeFragment(paymentFragment, "Payment Method");

//                                changeFragment();

//                                header.setVisibility(View.VISIBLE);
//                                mainHeaderLayout.setVisibility(View.VISIBLE);
                                timerTask.cancel();
                                // ride_id = 0;

                            });

                        }
                    } else {
                        if (!isRideCancelled) if (!checkStatus) {
                            Toast.makeText(getActivity(), "No driver found in your area... please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("GET_RIDE_RIDE_API", errorResponse.toString());
                Toast.makeText(getActivity(), "Get ride status failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.d("GET_RIDE_RIDE_API", throwable.toString());
                Toast.makeText(getActivity(), "Get ride status API failed", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }


    public static HomeFragment newInstance() {
        HomeFragment myFragment = new HomeFragment();
        return myFragment;
    }

    //on connections
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        int result = ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            mGoogleApiClient.connect();
            android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (location == null) {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                }
            } else {
                //setting current location initially
                if (initialVal.equalsIgnoreCase("1")) {
                    initialVal = "2";
                    setCurrentLocation();
                }

                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                Log.d(TAG, "currentLatitude : " + currentLatitude + "\n currentLongitude : " + currentLongitude);

                if (marker != null) {
                    marker.remove();
                }

                if (marker2 != null) {
                    marker2.remove();
                }
                BitmapDescriptor defaultIcon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                BitmapDescriptor customIcon1 = BitmapDescriptorFactory.fromResource(R.drawable.taxi_old);//for png/jpeg/jpg
                BitmapDescriptor customIcon = bitmapDescriptorFromVector(getActivity(), R.drawable.origin_icon);//for svg

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(currentLatitude, currentLongitude));
                markerOptions.icon(customIcon);
                marker = myMap.addMarker(markerOptions);
                marker.setVisible(false);

                myMap.setMyLocationEnabled(true);
                myMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLatitude, currentLongitude), 15));
                // Zoom in the Google Map
                myMap.animateCamera(CameraUpdateFactory.zoomTo(15));


                if (!currentLatitude.equals(0.0) && !currentLongitude.equals(0.0)) {
                    if (!flag) {
                        NearBy(String.valueOf(currentLatitude), String.valueOf(currentLongitude));
                    }
                } else {
                    Toast.makeText(getActivity(), getString(R.string.couldnt_get_location), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            askCompactPermissions(permissionAsk, new PermissionResult() {
                @Override
                public void permissionGranted() {

                }

                @Override
                public void permissionDenied() {
                }

                @Override
                public void permissionForeverDenied() {
                    Snackbar.make(rootView, getString(R.string.allow_permission), Snackbar.LENGTH_LONG).show();
                    openSettingsApp(getActivity());
                }
            });
        }
    }

    //current location
    private void setCurrentLocation() {
        if (!GPSEnable()) {
            tunonGps();
        } else {
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                try {
                    // Use fields to define the data types to return.
                    List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

                    // Use the builder to create a FindCurrentPlaceRequest.
                    FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

                    // Call findCurrentPlace and handle the response (first check that the user has granted permission).
                    if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
                        placeResponse.addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FindCurrentPlaceResponse response = task.getResult();
                                if (response != null && response.getPlaceLikelihoods() != null) {
                                    try {
                                        PlaceLikelihood placeLikelihood = response.getPlaceLikelihoods().get(0);
                                        pickup = placeLikelihood.getPlace();
                                        pickup_location.setText(placeLikelihood.getPlace().getAddress());
                                        sourceLat = pickup.getLatLng().latitude;
                                        sourceLng = pickup.getLatLng().longitude;
                                        current_location.setColorFilter(ContextCompat.getColor(context, R.color.white));
                                    } catch (Exception e) {
                                        Log.e(TAG, "Some Error: " + e.toString());
                                    }
                                }
                            } else {
                                Exception exception = task.getException();
                                if (exception instanceof ApiException) {
                                    ApiException apiException = (ApiException) exception;
                                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                                }
                            }
                        });
                    }
                } catch (Exception e) {

                }
            }
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

    //on Location changed
    @Override
    public void onLocationChanged(android.location.Location location) {
        if (location != null) {
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();
        }
        if (mMarkers != null) {
            mMarkers.clear();
        }

    }

    //appling fonts
    public void applyfonts() {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/montserrat_regular.ttf");
        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/montserrat_bold.ttf");
        pickup_location.setTypeface(font);
        drop_location.setTypeface(font);
//        ride_now.setTypeface(font1);
        cnfrmBooking.setTypeface(font);
    }

    //for near by
    public void NearBy(String latitude, String longitude) {
        flag = true;
        RequestParams params = new RequestParams();
        params.put("lat", latitude);
        params.put("long", longitude);
        Server.setHeader(getKEY());
        Server.get("nearby?", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    checkStatus = true;

                    if (response.has("status") && response.getBoolean("status")) {
                        Gson gson = new GsonBuilder().create();
                        List<NearbyData> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<NearbyData>>() {
                        }.getType());

                        multipleMarker(list);

                        //cost = response.getJSONObject("fair").getString("cost");
                        //unit = response.getJSONObject("fair").getString("unit");

                        //SessionManager.setUnit(unit);
                        //SessionManager.setCost(cost);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (getActivity() != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    //getting current Location
    public void getcurrentlocation() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                // The next two lines tell the new client that â€œthisâ€ current class will handle connection stuff
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API).build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(300 * 1000);
        mLocationRequest.setFastestInterval(300 * 1000);
    }

    //turning on Gps
    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(300 * 1000);
            mLocationRequest.setFastestInterval(300 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

            // **************************
            builder.setAlwaysShow(true); // this is the key ingredient
            // **************************

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can
                            // initialize location
                            // requests here.
                            getcurrentlocation();
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

    //GPS Enabled
    public Boolean GPSEnable() {
        GPSTracker gpsTracker = new GPSTracker(getActivity());
        if (gpsTracker.canGetLocation()) {
            return true;
        } else {
            return false;
        }
    }

    //on Map ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        myMap = googleMap;
        myMap.setMaxZoomPreference(16);
        setCustomMapStyle();
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        myMap.setMyLocationEnabled(true);

        myMap.setTrafficEnabled(false);
        myMap.getUiSettings().setMyLocationButtonEnabled(false);


        Log.d("map", "onmapready");
        /*myMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(final Marker marker) {

                View v = getActivity().getLayoutInflater().inflate(R.layout.view_custom_marker, null);

                LatLng latLng = marker.getPosition();
                TextView title = (TextView) v.findViewById(R.id.t);
                TextView t1 = (TextView) v.findViewById(R.id.t1);
                TextView t2 = (TextView) v.findViewById(R.id.t2);
                ImageView imageView = (ImageView) v.findViewById(R.id.profile_image);
                Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
                t1.setTypeface(font);
                t2.setTypeface(font);
                String name = marker.getTitle();
                title.setText(name);
                String info = marker.getSnippet();
                t1.setText(info);

                NearbyData nearbyData = (NearbyData) marker.getTag();
                if (nearbyData != null) {
                    pass.setVehicleName(nearbyData.getVehicle_info());
                    txt_info.setText(nearbyData.getVehicle_info());
                    driver_id = nearbyData.getUser_id();
                    drivername = marker.getTitle();
                    t2.setVisibility(View.VISIBLE);
                } else {
                    t2.setVisibility(View.GONE);
                }
                txt_cost.setText(cost + "  " + unit);
                //txt_address.setText(getAdd(Double.valueOf(nearbyData.getLatitude()), Double.valueOf(nearbyData.getLongitude())) + " ");
                return v;
            }
        });

        myMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                NearbyData nearbyData = (NearbyData) marker.getTag();
                if (nearbyData != null) {
                    driver_id = nearbyData.getUser_id();
                    drivername = marker.getTitle();
                }
                if (header.getVisibility() == View.VISIBLE && footer.getVisibility() == View.VISIBLE) {
                    header.startAnimation(animFadeOut);
                    footer.startAnimation(animFadeOut);
                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.GONE);
                } else {
                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.VISIBLE);
                    header.startAnimation(animFadeIn);
                    footer.startAnimation(animFadeIn);
                }
            }
        });

        if (myMap != null) {
            tunonGps();
        }*/

        //loginToFirebase(driverEmail);
    }


    private void setCustomMapStyle() {
        try {
            // Load the custom map style from the raw resource
            boolean success = myMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style));
            if (!success) {
                // Handle the case if the custom style couldn't be loaded
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    private String getAdd(double latitude, double longitude) {
        String finalAddress = null;
        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            finalAddress = address + ", " + city + "," + state + "," + country;
        } catch (Exception e) {

        }
        return finalAddress;
    }

    //adding svg as custom google location marker
    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    //live tracker added by pradnya

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //idle Camera setup
    private void configureCameraIdle() {
        onCameraIdleListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                latLng = myMap.getCameraPosition().target;
                Geocoder geocoder = new Geocoder(getActivity());
                try {
                    Log.d("P lat long ", "" + latLng.latitude + " " + latLng.longitude);
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10);
                    if (addressList != null && addressList.size() > 0) {
                        String locality = "";
                        String country = "";
                        if (addressList.get(0).getAddressLine(0) != null) {
                            locality = addressList.get(0).getAddressLine(0);
                        }
                        if (addressList.get(0).getCountryName() != null) {
                            country = addressList.get(0).getCountryName();
                        }
                        if (!locality.isEmpty() && !country.isEmpty()) {
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static void setDriverMarker(DataSnapshot dataSnapshot) {
        String key = dataSnapshot.getKey();
        HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
        lat = Double.parseDouble(value.get("latitude").toString());
        lng = Double.parseDouble(value.get("longitude").toString());

        //temp changes
//        setDriverInitialMarker(lat, lng);
//        setDriverToUserTrackingPolyline(lat, lng);

        Log.d(TAG, "set driver marker");
        // temp changes

        LatLng location = new LatLng(lat, lng);
        if (marker != null) {
            marker.remove();
        }
        if (!mMarkers.containsKey(key)) {
            mMarkers.clear();
            MarkerOptions marker = new MarkerOptions().position(location).title("Driver");
            marker.rotation(bearing + 98 + 180);
            marker.anchor(0.5f, 0.5f);
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.car_ride));
            mMarkers.put(key, myMap.addMarker(marker));
        } else {
            mMarkers.get(key).setPosition(location);
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : mMarkers.values()) {
            builder.include(marker.getPosition());
        }
        myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 300));
        /*mMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));*/
    }

    private static void updateDistanceAndTime(double lat, double lng, double speedIs10MetersPerMinute) {

        Location userLocation = new Location("User");
        userLocation.setLatitude(locationCt.getLatitude());
        userLocation.setLongitude(locationCt.getLongitude());
        Location driverLocation = new Location("Driver");
        driverLocation.setLatitude(lat);
        driverLocation.setLongitude(lng);

        double distanceDiffinMeter = driverLocation.distanceTo(userLocation);
        float mile = (float) (distanceDiffinMeter / 1609.34f);
        distance.setText(String.format(Locale.US, "%.2f %s", mile, "miles"));
        try {
            float estimatedDriveTimeInMinutes = (float) (distanceDiffinMeter / speedIs10MetersPerMinute);
            time.setText(String.format(Locale.US, "%.2f %s", speedIs10MetersPerMinute, "hours"));
        } catch (Exception ex) {
            time.setText("__" + ex.toString());
        }

    }

    public String getDurationForRoute(String origin, String destination) {
        // - We need a context to access the API
        GeoApiContext geoApiContext = new GeoApiContext.Builder().apiKey(instance.getString(R.string.google_android_map_api_key)).build();

        // - Perform the actual request
        DirectionsResult directionsResult = null;
        try {
            directionsResult = DirectionsApi.newRequest(geoApiContext).mode(TravelMode.DRIVING).origin(origin).destination(destination).await();
        } catch (com.google.maps.errors.ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // - Parse the result
        DirectionsRoute route = directionsResult.routes[0];
        DirectionsLeg leg = route.legs[0];
        Duration duration = leg.duration;
        return duration.humanReadable;
    }

    //userCurrent Location
    private void userCurrentLocation() {
        myMap.setOnCameraIdleListener(onCameraIdleListener);
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
                myMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
            myMap.setMyLocationEnabled(true);
        }
        LocationManager locationManagerCt = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationCt = locationManagerCt.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLastLocation = locationCt;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        if (locationCt != null) {
            LatLng latLng = new LatLng(locationCt.getLatitude(), locationCt.getLongitude());
            myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            myMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            Marker markerName = myMap.addMarker(new MarkerOptions().position(latLng));
            markerName.remove();

            myMap.setMyLocationEnabled(true);
            myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Zoom in the Google Map
            myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
    }

    public void zoomRoute(GoogleMap googleMap, List<LatLng> lstLatLngRoute) {

        if (googleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding));
    }

    private static Marker driverMarker;

    //setting Intial marker
    private static void setDriverInitialMarker(double driverLat, double driverLong, float bearing) {
        LatLng latLng = new LatLng(driverLat, driverLong);
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if (driverMarker != null) {
            driverMarker.remove();
        }
//        int height = 150;
//        int width = 220;
//        BitmapDrawable bitmapdraw = (BitmapDrawable) instance.getResources().getDrawable(R.drawable.car_ride);
//        Bitmap b = bitmapdraw.getBitmap();
//        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        driverMarker = myMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).rotation(bearing + 98 + 180).flat(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_ride)));

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        myMap.setMyLocationEnabled(true);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        myMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }

    //rider marker
    private static void userDestinationLocationMarker() {
        LatLng latLng = new LatLng(destlat, destlng);
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        myMap.setMyLocationEnabled(true);
        myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        // Zoom in the Google Map
        myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
    }

    private static Marker userLocationMarker = null;

    //userCurrentLocationMArker Accept
    private static void userCurrentLocationMarkerAccept() {
        try {
//            LatLng latLng = new LatLng(locationCt.getLatitude(),
//                    locationCt.getLongitude());

            LatLng latLng = new LatLng(sourceLat, sourceLng);
            myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            userLocationMarker = myMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            myMap.setMyLocationEnabled(true);
            myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Zoom in the Google Map
            myMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        } catch (Exception ex) {
            Log.e("LOCATION_EXCEPTION", ex.getMessage());
        }
    }

    //user curreent marker
    private static void userCurrentLocationMarker(double sourceLat, double sourceLng, float bearing) {
        try {
//            LatLng latLng = new LatLng(locationCt.getLatitude(),
//                    locationCt.getLongitude());
            LatLng latLng = new LatLng(sourceLat, sourceLng);
            myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//            int height = 150;
//            int width = 220;
//            BitmapDrawable bitmapdraw = (BitmapDrawable) instance.getResources().getDrawable(R.drawable.car_ride);
//            Bitmap b = bitmapdraw.getBitmap();
//            Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

            if (driverDestinationMarker != null) {
                driverDestinationMarker.remove();
            }
            driverDestinationMarker = myMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).rotation(bearing + 98 + 180).flat(true).icon(BitmapDescriptorFactory.fromBitmap(smallMarker)));
            //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            myMap.setMyLocationEnabled(true);
//            myMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            // Zoom in the Google Map
            myMap.animateCamera(CameraUpdateFactory.zoomTo(27));
        } catch (Exception ex) {
            Log.e("LOCATION_EXCEPTION", ex.getMessage());
        }
    }

    //checking permission
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(getActivity()).setTitle("Location Permission Needed").setMessage("This app needs the Location permission, please accept to use location functionality").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Prompt the user once explanation has been shown
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                    }
                }).create().show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    //loginTo Firebase
    private static void loginToFirebase(String driverEmail) {
        // String email = getString(R.string.firebase_email);
        // Authenticate with Firebase and subscribe to updates
        String password = context.getString(R.string.firebase_password);

        FirebaseAuth.getInstance().signInWithEmailAndPassword(driverEmail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    subscribeToUpdates(driverNames, String.valueOf(ride_id));
                    Log.d(TAG, "fireba0se auth success in fragment");
                } else {
                    Log.d(TAG, "firebase auth failed" + task.getException().getMessage());
//                    Toast.makeText(getApplicationContext(), "Firebase Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    //updating location in firebase
    private static void subscribeToUpdates(String locName, String locRideId) {
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path));
        ///DatabaseReference ref = FirebaseDatabase.getInstance().getReference(locName + locRideId);
        ref = FirebaseDatabase.getInstance().getReference("rides").child(locName + locRideId).child(locRideId);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                //new change on 27-09-2022

                try {
                    if (instance.status.equalsIgnoreCase("ACCEPTED")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                instance.requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("snap", dataSnapshot + "");
                                        updateDriverMarkerLocation(dataSnapshot);
                                        try {
                                            HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                                            LatLng origins = new LatLng(Double.parseDouble(value.get("latitude").toString()), Double.parseDouble(value.get("longitude").toString()));
                                            LatLng destination = new LatLng(sourceLat, sourceLng);
                                            //getDistance(origins.toString(),destination.toString());

                                            //speed.setText(String.format(Locale.US, "%.2f m/s", Float.parseFloat(value.get("speed").toString())));
                                        } catch (Exception ex) {

                                        }
                                    }
                                });
                            }

                        }).start();
                    } else if (instance.status.equalsIgnoreCase("START_RIDE")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                instance.requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        updateDriverMarkerLocation(dataSnapshot);

                                    }
                                });
                            }

                        }).start();
                    }
                } catch (Exception ex) {
                }

//                Toast.makeText(instance.requireContext(), "on child added", Toast.LENGTH_SHORT).show();
                //new change ends 27-09-2022

//                if (check) {
//                    setDriverMarker(dataSnapshot);
//                    Log.e("datasnap", dataSnapshot.toString());
//
//                    Toast.makeText(instance.requireContext(), "on child added", Toast.LENGTH_SHORT).show();
//                }
//                /*if (checkTwo) {
//                    setDriverMarker(dataSnapshot);
//                }*/
//                statusCheck();
                Log.d(TAG, "on child added");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {

                //new change on 27-09-2022
                try {
                    if (instance.status.equalsIgnoreCase("ACCEPTED")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                instance.requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            HashMap<String, Object> value = (HashMap<String, Object>) dataSnapshot.getValue();
                                            // speed.setText(String.format(Locale.US, "%.2f m/s", Float.parseFloat(value.get("speedAccuracyMetersPerSecond").toString())));
                                        } catch (Exception ex) {

                                        }
                                        updateDriverMarkerLocation(dataSnapshot);
                                    }
                                });
                            }
                        }).start();
                    } else if (instance.status.equalsIgnoreCase("START_RIDE")) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                instance.requireActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        updateDriverMarkerLocation(dataSnapshot);

                                    }
                                });
                            }

                        }).start();
                    }
                } catch (Exception ex) {

                }
//                Toast.makeText(instance.requireContext(), "on child changed", Toast.LENGTH_SHORT).show();
                //new change ends 27-09-2022

//                if (check) {
//                    setDriverMarker(dataSnapshot);
//
//                    Toast.makeText(instance.requireContext(), "on childe changed", Toast.LENGTH_SHORT).show();
//                }
//               /* if (checkTwo) {
//                    setDriverMarker(dataSnapshot);
//                }*/
//                statusCheck();
                Log.d(TAG, "on child changed");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                Log.d(TAG, "on child moved");
                Toast.makeText(instance.requireContext(), "on child moved", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "on child removed");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });


    }

    //updating driver marker
    private static void updateDriverMarkerLocation(DataSnapshot dataSnapshot) {
        HashMap<String, String> value = new HashMap();

        String KEY = dataSnapshot.getKey();
        Log.d("KEY", dataSnapshot.getKey() + "");

        String VALUE = dataSnapshot.getValue().toString();
        Log.d("VALUE", dataSnapshot.getValue() + "");

        value.put(KEY, VALUE);

        Log.d("size", dataSnapshot + "");
//        Log.d("size",dataSnapshot.child("latitude")+"");
//        Log.d("size",dataSnapshot.child("longitude")+"");
        float bear = 0.0f;

        if (value.containsKey("latitude")) {
            driverLat = Double.parseDouble(value.get("latitude").toString());
        }
        if (value.containsKey("longitude")) {
            driverLong = Double.parseDouble(value.get("longitude").toString());
        }
        if (value.containsKey("bearing")) {
            bearing = Float.parseFloat(value.get("bearing").toString());
            Log.d("bearing", bearing + "");
        }
        if (value.containsKey("bearing")) {
            bear = Float.parseFloat(value.get("bearing").toString());
            Log.d("bearing", bear + "");
        }


//        Location location = new Location("Driver");
//        location.setLatitude(lat);
//        location.setLongitude(lng);
        LatLng latLng = new LatLng(driverLat, driverLong);


//        updateDistanceAndTime(lat, lng, Double.parseDouble(value.get("speedAccuracyMetersPerSecond").toString()));
//                setDriverInitialMarker(lat, lng);
//        setDriverToUserTrackingPolyline(lat, lng);


//            instance.updateDriverMarkerTimer(lat, lng, locationCt.getLatitude(), locationCt.getLongitude());
        if (instance.status.equalsIgnoreCase("ACCEPTED")) {
            if (!instance.apiIsRunning) {
//                instance.updateDriverMarker_getRideStatus(driverLat, driverLong, locationCt.getLatitude(), locationCt.getLongitude());
                instance.updateDriverMarker_getRideStatus(driverLat, driverLong, sourceLat, sourceLng, bear);
            }
//            changePositionSmoothly(driverMarker, latLng, Float.parseFloat(value.get("bearing").toString()));
        } else if (instance.status.equalsIgnoreCase("START_RIDE")) {
//            changePositionSmoothly(driverDestinationMarker, latLng, Float.parseFloat(value.get("bearing").toString()));
            if (userLocationMarker != null) {
                userLocationMarker.remove();
            }
            if (!instance.apiIsRunning) {
                instance.updateDriverMarker_getRideStatus(sourceLat, sourceLng, destlat, destlng, bear);
            }
        }
    }

    private void unSubscribeToUpdates(String locName, String locRideId) {
        FirebaseAuth.getInstance().signOut();
        Log.d(TAG, "firebase auth sign out in fragment");
    }

    //change position smoothly
    static void changePositionSmoothly(final Marker myMarker, final LatLng newLatLng, final Float bearing) {

        final LatLng startPosition = myMarker.getPosition();
        final LatLng finalPosition = newLatLng;
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final Interpolator interpolator = new AccelerateDecelerateInterpolator();
        final float durationInMs = 3000;
        final boolean hideMarker = false;

        handler.post(new Runnable() {
            long elapsed;
            float t;
            float v;

            @Override
            public void run() {
                myMarker.setRotation(190 + bearing);
                // Calculate progress using interpolator
                elapsed = SystemClock.uptimeMillis() - start;
                t = elapsed / durationInMs;
                v = interpolator.getInterpolation(t);

                LatLng currentPosition = new LatLng(startPosition.latitude * (1 - t) + finalPosition.latitude * t, startPosition.longitude * (1 - t) + finalPosition.longitude * t);

                myMarker.setPosition(currentPosition);

                // Repeat till progress is complete.
                if (t < 1) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        myMarker.setVisible(false);
                    } else {
                        myMarker.setVisible(true);
                    }
                }
                driverLat = newLatLng.latitude;
                driverLong = newLatLng.longitude;
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(myMarker.getPosition());
                myMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 50));
            }
        });
    }

    //status checking
    public static void statusCheck() {
        if (context != null) {
            final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();
            } else {
                if (check) {
                    setTrackingPolyLine();
                }
                /*if(checkTwo){
                    setDestinationTrackingPolyLine();
                }*/
            }
        }
    }

    //alert dialog for no gps
    private static void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    //pollyline
    private void setCurrentToDestinationPolyLine() {

        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList<>();
        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder().apiKey(getResources().getString(R.string.google_android_map_api_key)).build();
        DirectionsApiRequest req = DirectionsApi.getDirections(context, currentLatitude + "," + currentLongitude, destlat + "," + destlng);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }


        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(ContextCompat.getColor(getApplicationContext(), R.color.pathLine)).width(10);
            if (polyline != null) {
                polyline.remove();
            }
            polyline = myMap.addPolyline(opts);
        }
        myMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //setting poluline
    private static void setTrackingPolyLine() {
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList<>();
        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder().apiKey(getApplicationContext().getResources().getString(R.string.google_android_map_api_key)).build();
        /*DirectionsApiRequest req = DirectionsApi.getDirections(context, currentLatitude + "," +
                currentLongitude, lat + "," + lng);*/
        DirectionsApiRequest req = DirectionsApi.getDirections(context, locationCt.getLatitude() + "," + locationCt.getLongitude(), lat + "," + lng);
        Log.d("driver lat long", lat + " " + lng);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
        }


        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(ContextCompat.getColor(getApplicationContext(), R.color.pathLine)).width(10);
            if (polyline != null) {
                polyline.remove();
            }
            polyline = myMap.addPolyline(opts);
        }
        myMap.getUiSettings().setZoomControlsEnabled(true);
    }

    //setting driver to user pollyline
    private static void setDriverToUserTrackingPolyline(double driverLat, double driverLong, float bearing) {
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList<>();
        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder().apiKey(getApplicationContext().getResources().getString(R.string.google_android_map_api_key)).build();
        /*DirectionsApiRequest req = DirectionsApi.getDirections(context, currentLatitude + "," +
                currentLongitude, lat + "," + lng);*/
        DirectionsApiRequest req = DirectionsApi.getDirections(context, driverLat + "," + driverLong, sourceLat + "," + sourceLng);
        Log.d("destination lat long", destlat + " " + destlng);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }

                                        }
                                    }

                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }

                                }
                            }
                        }
                    }
                }
            }


        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
            instance.apiIsRunning = false;
        }


        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(ContextCompat.getColor(getApplicationContext(), R.color.pathLine)).width(10);
            if (polyline != null) {
                polyline.remove();
            }
            polyline = myMap.addPolyline(opts);
        }
        myMap.getUiSettings().setZoomControlsEnabled(true);
        //new change
        setDriverInitialMarker(driverLat, driverLong, bearing);
//        changePositionSmoothly(driverMarker,new LatLng(driverLat, driverLong),bearing);
        instance.apiIsRunning = false;
    }

    private static void setDestinationTrackingPolyLine(double driverLat, double driverLng) {
        try {
            //Define list to get all latlng for the route
            List<LatLng> path = new ArrayList<>();
            //Execute Directions API request
            GeoApiContext context = new GeoApiContext.Builder().apiKey(getApplicationContext().getResources().getString(R.string.google_android_map_api_key)).build();
        /*DirectionsApiRequest req = DirectionsApi.getDirections(context, currentLatitude + "," +
                currentLongitude, lat + "," + lng);*/
            DirectionsApiRequest req = DirectionsApi.getDirections(context, driverLat + "," + driverLng, destlat + "," + destlng);
            Log.d("destination lat long", destlat + " " + destlng);
            try {
                DirectionsResult res = req.await();

                //Loop through legs and steps to get encoded polylines of each step
                if (res.routes != null && res.routes.length > 0) {
                    DirectionsRoute route = res.routes[0];

                    if (route.legs != null) {
                        for (int i = 0; i < route.legs.length; i++) {
                            DirectionsLeg leg = route.legs[i];
                            if (leg.steps != null) {
                                for (int j = 0; j < leg.steps.length; j++) {
                                    DirectionsStep step = leg.steps[j];
                                    if (step.steps != null && step.steps.length > 0) {
                                        for (int k = 0; k < step.steps.length; k++) {
                                            DirectionsStep step1 = step.steps[k];
                                            EncodedPolyline points1 = step1.polyline;
                                            if (points1 != null) {
                                                //Decode polyline and add points to list of route coordinates
                                                List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                                for (com.google.maps.model.LatLng coord1 : coords1) {
                                                    path.add(new LatLng(coord1.lat, coord1.lng));
                                                }
                                            }
                                        }
                                    } else {
                                        EncodedPolyline points = step.polyline;
                                        if (points != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords = points.decodePath();
                                            for (com.google.maps.model.LatLng coord : coords) {
                                                path.add(new LatLng(coord.lat, coord.lng));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            } catch (Exception ex) {
                Log.e(TAG, ex.getLocalizedMessage());

            }


            //Draw the polyline
            if (path.size() > 0) {
                PolylineOptions opts = new PolylineOptions().addAll(path).color(ContextCompat.getColor(getApplicationContext(), R.color.pathLine)).width(10);
                if (polyline != null) {
                    polyline.remove();
                }
                polyline = myMap.addPolyline(opts);
            }
            myMap.getUiSettings().setZoomControlsEnabled(true);
            instance.apiIsRunning = false;
        } catch (Exception ex) {
            instance.apiIsRunning = false;
        }
    }

    //set destination pollyline
    private static void setDestinationTrackingPolyLine(float bearing) {
//        if (SystemClock.elapsedRealtime() - lasthitTIme < 240 * 1000) {
//            return;
//        }
        lasthitTIme = SystemClock.elapsedRealtime();
        //Define list to get all latlng for the route
        List<LatLng> path = new ArrayList<>();
        //Execute Directions API request
        GeoApiContext context = new GeoApiContext.Builder().apiKey(getApplicationContext().getResources().getString(R.string.google_android_map_api_key)).build();
        /*DirectionsApiRequest req = DirectionsApi.getDirections(context, currentLatitude + "," +
                currentLongitude, lat + "," + lng);*/
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, locationCt.getLatitude() + "," +
//                locationCt.getLongitude(), destlat + "," + destlng);
        DirectionsApiRequest req = DirectionsApi.getDirections(context, driverLat + "," + driverLong, destlat + "," + destlng);
        Log.d("destination lat long", destlat + " " + destlng);
        try {
            DirectionsResult res = req.await();

            //Loop through legs and steps to get encoded polylines of each step
            if (res.routes != null && res.routes.length > 0) {
                DirectionsRoute route = res.routes[0];

                if (route.legs != null) {
                    for (int i = 0; i < route.legs.length; i++) {
                        DirectionsLeg leg = route.legs[i];
                        if (leg.steps != null) {
                            for (int j = 0; j < leg.steps.length; j++) {
                                DirectionsStep step = leg.steps[j];
                                if (step.steps != null && step.steps.length > 0) {
                                    for (int k = 0; k < step.steps.length; k++) {
                                        DirectionsStep step1 = step.steps[k];
                                        EncodedPolyline points1 = step1.polyline;
                                        if (points1 != null) {
                                            //Decode polyline and add points to list of route coordinates
                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
                                            for (com.google.maps.model.LatLng coord1 : coords1) {
                                                path.add(new LatLng(coord1.lat, coord1.lng));
                                            }
                                        }
                                    }
                                } else {
                                    EncodedPolyline points = step.polyline;
                                    if (points != null) {
                                        //Decode polyline and add points to list of route coordinates
                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
                                        for (com.google.maps.model.LatLng coord : coords) {
                                            path.add(new LatLng(coord.lat, coord.lng));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getLocalizedMessage());
            instance.apiIsRunning = false;
        }


        //Draw the polyline
        if (path.size() > 0) {
            PolylineOptions opts = new PolylineOptions().addAll(path).color(ContextCompat.getColor(getApplicationContext(), R.color.pathLine)).width(10);
            if (polyline != null) {
                polyline.remove();
            }
            polyline = myMap.addPolyline(opts);
        }
        myMap.getUiSettings().setZoomControlsEnabled(true);


        userCurrentLocationMarker(driverLat, driverLong, bearing);
        instance.apiIsRunning = false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        myMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    // Toast.makeText(MechTrackMapActivity.this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    //alert dialog
    public void AlertDialogCreate(String title, String message, final String status) {
        Drawable drawable = ContextCompat.getDrawable(requireContext(), R.mipmap.ic_warning_white_24dp);
        assert drawable != null;
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                // .setIcon(drawable)
                .setTitle(title).setMessage(message).setNegativeButton(getString(R.string.no), null).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        // sendStatus(pojo.getRide_id(), status);
                        stopRideTimer();
                        cancelRequest(String.valueOf(ride_id));
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    //accept request
    private void acceptRequest(String ride_id) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Posting ride", false, false);

        String url = Server.BASE_URL.concat("accept_ride");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loading.cancel();
                    JSONObject data = new JSONObject(response);
                    checkStatus = true;
                    Log.e("Response", "onResponse = \n " + response);

                    if (data.has("status") && data.getBoolean("status")) {
                        //Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loading.cancel();
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.cancel();
                Log.e("Response ", "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                try {
                    params.put("driver_id", SessionManager.getUserId());
                    params.put("ride_id", ride_id);
                    params.put("status", "PENDING");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth = "Bearer " + getKEY();
                headers.put("Authorization", auth);
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    //cancel request
    private void cancelRequest(String rideId) {
        final ProgressDialog loading = ProgressDialog.show(requireContext(), "", "Cancelling ride", false, false);
        setDisablePickUpAndDropLoc(true);
        String url = Server.BASE_URL.concat("accept_ride");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loading.cancel();
                    JSONObject data = new JSONObject(response);
                    unSubscribeToUpdates(driverNames, String.valueOf(ride_id));
                    Toast.makeText(requireContext(), data.getString("message"), Toast.LENGTH_SHORT).show();
                    myMap.clear();
                    if (marker != null) {
                        marker.remove();
                    }
                    if (marker2 != null) {
                        marker2.remove();
                    }
                    if (mMarkers != null) {
                        mMarkers.clear();
                    }
                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.remove();
                    }
                    if (polyline != null) {
                        polyline.remove();
                    }
                    if (driverMarker != null) {
                        driverMarker.remove();
                    }
                    drop_location.setText("");
                    SessionManager.setActiveRideId(null);
                    mainHeaderLayout.setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                    footer.setVisibility(View.GONE);
                    mainFooter.setVisibility(View.GONE);
                    linear_cnfrmBooking.setVisibility(View.GONE);
                    linear_afterridecnfrm.setVisibility(View.GONE);
                    completeRideLayout.setVisibility(View.GONE);
                    startRideLayout.setVisibility(View.GONE);
                    cnfrmBooking.setVisibility(View.GONE);
                    driver_details_layout.setVisibility(View.GONE);
                    check = false;
                    checkTwo = false;
                    checkStatus = false;
                    ride_id = 0;
                } catch (JSONException e) {
                    loading.cancel();
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.cancel();
                Log.e("Response ", "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                try {
                    params.put("driver_id", SessionManager.getUserId());
                    params.put("ride_id", rideId);
                    params.put("status", "CANCELLED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth = "Bearer " + getKEY();
                headers.put("Authorization", auth);
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    //delet request
    private void deleteRequest(String rideId) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Cancelling ride", false, false);

        String url = Server.BASE_URL.concat("accept_ride");
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    loading.cancel();
                    JSONObject data = new JSONObject(response);
                    Log.e("Response", "onResponse = \n " + response);

                    if (data.has("status") && data.getBoolean("status")) {
                        Toast.makeText(getActivity(), "No Driver found for ride", Toast.LENGTH_SHORT).show();
                        unSubscribeToUpdates(driverNames, String.valueOf(ride_id));
                        myMap.clear();
                        if (marker != null) {
                            marker.remove();
                        }
                        if (marker2 != null) {
                            marker2.remove();
                        }
                        if (driverMarker != null) {
                            driverMarker.remove();
                        }
                        if (mMarkers != null) {
                            mMarkers.clear();
                        }
                        if (mCurrLocationMarker != null) {
                            mCurrLocationMarker.remove();
                        }
                        if (polyline != null) {
                            polyline.remove();
                        }
                        polyline.remove();
                        drop_location.setText("");
                        SessionManager.setActiveRideId(null);
                        mainHeaderLayout.setVisibility(View.VISIBLE);
                        header.setVisibility(View.VISIBLE);
                        footer.setVisibility(View.GONE);
                        mainFooter.setVisibility(View.GONE);
                        linear_cnfrmBooking.setVisibility(View.GONE);
                        linear_afterridecnfrm.setVisibility(View.GONE);
                        completeRideLayout.setVisibility(View.GONE);
                        startRideLayout.setVisibility(View.GONE);
                        cnfrmBooking.setVisibility(View.GONE);
                        driver_details_layout.setVisibility(View.GONE);
                        // timerTask.cancel();
                        check = false;
                        checkTwo = false;
                        checkStatus = false;
                        ride_id = 0;
                        replaceFragment();
                    } else {
                        Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    loading.cancel();
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.cancel();
                Log.e("ResponseDELETE ", "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<String, String>();
                try {
                    params.put("driver_id", SessionManager.getUserId());
                    params.put("ride_id", rideId);
                    //  params.put("status", "DELETED");
                    params.put("status", "CANCELLED");

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth = "Bearer " + getKEY();
                headers.put("Authorization", auth);
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    private void startLocationService() {
        if (getActivity() != null) {
            Intent intent = new Intent(getApplicationContext(), TrackingActivity.class);
            intent.putExtra("user_name", userName);
            intent.putExtra("ride_id", String.valueOf(ride_id));
            intent.putExtra("user_email", userEmail);
            startActivity(intent);
        }
    }

    //media recorder
    public static void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mediaRecorder.setOutputFile(AudioSavePathInDevice);
        }
        // mediaRecorder.setOutputFile(getFilename());
    }

    //uploading recoding
    private static void uplaodRecordingToServer() {
        String token = "Bearer " + getKEY();
        Log.d("token", token);
//        final ProgressDialog loading = ProgressDialog.show(getApplicationContext(), "Please wait...", "Uploading data...", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();
        RequestBody request_ride_id = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(ride_id));


        MultipartBody.Part fileToUpload;
        //empty file
        RequestBody empty_file = RequestBody.create(MediaType.parse("audio/*"), "");

        if (AudioSavePathInDevice != null) {
            fileToUpload = MultipartBody.Part.createFormData("audio", AudioSavePathInDevice.getName(), RequestBody.create(MediaType.parse("audio/*"), AudioSavePathInDevice));
        } else {
            fileToUpload = MultipartBody.Part.createFormData("audio", "", empty_file);
        }

        Call<ChangePasswordResponse> call = apiService.uploadRecording(token, fileToUpload, request_ride_id);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse requestResponse = response.body();
                Toast.makeText(context, requestResponse.getMessage(), Toast.LENGTH_LONG).show();
                //loading.cancel();
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                // progressBar.setVisibility(View.GONE);
                //  loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });

    }

    //start recording
    public static void startRecording() {
        random = new Random();
        if (checkPermission()) {
            try {
                AudioSavePathInDevice = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/Camera/" + CreateRandomAudioFileName(5) + "AudioRecording.ogg");
                if (!AudioSavePathInDevice.getParentFile().exists())
                    AudioSavePathInDevice.getParentFile().mkdirs();
                if (!AudioSavePathInDevice.exists()) {

                    AudioSavePathInDevice.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            MediaRecorderReady();

            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

            Log.d("path", String.valueOf(AudioSavePathInDevice));
        } else {
            requestPermission();
        }
    }

    //request permission
    private static void requestPermission() {

        ActivityCompat.requestPermissions((Activity) context, new String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    //stop recording
    public static void stopRecording() {
        try {
            mediaRecorder.stop();
        } catch (Exception exception) {
        }
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new androidx.appcompat.app.AlertDialog.Builder(context).setIcon(drawable).setTitle("Save Recording").setMessage("Are you sure you want to save recording?").setNegativeButton("Cancel", null).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();

                uplaodRecordingToServer();
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        }).show();

        Toast.makeText(getApplicationContext(), "Recording Completed", Toast.LENGTH_LONG).show();
        /*try {
            mediaRecorder.stop();
        } catch (Exception exception) {
        }
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_warning_white_24dp);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.RED);
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setIcon(drawable)
                .setTitle("Save Recording")
                .setMessage("Are you sure you want to save recording?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        uplaodRecordingToServer();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();

        Toast.makeText(getApplicationContext(), "Recording Completed",
                Toast.LENGTH_LONG).show();*/
    }

    //checking permissions
    public static boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    //create audio file name
    public static String CreateRandomAudioFileName(int string) {
        StringBuilder stringBuilder = new StringBuilder(string);
        int i = 0;
        while (i < string) {
            stringBuilder.append(RandomAudioFileName.charAt(random.nextInt(RandomAudioFileName.length())));

            i++;
        }
        return stringBuilder.toString();
    }

    //getting cancellation
    public void getCancellationCount(String rideId) {
        auth_token = "Bearer " + getKEY();
        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<CancelRideCount> call = apiService.getCancelRideCount(auth_token, rideId);
        call.enqueue(new Callback<CancelRideCount>() {
            @Override
            public void onResponse(Call<CancelRideCount> call, retrofit2.Response<CancelRideCount> response) {
                CancelRideCount jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    if (jsonResponse.getCountRide().equalsIgnoreCase("3")) {
                        cancelRequestAuto(rideId);
                    }
                }
            }

            @Override
            public void onFailure(Call<CancelRideCount> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }

    //auto cancel request
    private void cancelRequestAuto(String rideId) {

        String url = Server.BASE_URL.concat("accept_ride");

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.getCache().clear();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data = new JSONObject(response);
                    Log.e("Response", "onResponse = \n " + response);

                    SessionManager.setActiveRideId(null);
                    if (data.has("status") && data.getBoolean("status")) {
                        Toast.makeText(getActivity(), data.getString("message"), Toast.LENGTH_SHORT).show();
                        unSubscribeToUpdates(driverNames, String.valueOf(ride_id));
                        myMap.clear();
                        if (marker != null) {
                            marker.remove();
                        }

                        if (marker2 != null) {
                            marker2.remove();
                        }
                        if (mMarkers != null) {
                            mMarkers.clear();
                        }
                        if (mCurrLocationMarker != null) {
                            mCurrLocationMarker.remove();
                        }
                        if (polyline != null) {
                            polyline.remove();
                        }
                        progressBar.setVisibility(View.GONE);
                        handler.removeCallbacks(runnable);
                        mainHeaderLayout.setVisibility(View.VISIBLE);
                        header.setVisibility(View.VISIBLE);
                        footer.setVisibility(View.VISIBLE);
                        linear_cnfrmBooking.setVisibility(View.VISIBLE);
                        linear_afterridecnfrm.setVisibility(View.GONE);
                        completeRideLayout.setVisibility(View.GONE);
                        startRideLayout.setVisibility(View.GONE);
                        cnfrmBooking.setVisibility(View.VISIBLE);
                        cnfrmBooking.setText("No rider found in your area. Please try again after sometime.");
                        cnfrmBooking.setEnabled(false);
                        // timerTask.cancel();
                        check = false;
                        checkTwo = false;
                        checkStatus = false;
                        ride_id = 0;
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
                    params.put("ride_id", rideId);
                    params.put("status", "CANCELLED");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                String auth = "Bearer " + getKEY();
                headers.put("Authorization", auth);
                return headers;
            }
        };
        RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);
        requestQueue.add(stringRequest);
    }

    //getting ride status
    public void getRideStatus(String ride_id) {

        Log.e("TIMER", "1");
        RequestParams params = new RequestParams();
        params.put("ride_id", ride_id);
        Server.setHeader(getKEY());

        Server.post("get_ride_status", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                Log.e("TIMER", "2");
                // progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("TIMER", "3");

                try {
                    if (response.has("status") && response.getBoolean("status")) {

                        if (response.getJSONObject("data").getString("status").equalsIgnoreCase("PENDING") || response.getJSONObject("data").getString("status").equalsIgnoreCase("CANCELLED") || response.getJSONObject("data").getString("status").equalsIgnoreCase("FAILED") || response.getJSONObject("data").getString("status").equalsIgnoreCase("NOT_CONFIRMED")) {
                            if (!ride_id.equals("0")) {
                                Log.e("RIDEData", "Ride has been cancelled successfully.");
                                deleteRequest(String.valueOf(ride_id));
                                getrideStatus_details();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("TIMER", "4 " + e.getMessage());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("TIMER", "5");
                // Toast.makeText(getApplicationContext(), "try_again", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("TIMER", "6");
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.e("TIMER", "7");

            }
        });
    }


    //change on 24-12-2021
    //changing ride status
    private void changeRideStatus(String rideStatus, int rideId) {
        final ProgressDialog progressDialog1 = new ProgressDialog(requireContext());

        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            progressDialog1.setMessage("Updating ride status......");
            //progressDialog1.show();
            String url = Server.BASE_URL.concat("/api/driver/change_ride_status");
        /*String url = Server.BASE_URL.concat("rides?id=").concat(SessionManager.getUserId()).concat("&status=").concat(status)
                .concat("&utype=2");*/

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {

                    // Toast.makeText(getActivity(), response.toString(), Toast.LENGTH_SHORT).show();
                    progressDialog1.dismiss();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Response ", "" + error.getMessage());
                    progressDialog1.dismiss();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String auth = "Bearer " + getKEY();
                    headers.put("Authorization", auth);
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("ride_id", String.valueOf(rideId));
                        params.put("status", rideStatus);
                        return params;
                    } catch (Exception e) {
                        Log.e("failure", "Authentication failure.");
                    }
                    return super.getParams();
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog1.cancel();
        }
    }


    //timer stoped
    public void stopTimer() {
        j = 0;
        if (mCountDownTimerBackground != null) {
            mCountDownTimerBackground.cancel();
        }
    }


    //check ride status ends here

    //getting last ride
    public void getLastRide1() {
        final ProgressDialog progressDialog1 = new ProgressDialog(requireContext());

        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            progressDialog1.setMessage("Loading......");
            //  progressDialog1.show();
            String url = Server.BASE_URL.concat("/api/driver/get_last_ride");

            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.getCache().clear();
            Log.e("Who", "getlast ride is calling");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject data = new JSONObject(response);
                        Log.e("Who", "getlast ride is calling from response");
                        if (data != null) {
                            try {
                                boolean isDocExpired = data.getBoolean("identification_expiry");
                            } catch (Exception ex) {

                            }
                        }
                        if (data.has("status") && data.getBoolean("status")) {
                            JSONObject object = data.getJSONObject("data");
                            Log.e("get_last_ride_Response", object.toString());
                            Gson gson = new Gson();

                            lastRideData = gson.fromJson(object.toString(), LastRideData.class);
                            Log.e("RIDEData", lastRideData.getStatus());

                            progressDialog1.dismiss();
                            if (lastRideData != null) {


                                status = lastRideData.getStatus();
                                if (status.equalsIgnoreCase("failed")) {
                                    stopTimer();
                                    cancelTimer();
                                    SessionManager.setIsDriverArrived(false);
                                    getrideStatus_details();
                                } else if (status.equalsIgnoreCase("CANCELLED")) {
                                    stopTimer();
                                    cancelTimer();
                                    SessionManager.setIsDriverArrived(false);
                                    getrideStatus_details();
                                } else if (status.equalsIgnoreCase("NOT_CONFIRMED")) {
                                    pickup_location.setText(lastRideData.getPickupAdress());
                                    drop_location.setText(lastRideData.getDropLocatoin());
                                    cancelTimer();
                                    SessionManager.setIsDriverArrived(false);
                                    getrideStatus_details();
                                } else if (status.equalsIgnoreCase("PENDING")) {
                                    SessionManager.setIsDriverArrived(false);
                                    SessionManager.setActiveRideId(lastRideData.getRideId());

                                    cancelTimer();
                                    if (SessionManager.getActiveRideId() != null) {
                                        drop_location.setText(lastRideData.getDropAddress());
                                        pickup_location.setText(lastRideData.getPickupAdress());
                                        ride_id = Integer.parseInt(SessionManager.getActiveRideId());
                                        getrideStatus_details();

                                    }
                                } else if (status.equalsIgnoreCase("ACCEPTED")) {
                                    status = "ACCEPTED";
                                    cancelRideCharge = object.getString("cancellation_charge");
                                    Log.d("cancelRideCharge", cancelRideCharge);
                                    stopTimer();
                                    SessionManager.setActiveRideId(lastRideData.getRideId());
                                    if (SessionManager.getActiveRideId() != null) {


                                        if (object.getString("vehicle_image") != null) {
                                            carPicUrl = object.getString("vehicle_image");
                                            if (carPicUrl.trim().isEmpty() || carPicUrl.trim().equalsIgnoreCase(" ")) {
                                                Glide.with(requireActivity()).load(R.drawable.car_pic).into(car_pic);
                                            } else {
                                                Glide.with(requireActivity()).load(object.getString("vehicle_image")).into(car_pic);
                                            }
                                        }

                                        ride_id = Integer.parseInt(SessionManager.getActiveRideId());
                                        //  changeRideStatus(status,ride_id);
                                        drop_location.setText(lastRideData.getDropAddress());
                                        pickup_location.setText(lastRideData.getPickupAdress());
                                        setDisablePickUpAndDropLoc(false);
                                        getrideStatus_details();

                                        //acceptRideStatus();
                                    }
                                } else if (status.equalsIgnoreCase("START_RIDE")) {
                                    status = "START_RIDE";
                                    stopTimer();
                                    cancelTimer();
                                    SessionManager.setIsDriverArrived(false);
                                    SessionManager.setActiveRideId(lastRideData.getRideId());
                                    if (SessionManager.getActiveRideId() != null) {
                                        ride_id = Integer.parseInt(SessionManager.getActiveRideId());
                                        drop_location.setText(lastRideData.getDropAddress());
                                        pickup_location.setText(lastRideData.getPickupAdress());
                                        setDisablePickUpAndDropLoc(false);
                                        getrideStatus_details();
                                        //acceptRideStatus();

                                    }
                                } else if (status.equalsIgnoreCase("Completed") && (lastRideData.getIsTechnicalIssue().equalsIgnoreCase("Yes") || lastRideData.getIsTechnicalIssue().equalsIgnoreCase("No"))) {
//                                    if (lastRideData.getIsTechnicalIssue().equalsIgnoreCase("Yes")) {
////                                        if (ride_id != 0)
//                                        endRide();
//                                    }
                                    if (lastRideData.getIsTechnicalIssue().equalsIgnoreCase("No")) {
//                                        if (ride_id != 0 && !securityPopupShown)
                                        if (!lastRideData.getPaymentStatus().equalsIgnoreCase("Completed")) {
                                            ride_id = Integer.parseInt(lastRideData.getRideId());
                                            if (!securityPopupShown) showSecurityAlert();
                                        }
                                    }

                                    //old code
//                                    cancelTimer();
//                                    if (SessionManager.getActiveRideId() != null) {
//                                        ride_id = Integer.parseInt(SessionManager.getActiveRideId());
//                                        getrideStatus_details();
//                                    }

                                    //start new code
                                    if (!lastRideData.getPaymentStatus().equalsIgnoreCase("Completed") && lastRideData.getIsTechnicalIssue().equalsIgnoreCase("Yes")) {
                                        stopTimer();
                                        cancelTimer();
                                        SessionManager.setActiveRideId(lastRideData.getRideId());
                                        if (SessionManager.getActiveRideId() != null) {
                                            ride_id = Integer.parseInt(SessionManager.getActiveRideId());
                                            getrideStatus_details();
                                            //acceptRideStatus();

                                        }
                                    }
                                    //end new code

                                } else if (status.equalsIgnoreCase("Completed")) {
                                    if (!lastRideData.getPaymentStatus().equalsIgnoreCase("Completed")) {
                                        stopTimer();
                                        cancelTimer();
                                        SessionManager.setActiveRideId(lastRideData.getRideId());
                                        if (SessionManager.getActiveRideId() != null) {
                                            ride_id = Integer.parseInt(SessionManager.getActiveRideId());
                                            getrideStatus_details();
                                            //acceptRideStatus();

                                        }
                                    } else {
                                        if (lastRideData.getFeedback().equalsIgnoreCase("0")) {
                                            getrideStatus_details();
                                        }

                                    }

                                }
                                Log.e("OnResumeStatus", status);

                            } else {
                                progressDialog1.dismiss();
                            }
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
//                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                        Log.e("Who", "getlast ride is calling from exception");
                        progressDialog1.dismiss();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Response ", "" + error.getMessage());
                    Log.e("Who", "getlast ride is calling from error");
                    progressDialog1.dismiss();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> headers = new HashMap<String, String>();
                    String auth = "Bearer " + getKEY();
                    headers.put("Authorization", auth);
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    try {
                        Map<String, String> params = new HashMap<String, String>();
                        return params;
                    } catch (Exception e) {
                        Log.e("failure", "Authentication failure.");
                    }
                    return super.getParams();
                }
            };
            RetryPolicy policy = new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(policy);
            requestQueue.add(stringRequest);
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog1.cancel();
        }
    }

    private Dialog securityAlert = null;
    private Dialog videoAlert = null;
    private Dialog driverArrivedAlert = null;

    //security dialog
    private void showSecurityAlert() {
        securityPopupShown = true;
        securityAlert = new Dialog(requireActivity());
        securityAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        securityAlert.setContentView(R.layout.security_alert_layout);
        securityAlert.setCancelable(false);
        WindowManager.LayoutParams params = securityAlert.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        securityAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        footer.setVisibility(View.GONE);
        Button btnCancel = securityAlert.findViewById(R.id.btn_cancel_tech);
        Button btnSubmit = securityAlert.findViewById(R.id.btn_yes_tech);
        LinearLayout callingLayout = securityAlert.findViewById(R.id.calling_layout_tech);
        LinearLayout buttonLayout = securityAlert.findViewById(R.id.btn_layout);
        callingLayout.setVisibility(View.GONE);

        btnSubmit.setOnClickListener(e -> {
            securityAlert.dismiss();

            showPaymentConfirmationAlert(securityAlert);
            // endRide();
        });


        btnCancel.setOnClickListener(e -> {
            endRide();
            buttonLayout.setVisibility(View.GONE);
            callingLayout.setVisibility(View.VISIBLE);
        });

        callingLayout.setOnClickListener(e -> {
            securityAlert.dismiss();
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + getString(R.string.getduma_call_center)));
            requireActivity().startActivity(intent);
        });

        securityAlert.show();

    }

    //payment dialog
    private void showPaymentConfirmationAlert(Dialog securityAlertDialog) {

        final Dialog alertDialog = new Dialog(requireActivity());
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.security_alert_layout);
        alertDialog.setCancelable(false);
        WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button btnCancel = alertDialog.findViewById(R.id.btn_cancel_tech);
        Button btnSubmit = alertDialog.findViewById(R.id.btn_yes_tech);
        TextView textView = alertDialog.findViewById(R.id.txt_security_msg);
        textView.setText(getString(R.string.payment_confirmation));
        LinearLayout callingLayout = alertDialog.findViewById(R.id.calling_layout_tech);
        LinearLayout buttonLayout = alertDialog.findViewById(R.id.btn_layout);
        callingLayout.setVisibility(View.GONE);

        btnCancel.setOnClickListener(e -> {
            alertDialog.dismiss();
            endRide();
        });

        btnSubmit.setOnClickListener(e -> {
            if (securityAlertDialog != null) securityAlertDialog.dismiss();
            if (lastRideData != null) {
                PendingPojo pendingRequestPojo = new PendingPojo();
                pendingRequestPojo.setAmount(lastRideData.getAmount());
                pendingRequestPojo.setRideId(lastRideData.getRideId());
                pendingRequestPojo.setPaymentStatus(paymentStatus);
                AutoPayment autoPayment = new AutoPayment(context, pendingRequestPojo, HomeFragment.getInstance(), alertDialog);
            }

        });
        alertDialog.show();

    }

    //countdown timer
    public static CountDownTimer mCountDownTimerBackground = null;
    private int j = 0;
    private static int millisInFuture = 120000, countDownInterval = 1000;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void cancelRideIfNoDriver() {
        stopTimer();
        acceptRideProgressbar.setProgress(j, true);

        mCountDownTimerBackground = new CountDownTimer(millisInFuture, countDownInterval) {
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e("Log_tag", "Tick of Progress: " + j + " " + millisUntilFinished);
                j++;
                acceptRideProgressbar.setProgress((int) j * 100 / (millisInFuture / countDownInterval));
            }

            @Override
            public void onFinish() {
                j++;
                acceptRideProgressbar.setProgress(100);
                Log.e("REJECTSTATUS", "reject ride accepted " + status);
                replaceFragment();
                if (ride_id != 0) {
                    setDisablePickUpAndDropLoc(true);
                    getRideStatus(String.valueOf(ride_id));
                    j = 0;
                    if (mCountDownTimerBackground != null) {
                        mCountDownTimerBackground.cancel();
                    }
                }
//                if (ride_id != 0 && (status.equalsIgnoreCase("NOT_CONFIRMED") || status.equalsIgnoreCase("pending") || status.equalsIgnoreCase("cancelled") || status.equalsIgnoreCase("failed"))) {
//                    setDisablePickUpAndDropLoc(true);
//                    Log.e("Log_tag", "Tick of Progress is finished. ");
//                    getRideStatus(String.valueOf(ride_id));
//                    j = 0;
//                    if (mCountDownTimerBackground != null) {
//                        mCountDownTimerBackground.cancel();
//                    }
//                }
            }
        };
        mCountDownTimerBackground.start();
    }

    //add on 14-09-2022
    //getting card list
    private void getCardLists() {
        if (CheckConnection.haveNetworkConnection(requireContext())) {
            final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Checking Card Details.", false, false);
            RequestParams params = new RequestParams();
            Server.setContetntType();
            Server.setHeader(getKEY());
            Server.get("card_list", params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    // Toast.makeText(requireContext(), "Card List ....", Toast.LENGTH_SHORT).show();
                    loading.cancel();
                    Gson gson = new GsonBuilder().create();
                    try {
                        List<CardDetail> list = gson.fromJson(response.getJSONArray("data").toString(), new TypeToken<List<CardDetail>>() {
                        }.getType());
                        if (response.getJSONArray("data").length() != 0) {
                            cardDetails = list;
                            if (holdAmount < rate) {
                                Toast.makeText(getActivity(), "Your Pre-authorized amount is less than your ride Amount. Please contact to admin at info@ridesharerates.com", Toast.LENGTH_SHORT).show();
                            } else {
                                showPreAuthorizationAlert();
                            }
//                            addPreAuthorizationAmount();
//                            confirm_ride_details(currentLatitude, currentLongitude, destlat, destlng);
                        } else {
                            Log.e("Card_LIST", "Empty");
                            Toast.makeText(requireContext(), "Please add card before start ride.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        loading.cancel();
                        Toast.makeText(requireContext(), "Please add card before start ride.", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                        Log.e("Card_LIST", "Json Error");
                    }
                    Log.e("Card_List", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    loading.cancel();
                    Log.e("Payment_Response", responseString);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    if (getActivity() != null) {
                        loading.cancel();
                    }
                }
            });


        } else {
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //
    public void updateDriverMarkerTimer(double originLat, double originLong, double distLat, double distLong) {
        cTimer = new CountDownTimer(12500000, 300000) {
            public void onTick(long millisUntilFinished) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        instance.requireActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                updateDriverMarker_getRideStatus(originLat, originLong, distLat, distLong, 0);
                            }
                        });
                    }
                }).start();
            }

            public void onFinish() {
                cancelTimer();
            }
        };
        cTimer.start();

    }

    //cancel timer
    void cancelTimer() {
        if (cTimer != null) {
            cTimer.cancel();
            cTimer = null;
        }
    }

    //updateDriverMarker
    public void updateDriverMarker_getRideStatus(double originLat, double originLong, double distLat, double distLong, float bearing) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 300 * 1000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            apiIsRunning = true;
            String base_url = "https://maps.googleapis.com/";
            String origin = originLat + "," + originLong;
            String dest = distLat + "," + distLong;
            String key = getString(R.string.google_android_map_api_key);
            Retrofit retrofit = new Retrofit.Builder().baseUrl(base_url).addConverterFactory(GsonConverterFactory.create()).build();
            ApiNetworkCall service = retrofit.create(ApiNetworkCall.class);
            service.getDistanceFromDistanceMatrix(origin, dest, key).enqueue(new Callback<DistanceMatrixResponse>() {
                @Override
                public void onResponse(Call<DistanceMatrixResponse> call, retrofit2.Response<DistanceMatrixResponse> response) {
                    Log.e("   ", response.toString());
                    DistanceMatrixResponse matrixResponse = response.body();

                    try {
                        if (status.equalsIgnoreCase("accepted")) {
                            double distanceInMeters = Double.parseDouble(matrixResponse.getRows().get(0).getElements().get(0).getDistance().getValue().toString());
                            double distanceInMiles1 = distanceInMeters * 0.00062137;
                            distanceInMile = distanceInMeters * 0.00062137;
                            distance.setText(String.format("%.1f miles", distanceInMile));
                            time.setText(matrixResponse.getRows().get(0).getElements().get(0).getDuration().getText());
                            // int Dist = Integer.parseInt(matrixResponse.getRows().get(0).getElements().get(0).getDistance().getText());
                            //int Time = Integer.parseInt(matrixResponse.getRows().get(0).getElements().get(0).getDuration().getText());
                            // int velocity = Dist%Time;
                            //speed.setText(String.valueOf(velocity));

                            setDriverToUserTrackingPolyline(driverLat, driverLong, bearing);
                            Log.d("distance", matrixResponse.getRows().get(0).getElements().get(0).getDistance().getValue().toString());
                            Log.d("duration", matrixResponse.getRows().get(0).getElements().get(0).getDuration().getValue().toString());
//                            int dis = Integer.parseInt(matrixResponse.getRows().get(0).getElements().get(0).getDistance().getText().toString());
//                            int time = Integer.parseInt(matrixResponse.getRows().get(0).getElements().get(0).getDuration().getText().toString());
//                            int sp = dis/time;
//
//                            if(sp<=1 || String.valueOf(sp).isEmpty()){
//                                speed.setText("0");
//                            }else{
//                                speed.setText(sp);
//                            }

                            //here we replaced getduration to getdistance
                            if (Integer.parseInt(matrixResponse.getRows().get(0).getElements().get(0).getDistance().getValue().toString()) <= 200) {
                                if (!SessionManager.getIsDriverArrived()) {
                                    if (driverArrivedAlert == null) {
                                        showDriverArrivedAlert();
                                        driver_details_layout.setVisibility(View.GONE);
                                        Log.d("DriverReached1", "DriverReached");
                                    } else {
                                        if (!driverArrivedAlert.isShowing()) {
                                            showDriverArrivedAlert();
                                            driver_details_layout.setVisibility(View.GONE);
                                            Log.d("DriverReached2", "DriverReached");

                                        } else {
                                            Log.d("DriverReached1", "DriverNotReached");

                                        }
                                    }
                                } else {
                                    Log.d("DriverReached2", "DriverNotReached");
                                }
                            }
                        } else if (status.equalsIgnoreCase("start_ride")) {
                            setDestinationTrackingPolyLine(bearing);
                        }
                    } catch (Exception ex) {
                        time.setText("__");
                        apiIsRunning = false;
                    }

                }

                @Override
                public void onFailure(Call<DistanceMatrixResponse> call, Throwable t) {
                    Log.e("GOOGLER", t.toString());
                    apiIsRunning = false;
                }
            });
        } else {
            Toast.makeText(requireActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            apiIsRunning = false;
        }
    }

    //driver arrived dialog
    private void showDriverArrivedAlert() {
        driverArrivedAlert = new Dialog(requireActivity());
        driverArrivedAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        driverArrivedAlert.setContentView(R.layout.security_alert_layout);
        driverArrivedAlert.setCancelable(false);
        WindowManager.LayoutParams params = driverArrivedAlert.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        driverArrivedAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button btnCancel = driverArrivedAlert.findViewById(R.id.btn_cancel_tech);
        btnCancel.setVisibility(View.GONE);
        Button btnSubmit = driverArrivedAlert.findViewById(R.id.btn_yes_tech);
        btnSubmit.setText("OK");
        LinearLayout callingLayout = driverArrivedAlert.findViewById(R.id.calling_layout_tech);
        ImageView warn_icon = driverArrivedAlert.findViewById(R.id.warn_icon);
        warn_icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.origin_icon, null));
        LinearLayout buttonLayout = driverArrivedAlert.findViewById(R.id.btn_layout);
        TextView txtMsg = driverArrivedAlert.findViewById(R.id.txt_security_msg);
        txtMsg.setText(getString(R.string.driver_arrived_msg));


        btnSubmit.setOnClickListener(e -> {
            driverArrivedAlert.dismiss();
            stopPlaying();
            SessionManager.setIsDriverArrived(true);
        });

        driverArrivedAlert.show();
        try {
            if (musicPlayer != null) {
                musicPlayer.pause();
                musicPlayer.stop();
                musicPlayer.reset();
                musicPlayer.release();
                musicPlayer = null;
            }
            musicPlayer = MediaPlayer.create(getApplicationContext(), R.raw.driver_chime_2);
            musicPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            musicPlayer.setLooping(false);
            if (!musicPlayer.isPlaying()) {
                musicPlayer.start();
                Log.e("MEDIA_START", "I am playing");
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private void stopPlaying() {
        try {
            Log.e("MUSIC", "Stopping...");
            if (musicPlayer != null) {
                musicPlayer.pause();
                musicPlayer.stop();
                musicPlayer.reset();
                musicPlayer.release();
                musicPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void callDriver(String contactNumber) {
        Map<String, String> param = new HashMap();
        param.put("Caller", contactNumber);

        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(requireContext())) {
            progressDialog.setMessage("Calling Driver.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<CallingDriverResponse> call = apiService.callingDriverApi("Bearer " + SessionManager.getKEY(), param);
            call.enqueue(new Callback<CallingDriverResponse>() {
                @Override
                public void onResponse(Call<CallingDriverResponse> call, retrofit2.Response<CallingDriverResponse> response) {
                    CallingDriverResponse jsonResponse = response.body();
                    if (jsonResponse != null) {
                        if (jsonResponse.getStatus() == 200) {
                            progressDialog.cancel();

                        } else {
                            progressDialog.cancel();
                            Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<CallingDriverResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    public static void getDistance(String origin, String destination) {

        if (VolleySingleton.getInstance(getApplicationContext()).isConnected()) {
            String url = Constants.getDistance + "origins=" + origin + "&destinations=" + destination + "&" + "key=AIzaSyAJuI_IDQB0lt10U0Obffdr0qFV1soIMh4";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Res", response);
                    Log.d("URL", url);

                    JSONObject jobjMain = null;
                    try {
                        jobjMain = new JSONObject(response);
                        String Status = jobjMain.getString("status");

                        if (Status.equals("OK")) {

                            JSONArray rows = jobjMain.getJSONArray("rows");
                            JSONObject jObjElement = rows.getJSONObject(0);

                            JSONArray elements = jObjElement.getJSONArray("elements");
                            JSONObject jObjDistance = elements.getJSONObject(0);

                            String distanceStatus = jObjDistance.getString("status");

                            if (distanceStatus.equals("OK")) {
                                JSONObject ObjDistance = jObjDistance.getJSONObject("distance");
                                distanceInMeters = String.valueOf(ObjDistance.getInt("value") / 1609.344);
                                distance.setText(distanceInMeters);
                                //time
                            }

                            Log.d("roes1", rows.toString());
                            Log.d("roes1", jObjElement.toString());
                            Log.d("roes1", elements.toString());
                            Log.d("roes1", jObjDistance.toString());
                            Log.d("roes1", distance + "");
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();

                    if (error instanceof TimeoutError || error instanceof NoConnectionError || error instanceof NetworkError) {
                        Toast.makeText(getApplicationContext(), context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show();
                    } else {
                    }
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
//                    params.put("destinations","jodhpur");
//                    params.put("origins", "6827+J9V Palasni");
//                    params.put("key", "AIzaSyAwnPm0pAcIiMcMIeNFRxPmg-6OL_t3YFI");
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    // headers.put("header2", "header2");

                    return headers;
                }

            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            /*stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constants.MY_TIMEOUT_MS,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));*/
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        } else {
            Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateLoginLogotApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
            Map<String, String> params = new HashMap();
            params.put("status", "1");

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<UpdateLoginLogoutResponse> call = apiService.updateLoginLogoutApi("Bearer " + SessionManager.getKEY(), params);
            call.enqueue(new Callback<UpdateLoginLogoutResponse>() {
                @Override
                public void onResponse(Call<UpdateLoginLogoutResponse> call, retrofit2.Response<UpdateLoginLogoutResponse> response) {
                    UpdateLoginLogoutResponse jsonResponse = response.body();
                    Log.d("UpdateLoginLogoutResponse", response.toString());
                    if (jsonResponse != null) {

                    }

                }

                @Override
                public void onFailure(Call<UpdateLoginLogoutResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    //   progressDialog.cancel();
                }
            });
        } else {
            //progressDialog.cancel();
            Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
            // progressDialog.dismiss();
        }
    }

    private void showVideoAlert() {
        try {
            homeVideoView = videoAlert.findViewById(R.id.homeVideoView);
            homeVideoView.setVideoURI(videoUri);
            homeVideoView.setMediaController(mc);
            homeVideoView.start();
            videoAlert.setCancelable(false);
            videoAlert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getNotify() {
        adapter.notifyDataSetChanged();
    }

    private void addPreAuthorizationAmount() {
        final ProgressDialog loading = ProgressDialog.show(context, "", "Checking Wallet Amount...", false, false);
        if (CheckConnection.haveNetworkConnection(context)) {
            RequestParams params = new RequestParams();
            String url = "add_payment";
            params.put("amount", holdAmount);
            params.put("card_id", getDefaultCard());

            Server.setContetntType();
            Server.setHeader(SessionManager.getKEY());
            Server.post(url, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    try {
                        loading.cancel();
                        if (response.getBoolean("status")) {
                            txnId = response.getString("txn_id");
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            confirm_ride_details(currentLatitude, currentLongitude, destlat, destlng);
                        } else {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        Log.e("PreAuthorization", response.toString());
                    } catch (JSONException e) {
                        loading.cancel();
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    loading.cancel();
                    Toast.makeText(context, responseString, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                }
            });
        } else {
            loading.cancel();
            Toast.makeText(context, R.string.network, Toast.LENGTH_LONG).show();
        }
    }

    private String getDefaultCard() {
        String card_id;
        if (cardDetails != null) {
            if (cardDetails.size() > 0) {
                for (int i = 0; i < cardDetails.size(); i++) {
                    if (cardDetails.get(i).getIsDefault().equals("1")) {
                        card_id = cardDetails.get(i).getId();
                        return card_id;
                    }
                }
            }
        }
        return null;

    }

    private void showPreAuthorizationAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK);
        builder.setTitle("Rideshare Authorization").setMessage("Rideshare authorized to hold $" + holdAmount + " for booking your ride.").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                addPreAuthorizationAmount();
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            public void onClick(final DialogInterface dialog, final int id) {
                dialog.cancel();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void runTimer() {
        timeRunnable = () -> {
            Log.d("canRunStatus", canRun + "" + status);
            if (!canRun) return;

            seconds++;

            if (timerLayout.getVisibility() == View.GONE) {
                timerLayout.setVisibility(View.VISIBLE);
            }

            Log.d("timerCountActivity", seconds + "");
            long minutes = TimeUnit.SECONDS.toMinutes(seconds);
            String mins = String.valueOf(minutes).length() == 2 ? minutes + "" : "0" + minutes;
            timerTime = mins + ":" + (String.valueOf(seconds - TimeUnit.MINUTES.toSeconds(minutes)).length() == 2 ? (seconds - TimeUnit.MINUTES.toSeconds(minutes)) : "0" + (seconds - TimeUnit.MINUTES.toSeconds(minutes)));
            ride_timer.setText(timerTime);

            Global.setTimerSecondsPassed(context, seconds);

            if (minutes < 5) {
                timeHandler.postDelayed(timeRunnable, 1000);
            } else {
                stopRideTimer();
            }

        };
    }

    private void startRideTimer() {
        timerLayout.setVisibility(View.VISIBLE);
        timeHandler.removeCallbacks(timeRunnable);
        timeHandler.postDelayed(timeRunnable, 1000);
        isRunning = true;
        Global.setIsRunningInBackground(context, false);
        Global.setWasTimerRunning(context, true);
    }

    private void stopRideTimer() {
        isRunning = false;
        timerLayout.setVisibility(View.GONE);
        timeHandler.removeCallbacks(timeRunnable);
        seconds = 0;
        timerTime = "00:00";
        ride_timer.setText(timerTime);

        Global.setTimerSecondsPassed(context, seconds);
        Global.setIsRunningInBackground(context, false);
        Global.setWasTimerRunning(context, false);
    }
}