package com.rideshare.app.acitivities;

import static com.rideshare.app.fragement.ProfileFragment.PICK_IMAGE;
import static com.rideshare.app.pojo.Global.checkRotationFromCamera;
import static com.rideshare.app.pojo.Global.getImageOrientation;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;

import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.Constants;
import com.rideshare.app.custom.LocationListener;
import com.rideshare.app.custom.PhoneNumberFormat;
import com.rideshare.app.custom.RealPathUtil;
import com.rideshare.app.custom.SSNFormattingTextWatcher;
import com.rideshare.app.custom.Utils;
import com.rideshare.app.pojo.CountryCode;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.CurrentCountry;
import com.rideshare.app.Server.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rideshare.app.pojo.SignupResponse;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by android on 17/08/23.
 */

//Register Activity
public class RegisterActivity extends ActivityManagePermission implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, com.tsongkha.spinnerdatepicker.DatePickerDialog.OnDateSetListener {

    private static final int IMAGE_RESULT = 200;
    InputStream inputStream;
    private static final String IMAGE_DIRECTORY = "/RideShareUser";

    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    EditText input_email, input_password, input_confirmPassword, input_mobile, input_name,input_lname, input_home, input_ssn;
    AppCompatButton sign_up;
    ImageView profile_img, identification_img, date_img;
    SwipeRefreshLayout swipeRefreshLayout;
    private java.io.File avatarFinalFile, verificationIdFinalFile;
    private String fileType = "", dateOfBirth = "", identityName = "", identityId = "";
    private LinearLayout ll_identification_layout;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    private Double currentLongitude;
    private Spinner identity_spinner;
    private View rootView;
    private Toolbar toolbar;
    private Place homePlace;
    String token, identityIssueDate = "", identityExpireDate = "";
    TextView txt_register, input_dob, identity_issue_date, identity_expire_date, identification_txt, txt_terms;
    ArrayList<String> identity_names = new ArrayList<>();
    ArrayList<String> identity_id = new ArrayList<>();
    Dialog Progressdialog;
    Activity activity;
    private List<CountryCode> countryCodeList;
    private TextView selectedCountryCodeTextView;
    com.hbb20.CountryCodePicker countryCodeSpinner;
    private ActivityResultLauncher<Intent> galleryLauncher;
    Uri avatarUri, VerificationIdUri;
    MultipartBody.Part avtarFileToupload, verificationIdFileToupload;
    private String verificationID_path = "", avatar_path = "";
    private CheckBox checkbox;
    private boolean checkValue;
    ArrayList<String> title_names = new ArrayList<>(Arrays.asList("Boss","Mr.","Ms."));
    Spinner name_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        BindView();
        applyfonts();
        AskPermission();
        activity = RegisterActivity.this;
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.black));

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    fileUri = data.getData();
                    String path = fileUri.getPath();
                    if (!path.isEmpty()) {
                        file = new File(path);

                        try {
//                            inputStream = getContentResolver().openInputStream(data.getData());
//                            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
//                            Bitmap bitmap = bmp;
//                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                            bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
//                            byte[] bitmapdata = bos.toByteArray();
//
//                            switch (fileType) {
//                                case "verification_id":
//                                    Glide.with(RegisterActivity.this).asBitmap().load(bmp).into(identification_img);
//                                    String id_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
//                                    verificationIdFinalFile = new File(id_path);
//                                    break;
//
//                                case "avatar":
//                                    Glide.with(RegisterActivity.this).asBitmap().load(bmp).into(profile_img);
//                                    String avatar_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
//                                    avatarFinalFile = new File(avatar_path);
//                                    break;
//                            }
                            switch (fileType) {
                                case "verification_id":
                                    VerificationIdUri = data.getData();
                                    Glide.with(RegisterActivity.this).load(VerificationIdUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(identification_img);
                                    verificationID_path = VerificationIdUri.getPath();
                                    break;
                                case "avatar":
                                    avatarUri = data.getData();
                                    Glide.with(RegisterActivity.this).load(avatarUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(profile_img);
                                    avatar_path = avatarUri.getPath();
                                    break;
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        //Firebase Token
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            private static final String TAG = "FirebaseMessaging";

            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }

                // Get new FCM registration token
                token = task.getResult();
                SessionManager.setGcmToken(token);
            }
        });


        countryCodeList = loadCountryCodeData();

//        CustomSpinnerAdapter adapter = new CustomSpinnerAdapter(this, countryCodeList);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        countryCodeSpinner.setAdapter(adapter);

        // Handle country code selection
//        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                CountryCode selectedCountryCode = (CountryCode) parent.getSelectedItem();
//                Log.d("selectedCode",selectedCountryCode.getPhone_code().toString());
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                // Do nothing
//            }
//        });


        //several clickable parts of multiple texts in a single textview
        SpannableString ss = new SpannableString("Already have an account? Sign In");
        final ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                Intent intent1 = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
        };

        ss.setSpan(span1, 25, 32, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_register.setText(ss);
        txt_register.setMovementMethod(LinkMovementMethod.getInstance());
        txt_register.setHighlightColor(Color.TRANSPARENT);

        Calendar cal = Calendar.getInstance();
        date_img.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder().context(RegisterActivity.this).callback(RegisterActivity.this).spinnerTheme(R.style.NumberPickerStyle).showTitle(true).showDaySpinner(true).defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).maxDate(cal.get(Calendar.YEAR) - 18, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).minDate(cal.get(Calendar.YEAR) - 83, 11, 31).build().show();
        });

        identity_issue_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder().context(RegisterActivity.this).callback(new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                    Calendar newCalendar = Calendar.getInstance();
                    newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                    identityIssueDate = format.format(newCalendar.getTime());
                    identity_issue_date.setText(identityIssueDate);
                }
            }).spinnerTheme(R.style.NumberPickerStyle).showTitle(true).showDaySpinner(true).defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).maxDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).minDate(1900, 0, 1).build().show();
        });

        identity_expire_date.setOnClickListener(e -> {
            new SpinnerDatePickerDialogBuilder().context(RegisterActivity.this).callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            identityExpireDate = format.format(newCalendar.getTime());
                            identity_expire_date.setText(identityExpireDate);
                        }
                    }).spinnerTheme(R.style.NumberPickerStyle).showTitle(true).showDaySpinner(true).defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).maxDate(cal.get(Calendar.YEAR) + 25, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
//                    .minDate(1900, 0, 1)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).build().show();
        });


        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {


                        String latitude = "";
                        String longitude = "";

                        latitude = String.valueOf(currentLatitude);
                        longitude = String.valueOf(currentLongitude);
                        String city = null, state = null, country = null;
                        String email = input_email.getText().toString().trim();
                        String mobile = input_mobile.getText().toString().trim();
                        String password = input_password.getText().toString().trim();
                        String name = input_name.getText().toString().trim();
                        String lname = input_lname.getText().toString().trim();
                        String homeAddress = input_home.getText().toString().trim();
                        //String ssn = input_ssn.getText().toString().trim();
                        try {
                            Geocoder geocoder;
                            List<Address> addresses = null;
                            geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            if (!latitude.equals("0.0") && !longitude.equals("0.0")) {
                                try {
                                    addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
                                    if (addresses != null && addresses.size() > 0) {
                                        String merged = "";
                                        city = addresses.get(0).getLocality();
                                        country = addresses.get(0).getCountryName();
                                        state = addresses.get(0).getAdminArea();
                                        if (city != null) {
                                            merged = city;
                                        } else {
                                            city = "null";
                                        }
                                        if (state != null) {
                                            merged = city + "," + state;

                                        } else {
                                            state = "null";
                                        }
                                        if (country != null) {
                                            merged = city + "," + state + "," + country;

                                        } else {
                                            country = "null";
                                        }
                                    }
                                } catch (IOException | IllegalArgumentException e) {
                                    //  e.printStackTrace();
                                    Log.e("data", e.toString());
                                }
                            } else {
                                latitude = "0.0";
                                longitude = "0.0";
                                city = "null";
                                state = "null";
                                country = "null";
                            }
                        } catch (Exception e) {
                            latitude = "0.0";
                            longitude = "0.0";
                            city = "null";
                            state = "null";
                            country = "null";
                        }

                        if (!Utils.isValidMobile(mobile)) {
                            Toast.makeText(RegisterActivity.this, "Please enter valid mobile number", Toast.LENGTH_SHORT).show();
                            return;
                        }

//                        else if (dateOfBirth.isEmpty()) {
//                            Toast.makeText(RegisterActivity.this, "Please select date of birth", Toast.LENGTH_SHORT).show();
//                            return;
//                        } else
                        if (avatar_path.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please upload Profile Pic", Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (identityId.equalsIgnoreCase("")) {
                            Toast.makeText(RegisterActivity.this, "Please Select Identity Document.", Toast.LENGTH_LONG).show();
                            return;
                        } else if (verificationID_path.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please Upload Identity Document", Toast.LENGTH_LONG).show();
                            return;
                        } else if (identityIssueDate.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please select identity issue Date", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (identityExpireDate.isEmpty()) {
                            Toast.makeText(RegisterActivity.this, "Please select identity expire Date", Toast.LENGTH_SHORT).show();
                            return;
                        }  else if (!checkValue) {
                            Toast.makeText(RegisterActivity.this, "Please select terms & conditions", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        register(email, mobile, password, name,lname, latitude, longitude, country, state, city, token, "1", dateOfBirth, homeAddress);
                    } else {
                        Toast.makeText(RegisterActivity.this, "network is not available", Toast.LENGTH_LONG).show();
                    }
                } else {
                    // do nothing
                }


            }
        });


        getIdentity_details();
    }

    private List<CountryCode> loadCountryCodeData() {
        List<CountryCode> countryCodeList = new ArrayList<>();

        try {
            // Get the input stream for the raw resource
            Resources resources = getResources();
            InputStream inputStream = resources.openRawResource(R.raw.country_codes);

            // Read the input stream line by line
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            // Close the reader and input stream
            reader.close();
            inputStream.close();

            // Convert the JSON string to a list of CountryCode objects
            String json = stringBuilder.toString();
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String name = jsonObject.getString("name");
                String code = jsonObject.getString("code");
                String phoneCode = jsonObject.getString("phone_code");

                CountryCode countryCode = new CountryCode();
                countryCode.setName(name);
                countryCode.setCode(code);
                countryCode.setPhone_code(phoneCode);
                countryCodeList.add(countryCode);
            }

        } catch (Resources.NotFoundException | IOException | JSONException e) {
            e.printStackTrace();
        }

        return countryCodeList;
    }
    // Rest of your code...

    //getting identity details
    private void getIdentity_details() {
        RequestParams params = new RequestParams();
        Server.get("get_documentidentity_get", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    identity_names.clear();
                    identity_id.clear();
                    if (response.has("status") && response.getBoolean("status")) {
                        identity_names.add("Select Identity");

                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            identity_names.add(jsonObject.getString("document_name"));
                            identity_id.add(jsonObject.getString("id"));
                        }

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter);

                        identity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                if (!identity_spinner.getItemAtPosition(position).equals("Select Identity")) {
                                    identityId = (identity_id.get(position - 1));
                                    identityName = adapterView.getItemAtPosition(position).toString();
                                    ll_identification_layout.setVisibility(View.VISIBLE);
                                    resetIdentity(identityName);
                                } else {
                                    identityId = "";
                                    identityName = "";
                                    ll_identification_layout.setVisibility(View.GONE);
                                    identification_txt.setText(getString(R.string.upload_identity_document));
                                }


                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                // DO Nothing here
                            }
                        });
                    } else {
                        identity_names.clear();
                        identity_id.clear();
                        identity_names.add("Select Identity");

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(RegisterActivity.this, getString(R.string.try_again), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //reset identity details
    private void resetIdentity(String identityName) {

        identification_txt.setText(String.format("Upload %s", identityName));
        identity_expire_date.setText("Expire Date");
        identity_issue_date.setText("Issue Date");
        verificationIdFinalFile = new File("");
        identification_img.setImageDrawable(getDrawable(R.drawable.ic_upload_pic));
        identityIssueDate = "";
        identityExpireDate = "";

    }


    @Override
    public void onDateSet(com.tsongkha.spinnerdatepicker.DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);

        dateOfBirth = format.format(newCalendar.getTime());
        input_dob.setText(dateOfBirth);

    }

    //checking SSN is valid
    public boolean isValidSSN(String ssn) {
        // Regex to check SSN
        // (Social Security Number).
        String regex = "^(?!666|000|9\\d{2})\\d{3}" + "-(?!00)\\d{2}-" + "(?!0{4})\\d{4}$";

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the string is empty
        // return false
        if (ssn == null) {
            return false;
        }

        // Pattern class contains matcher()
        //  method to find matching between
        //  given string and regular expression.
        Matcher m = p.matcher(ssn);

        // Return if the string
        // matched the ReGex
        return m.matches();
    }

    //validation
    public Boolean validate() {
        Boolean value = true;

        if (input_name.getText().toString().trim().equals("")) {
            input_name.setError(getString(R.string.fname_is_required));
            value = false;
        } else {
            input_name.setError(null);
        }

        if (input_lname.getText().toString().trim().equals("")) {
            input_lname.setError(getString(R.string.lname_is_required));
            value = false;
        } else {
            input_lname.setError(null);
        }
//        if (input_home.getText().toString().trim().equals("")) {
//            input_home.setError("Please Enter Home Address");
//            value = false;
//        } else {
//            input_home.setError(null);
//        }


        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(input_email.getText().toString().trim()).matches()) {
            input_email.setError(getString(R.string.email_invalid));
            value = false;
        } else {
            input_email.setError(null);
        }
        if (input_mobile.getText().toString().trim().equals("")) {
            input_mobile.setError(getString(R.string.mobile_invalid));
            value = false;
        } else {
            input_mobile.setError(null);
        }
        if (!(input_password.length() >= 6)) {
            value = false;
            input_password.setError(getString(R.string.password_length));
        } else {
            input_password.setError(null);
        }
        if (!input_password.getText().toString().trim().equals("") && (!input_confirmPassword.getText().toString().trim().equals(input_password.getText().toString().trim()))) {
            value = false;
            input_confirmPassword.setError(getString(R.string.password_nomatch));
        } else {
            input_confirmPassword.setError(null);
        }
        return value;
    }


    //backpressd
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    //binding view
    public void BindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Sign Up");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //identity
        ll_identification_layout = findViewById(R.id.ll_identification_layout);
        ll_identification_layout.setVisibility(View.GONE);
        identity_spinner = findViewById(R.id.identity_doc_spinner);
        identity_issue_date = findViewById(R.id.identification_issue_date);
        identity_expire_date = findViewById(R.id.identification_expire_date);
        identification_txt = findViewById(R.id.identification_txt);
        //identity
        countryCodeSpinner = findViewById(R.id.country_code);


        input_email = findViewById(R.id.input_email);
        input_password = findViewById(R.id.input_password);
        input_confirmPassword = findViewById(R.id.input_confirmPassword);
        input_name = findViewById(R.id.input_name);
        input_lname = findViewById(R.id.input_lname);
        input_dob = findViewById(R.id.input_dob);
        date_img = findViewById(R.id.date_img);
        input_home = findViewById(R.id.input_home_address);
        input_mobile = findViewById(R.id.input_mobile);
        input_mobile.addTextChangedListener(new PhoneNumberFormat(input_mobile));
        sign_up = findViewById(R.id.sign_up);
        input_ssn = findViewById(R.id.input_ssn);
        profile_img = findViewById(R.id.profile_img);
        identification_img = findViewById(R.id.identification_img);
        txt_register = findViewById(R.id.txt_register);
        txt_terms = findViewById(R.id.txt_terms);
        checkbox = findViewById(R.id.checkbox);
        name_title=findViewById(R.id.name_title);

        avatarFinalFile = new java.io.File("");
        verificationIdFinalFile = new java.io.File("");

        swipeRefreshLayout = findViewById(R.id.swipe_refresh);


        input_home.setOnClickListener(new LocationListener(this, this));

        input_ssn.addTextChangedListener(new SSNFormattingTextWatcher(input_ssn));

        identification_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "verification_id";
                showPictureDialog();
            }
        });

        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "avatar";
                showPictureDialog();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        txt_terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, TermsConditions.class);
                startActivity(intent);
                finish();
            }
        });

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                checkValue = isChecked;
            }
        });

        ArrayAdapter<String> titleNameAdapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.spinner_item, title_names);
        name_title.setAdapter(titleNameAdapter);

    }

    //appling font
    public void applyfonts() {
        TextView textView = findViewById(R.id.txt_register);
        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        textView.setTypeface(font);
        input_name.setTypeface(font1);
        input_lname.setTypeface(font1);
        input_ssn.setTypeface(font1);
        input_dob.setTypeface(font1);
        input_home.setTypeface(font1);
        input_email.setTypeface(font1);
        input_password.setTypeface(font1);
        input_confirmPassword.setTypeface(font1);
        input_mobile.setTypeface(font1);
        sign_up.setTypeface(font1);
        txt_register.setTypeface(font1);
        AskPermission();

    }

    //register API
    public void register(String email, String mobile, String password, String name,String lname, String latitude, String longitude, String country, String state, String city, String gcm_token, String utype, String dob, String home_address) {
//        RequestParams params = new RequestParams();
//        Map<String, String> params = new HashMap();
//        params.put("email", email);
//        params.put("mobile", mobile);
//        params.put("country_code", "+1");
//        params.put("password", password);
//        params.put("name", name);
//        params.put("latitude", latitude);
//        params.put("longitude", longitude);
//        params.put("country", country);
//        params.put("state", state);
//        params.put("city", city);
//        params.put("utype", utype);
//        params.put("gcm_token", gcm_token);


        Map<String, RequestBody> params = new HashMap();
        params.put("email", createRequestBody(email));
        params.put("mobile", createRequestBody(mobile));
        params.put("country_code", createRequestBody("+1"));
        params.put("password", createRequestBody(password));
        params.put("name_title", createRequestBody(name_title.getSelectedItem().toString()));
        params.put("name", createRequestBody(name));
        params.put("last_name", createRequestBody(lname));
        params.put("latitude", createRequestBody(latitude));
        params.put("longitude", createRequestBody(longitude));
        params.put("country", createRequestBody(country));
        params.put("state", createRequestBody(state));
        params.put("city", createRequestBody(city));
        params.put("utype", createRequestBody(utype));
        params.put("gcm_token", createRequestBody(gcm_token));
//        params.put("dob", createRequestBody(dob));
//        params.put("home_address", createRequestBody(home_address));
        params.put("identification_document_id", createRequestBody(identityId));
        params.put("identification_issue_date", createRequestBody(identityIssueDate));
        params.put("identification_expiry_date", createRequestBody(identityExpireDate));

        try {
            if (!avatar_path.isEmpty()) {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), avatarUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final int rotation = getImageOrientation(avatar_path);
                bitmap = checkRotationFromCamera(bitmap, rotation);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                avtarFileToupload = MultipartBody.Part.createFormData("avatar", "image.jpg", requestBody);
            }
            if (!verificationID_path.isEmpty()) {

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(RegisterActivity.this.getContentResolver(), VerificationIdUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final int rotation = getImageOrientation(verificationID_path);
                bitmap = checkRotationFromCamera(bitmap, rotation);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                verificationIdFileToupload = MultipartBody.Part.createFormData("verification_id", "image.jpg", requestBody);
            } else {
                avtarFileToupload = MultipartBody.Part.createFormData("avatar", avatarFinalFile.getName(), RequestBody.create(MediaType.parse("*/*"), avatarFinalFile));
                verificationIdFileToupload = MultipartBody.Part.createFormData("verification_id", verificationIdFinalFile.getName(), RequestBody.create(MediaType.parse("*/*"), verificationIdFinalFile));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        params.put("verification_id", verificationIdFileToupload);
//        params.put("avatar", avtarFileToupload);

        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Registering.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();

//            Call<SignupResponse> call = apiService.signin(params);
            Call<SignupResponse> call = apiService.signin(params, avtarFileToupload, verificationIdFileToupload);
            call.enqueue(new Callback<SignupResponse>() {
                @Override
                public void onResponse(Call<SignupResponse> call, retrofit2.Response<SignupResponse> response) {
                    SignupResponse jsonResponse = response.body();

                    if (jsonResponse != null) {
                        if (jsonResponse.isStatus()) {
                            progressDialog.cancel();
                            Toast.makeText(RegisterActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                            SessionManager.setKEY(jsonResponse.getToken());
                            Log.e("SIgnInResponse", response.toString());

                            if (jsonResponse.getData().isAdd_card()) {
//                                LoginUser.login(RegisterActivity.this, RegisterActivity.this, email, password, token);
                                SessionManager.setStatus(jsonResponse.isStatus());
                                SessionManager.setUserId(String.valueOf(jsonResponse.getData().getUser_id()));
                                SessionManager.setUserEmail(jsonResponse.getData().getEmail().toString());
                                SessionManager.setUserName(jsonResponse.getData().getUsername().toString());
                                SessionManager.setUserMobile(jsonResponse.getData().getMobile().toString());
//                                SessionManager.setUserCountryCode(jsonResponse.getData().getCountry_code().toString());
                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                finish();

                            } else {
                                SessionManager.setStatus(jsonResponse.isStatus());
                                SessionManager.setUserId(String.valueOf(jsonResponse.getData().getUser_id()));
                                SessionManager.setUserEmail(jsonResponse.getData().getEmail().toString());
                                SessionManager.setUserName(jsonResponse.getData().getUsername().toString());
                                SessionManager.setUserMobile(jsonResponse.getData().getMobile().toString());
//                                SessionManager.setUserCountryCode(jsonResponse.getData().getCountry_code().toString());
                                Intent intent = new Intent(RegisterActivity.this, AddPaymentMethodActivity.class);
                                intent.putExtra("Password", password);
                                startActivity(intent);
                                finish();

                            }
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(RegisterActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<SignupResponse> call, Throwable t) {
                    Log.d("Failed", t.getMessage());

                    progressDialog.cancel();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }

        Log.e("Country", CurrentCountry.getCountryName(RegisterActivity.this, longitude, latitude));

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                //If everything went fine lets get latitude and longitude
                currentLatitude = location.getLatitude();
                currentLongitude = location.getLongitude();

                //Toast.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {

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
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
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
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }

    //getting current location
    public void getCurrentlOcation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API).build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);
    }

    //turn on GPS
    public void tunonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
            mGoogleApiClient.connect();
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
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
                            getCurrentlOcation();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and setting the result in onActivityResult().
                                status.startResolutionForResult(RegisterActivity.this, 1000);
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    File file;
    Uri fileUri;

    //takig picture from camera
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(getExternalCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        fileUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, IMAGE_RESULT);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {

//            switch (fileType) {
//                case "verification_id":
//                    Glide.with(this).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(identification_img);
//                    file = compressImage(fileUri.getPath());
//                    String insurance_path = file.getPath();
//                    verificationIdFinalFile = new File(insurance_path);
//                    break;
//                case "avatar":
//                    Glide.with(this).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(profile_img);
//                    file = compressImage(fileUri.getPath());
//                    String permit_path = file.getPath();
//                    avatarFinalFile = new File(permit_path);
//                    break;
//
//            }
            switch (fileType) {
                case "verification_id":
                    VerificationIdUri = fileUri;
                    Glide.with(this).load(VerificationIdUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(identification_img);
                    verificationID_path = VerificationIdUri.getPath();
                    break;
                case "avatar":
                    avatarUri = fileUri;
                    Glide.with(this).load(avatarUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(profile_img);
                    avatar_path = avatarUri.getPath();
                    break;
            }

        }

        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }


            try {
                inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
                Bitmap bitmap = bmp;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

                switch (fileType) {
                    case "verification_id":
                        Glide.with(this).asBitmap().load(bmp).into(identification_img);
                        String id_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        verificationIdFinalFile = new File(id_path);
                        break;

                    case "avatar":
                        Glide.with(this).asBitmap().load(bmp).into(profile_img);
                        String avatar_path = compressImage(RealPathUtil.getRealPath(getApplicationContext(), data.getData())).getPath();
                        avatarFinalFile = new File(avatar_path);
                        break;

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {

                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                homePlace = Autocomplete.getPlaceFromIntent(data);
                input_home.setText(homePlace.getAddress());

            }
        }

    }

    //showing  picture dialog
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.setType("image/*");
                        i.addCategory(Intent.CATEGORY_OPENABLE);
                        galleryLauncher.launch(i);
                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }

        });
        pictureDialog.show();
    }

    //permission check
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissionAsk) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    //asking permission
    public void AskPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getCurrentlOcation();
            return;
        }
        askCompactPermissions(permissionAsk, new PermissionResult() {
            @Override
            public void permissionGranted() {
                if (!GPSEnable()) {
                    tunonGps();
                } else {
                    getCurrentlOcation();
                }
            }

            @Override
            public void permissionDenied() {

            }

            @Override
            public void permissionForeverDenied() {
                //  openSettingsApp(getApplicationContext());
            }
        });
    }

    //compressing image
//    public File compressImage(String imageUrl) {
//        int compressionRatio = 2; //1 == originalImage, 2 = 50% compression, 4=25% compress
//        File file = new File(imageUrl);
//        try {
//            BitmapFactory.Options Options = new BitmapFactory.Options();
//            Options.inSampleSize = 4;
//            Options.inJustDecodeBounds = false;
//           // Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), Options);
//            Bitmap bitmap = CameraRotation.handleSamplingAndRotationBitmap(this,Uri.parse(imageUrl));
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, new FileOutputStream(file));
//            return file;
//        } catch (Throwable t) {
//            Log.e("ERROR", "Error compressing file." + t.toString());
//            t.printStackTrace();
//            return file;
//        }
//    }

    public File compressImage(String imageUrl) {
        try {
            File file = new File(imageUrl);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            // Decode the image bounds without loading the full image into memory
            BitmapFactory.decodeFile(file.getPath(), options);

            // Calculate the inSampleSize value to scale the image
            options.inSampleSize = calculateInSampleSize(options, 800, 600);

            // Decode the image with the calculated inSampleSize value
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);

            // Compress the bitmap to reduce file size
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int quality = 90; // Initial compression quality
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            while (outputStream.toByteArray().length > 1024 * 1024) {
                outputStream.reset();
                quality -= 10; // Decrease compression quality by 10%
                bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            }

            // Write the compressed bitmap to a file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(outputStream.toByteArray());
            fileOutputStream.flush();
            fileOutputStream.close();

            // Recycle the bitmap to free up memory
            bitmap.recycle();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return new File(imageUrl); // Return the original file if any exception occurs
        }
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    //Gps Enabled
    public Boolean GPSEnable() {
        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;
        }
        return false;
    }

    public void showDialog() {
        Progressdialog = new Dialog(activity);
        Progressdialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Progressdialog.setCancelable(false);
        Progressdialog.setContentView(R.layout.loading_dialog);
        Progressdialog.show();
    }

    public RequestBody createRequestBody(String data) {
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody requestBody = RequestBody.create(mediaType, data);
        return requestBody;
    }

}
