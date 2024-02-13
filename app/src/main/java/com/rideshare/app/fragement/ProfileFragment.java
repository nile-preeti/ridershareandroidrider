package com.rideshare.app.fragement;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rideshare.app.acitivities.RegisterActivity;
import com.rideshare.app.custom.CameraRotation;
import com.rideshare.app.custom.PhoneNumberFormat;
import com.rideshare.app.custom.RealPathUtil;
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
import com.google.gson.Gson;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.Utils;
import com.rideshare.app.pojo.User;
import com.rideshare.app.pojo.changepassword.ChangePasswordResponse;
import com.rideshare.app.pojo.getprofile.GetProfile;
import com.rideshare.app.pojo.profileresponse.ProfileResponse;
import com.rideshare.app.Server.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.FragmentManagePermission;
import com.thebrownarrow.permissionhelper.PermissionResult;
import com.thebrownarrow.permissionhelper.PermissionUtils;
import com.tsongkha.spinnerdatepicker.DatePicker;
import com.tsongkha.spinnerdatepicker.DatePickerDialog;
import com.tsongkha.spinnerdatepicker.SpinnerDatePickerDialogBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import gun0912.tedbottompicker.TedBottomPicker;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LOCATION_SERVICE;
import static com.rideshare.app.Server.session.SessionManager.getUserEmail;
import static com.rideshare.app.Server.session.SessionManager.getUserMobile;
import static com.rideshare.app.Server.session.SessionManager.getUserName;
import static com.rideshare.app.Server.session.SessionManager.setUserMobile;
import static com.rideshare.app.Server.session.SessionManager.setUserName;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;
import static com.rideshare.app.pojo.Global.checkRotationFromCamera;
import static com.rideshare.app.pojo.Global.getImageOrientation;

/**
 * Created by android on 14/3/17.
 */

//user profile
public class ProfileFragment extends FragmentManagePermission implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {
    View view;
    public File imageFile;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    String permissionAsk[] = {PermissionUtils.Manifest_CAMERA, PermissionUtils.Manifest_WRITE_EXTERNAL_STORAGE, PermissionUtils.Manifest_READ_EXTERNAL_STORAGE, PermissionUtils.Manifest_ACCESS_FINE_LOCATION, PermissionUtils.Manifest_ACCESS_COARSE_LOCATION};
    ProfileUpdateListener profileUpdateListener;
    private LinearLayout ll_identification_layout;
    private Spinner identity_spinner;
    UpdateListener listener;
    EditText input_vehicle, input_name,input_lname, input_password, input_mobile;
    TextView input_email, identity_issue_date, identity_expire_date, identification_txt;
    Button btn_update, btn_change;
    ImageView profile_pic, identification_img;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Double currentLatitude;
    ProgressBar progressBar, imgProgressBar;
    private Double currentLongitude;
    SwipeRefreshLayout swipeRefreshLayout;
    String image, identityId = "", identityIssueDate = "", identityName = "", identityExpireDate = "";
    InputStream inputStream;
    ArrayList<String> identity_names = new ArrayList<>();
    ArrayList<String> identity_id = new ArrayList<>();
    private boolean itemSelected = false;
    // File filetoupload;
    private static final String IMAGE_DIRECTORY = "/RideShareRider"; //  GetdumaRider

    public static final int PICK_IMAGE = 1;
    private File finalFile, verificationIdFinalFile;
    String[] permissionsFile = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,};
    private final static int IMAGE_RESULT = 200;
    private String fileType = "";
    private String savedIdentityId = "";
    private ActivityResultLauncher<Intent> galleryLauncher;
    private Uri avatarUri, VerificationIdUri;
    private String avatar_path = "", verificationID_path = "";
    MultipartBody.Part avtarFileToupload, verificationIdFileToupload;
    ArrayList<String> title_names = new ArrayList<>(Arrays.asList("Boss","Mr.","Ms."));
    Spinner name_title;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.setting, container, false);
        setHasOptionsMenu(true);
        bindView();
        Calendar cal = Calendar.getInstance();

        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null) {
                        fileUri = data.getData();
                        String path = fileUri.getPath();
                        if (!path.isEmpty()) {
                            file = new File(path);
                            try {
                                switch (fileType) {
                                    case "identity_pic":
                                        VerificationIdUri = data.getData();
                                        Glide.with(requireContext()).load(VerificationIdUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(identification_img);
                                        verificationID_path = VerificationIdUri.getPath();
                                        break;
                                    case "profile_pic":
                                        avatarUri = data.getData();
                                        Glide.with(requireContext()).load(avatarUri).apply(new RequestOptions().error(R.drawable.ic_upload_pic)).into(profile_pic);
                                        avatar_path = avatarUri.getPath();
                                        break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        switch (fileType) {
                            case "profile_pic":
                                avatarUri = fileUri;
                                Glide.with(requireContext()).load(avatarUri).into(profile_pic);
                                avatar_path = avatarUri.getPath();
                                break;
                            case "identity_pic":
                                VerificationIdUri = fileUri;
                                Glide.with(requireContext()).load(VerificationIdUri).into(identification_img);
                                verificationID_path = VerificationIdUri.getPath();
                                break;
                        }
                    }
                }
            }
        });

        identity_issue_date.setOnClickListener(e -> {
            identityIssueDate = "";
            new SpinnerDatePickerDialogBuilder().context(requireContext()).callback(new DatePickerDialog.OnDateSetListener() {
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
            identityExpireDate = "";
            new SpinnerDatePickerDialogBuilder().context(requireContext()).callback(new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                            Calendar newCalendar = Calendar.getInstance();
                            newCalendar.set(year, monthOfYear, dayOfMonth, 0, 0);
                            identityExpireDate = format.format(newCalendar.getTime());
                            identity_expire_date.setText(identityExpireDate);
                        }
                    }).spinnerTheme(R.style.NumberPickerStyle).showTitle(true).showDaySpinner(true).defaultDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1).maxDate(cal.get(Calendar.YEAR) + 25, cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
//                    .minDate(1900, 0, 1)
                    .minDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH) + 1).build().show();
        });


        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Server.setHeader(SessionManager.getKEY());

                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    String name = input_name.getText().toString().trim();
                    String lname = input_lname.getText().toString().trim();
                    String title_name = name_title.getSelectedItem().toString();
                    String mobile = input_mobile.getText().toString().trim();
                    if (Utils.isValidMobile(mobile)) {
                        if (!itemSelected) {
                            updateUserProfile(name,lname,title_name, mobile);
                        } else {
                            if (validateIdentityDoc()) updateUserProfile(name,lname,title_name, mobile);
                        }
                    } else showAlert("Please enter valid mobile number.");

                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
                //  updateUserProfile();

            }
        });
        profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // uploadImage();
                fileType = "profile_pic";
                showPictureDialog();
            }
        });

        identification_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileType = "identity_pic";
                showPictureDialog();

            }
        });


        return view;
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }

    //getting identity details
    public void getIdentity_details() {
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

                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter);
                        if (savedIdentityId != null && !savedIdentityId.isEmpty()) {
                            int spinnerPosition = dataAdapter.getPosition(savedIdentityId);
                            identity_spinner.setSelection(Integer.parseInt(savedIdentityId));
                            ll_identification_layout.setVisibility(View.VISIBLE);
                            identification_txt.setText(String.format("Update %s", identity_names.get(Integer.parseInt(savedIdentityId))));
                            itemSelected = false;
                        }
                        identity_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                                try {
                                    if (!identity_spinner.getItemAtPosition(position).equals("Select Identity")) {
                                        identityId = (identity_id.get(position - 1));
                                        identityName = adapterView.getItemAtPosition(position).toString();
                                        ll_identification_layout.setVisibility(View.VISIBLE);
                                        if (identity_names.get(Integer.parseInt(savedIdentityId)).equalsIgnoreCase(identityName)) {
                                            identification_txt.setText(String.format("Update %s", identityName));
                                        } else {
                                            resetIdentity(identityName);
                                        }

                                    } else {
                                        identityId = "";
                                        identityName = "";
                                        ll_identification_layout.setVisibility(View.GONE);
                                    }
                                } catch (Exception ex) {

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

                        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(requireContext(), R.layout.spinner_item, identity_names);
                        identity_spinner.setAdapter(dataAdapter1);
                    }
                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(requireContext(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
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

    //resetting identity
    private void resetIdentity(String identityName) {

        identification_txt.setText(String.format("Update %s", identityName));
        identity_expire_date.setText("Expire Date");
        identity_issue_date.setText("Issue Date");
        verificationIdFinalFile = new File("");
        identification_img.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_upload_pic, null));
        identityIssueDate = "";
        identityExpireDate = "";

        itemSelected = true;

    }

    //picture dialog
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(getActivity());
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {"Select photo from gallery", "Capture photo from camera"};
        pictureDialog.setItems(pictureDialogItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        galleryLauncher.launch(intent);
//                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

                        break;
                    case 1:
                        takePhotoFromCamera();
                        break;
                }
            }
        });
        pictureDialog.show();
    }

    File file;
    Uri fileUri;

    //capturing picture from camera
    private void takePhotoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        file = new File(getActivity().getExternalCacheDir(), String.valueOf(System.currentTimeMillis()) + ".jpg");
        fileUri = FileProvider.getUriForFile(requireContext(), getApplicationContext().getPackageName() + ".provider", file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        galleryLauncher.launch(intent);
//        startActivityForResult(intent, IMAGE_RESULT);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            profileUpdateListener = (ProfileUpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement profileUpdateListener");
        }
        try {
            listener = (UpdateListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement listener");
        }
    }

    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    public void bindView() {
        ((HomeActivity) getActivity()).fontToTitleBar(getString(R.string.profile));
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);

        profile_pic = (ImageView) view.findViewById(R.id.profile_pic);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        imgProgressBar = (ProgressBar) view.findViewById(R.id.img_progress);
        btn_change = view.findViewById(R.id.btn_change);

        input_email = (TextView) view.findViewById(R.id.input_email);
        input_email.setEnabled(false);
        //input_vehicle = (EditText) view.findViewById(R.id.input_vehicle);
        input_name = (EditText) view.findViewById(R.id.input_name);
        input_lname = (EditText) view.findViewById(R.id.input_lname);
        name_title = (Spinner) view.findViewById(R.id.name_title);
        //  input_password = (EditText) view.findViewById(R.id.input_password);
        input_mobile = (EditText) view.findViewById(R.id.input_mobile);
        input_mobile.addTextChangedListener(new PhoneNumberFormat(input_mobile));
        btn_update = view.findViewById(R.id.btn_update);

        //identification
        identification_img = view.findViewById(R.id.identification_img);
        ll_identification_layout = view.findViewById(R.id.ll_identification_layout);
        ll_identification_layout.setVisibility(View.GONE);
        identity_spinner = view.findViewById(R.id.identity_doc_spinner);
        identity_issue_date = view.findViewById(R.id.identification_issue_date);
        identity_expire_date = view.findViewById(R.id.identification_expire_date);
        identification_txt = view.findViewById(R.id.identification_txt);
        verificationIdFinalFile = new File("");

        //identificaiton

//        MediumFont(input_email);
//        //  MediumFont(input_vehicle);
//        MediumFont(input_name);
//
//        MediumFont(input_mobile);
       /* BookFont(btn_update);
        BookFont(btn_change);*/
        getProfile();


        // set profile details
       /* input_name.setText(getUserName());
        input_mobile.setText(getUserMobile());
        input_email.setText(getUserEmail());*/

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        askCompactPermissions(permissionAsk, new PermissionResult() {
            @Override
            public void permissionGranted() {
                if (!GPSEnable()) {
                    turnonGps();
                } else {
                    getCurrentlOcation();
                }
            }

            @Override
            public void permissionDenied() {

            }

            @Override
            public void permissionForeverDenied() {

            }
        });

        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getActivity())) {
                    changepassword_dialog(getString(R.string.change_password));
                } else {
                    Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });

        /*if (CheckConnection.haveNetworkConnection(getActivity())) {
            getUserInfo();
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();

            User user = SessionManager.getUser();
            input_name.setText(user.getName());
            input_email.setText(user.getEmail());
            input_mobile.setText(user.getMobile());
            Glide.with(getActivity()).load(user.getAvatar()).apply(new RequestOptions().error(R.mipmap.ic_account_circle_black_24dp)).into(profile_pic);


        }*/
    }


    public void BookFont(AppCompatButton view1) {
        Typeface font1 = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Book.otf");
        view1.setTypeface(font1);
    }

    public void MediumFont(TextView view) {
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "font/AvenirLTStd_Medium.otf");
        view.setTypeface(font);
    }

    //validation for mobile number
    private boolean isValidMobile(String phone) {
        //return android.util.Patterns.PHONE.matcher(phone).matches();
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 8 && phone.length() <= 13;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("permisson", "granted");
                    TedBottomPicker tedBottomPicker = new TedBottomPicker.Builder(getActivity()).setOnImageSelectedListener(new TedBottomPicker.OnImageSelectedListener() {
                        @Override
                        public void onImageSelected(Uri uri) {
                            // here is selected uri
                            profile_pic.setImageURI(uri);
                        }
                    }).setOnErrorListener(new TedBottomPicker.OnErrorListener() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(getActivity(), getString(R.string.try_again), Toast.LENGTH_LONG).show();
                            Log.d(getTag(), message);
                        }
                    }).create();

                    tedBottomPicker.show(getActivity().getSupportFragmentManager());

                } else {

                }
            }
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
    }

    private boolean checkIfAlreadyhavePermission() {
        int fine = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int read = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int write = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (fine == PackageManager.PERMISSION_GRANTED) {
            Log.e("permission1", "fine");
            return true;

        }
        if (read == PackageManager.PERMISSION_GRANTED) {
            Log.e("permission2", "coarse");
            return true;
        }
        if (write == PackageManager.PERMISSION_GRANTED) {
            Log.e("permission2", "coarse");
            return true;
        } else {
            return false;
        }
    }

    public void UpdateUser() {
        RequestParams params = new RequestParams();
        params.put("mobile", input_mobile.getText().toString().trim());
        params.put("name", input_name.getText().toString().trim());

        Server.setHeader(SessionManager.getKEY());


        params.put("user_id", SessionManager.getUserId());
        Server.post("api/user/update/format/json", params, new JsonHttpResponseHandler() {
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

                        User user = SessionManager.getUser();

                        input_name.setText(user.getName());
                        input_email.setText(user.getEmail());
                        input_mobile.setText(user.getMobile());

                        Toast.makeText(getActivity(), getString(R.string.profile_updated), Toast.LENGTH_LONG).show();

                        listener.name(input_name.getText().toString().trim());

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
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


    }

    //getting current location
    public void getCurrentlOcation() {

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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

    //turn Gps
    public void turnonGps() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity()).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
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
//        ((HomeActivity)requireActivity()).setProfile();
//
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    //Gps enable
    public Boolean GPSEnable() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return true;

        }
        return false;
    }

    //on Location changed
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
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
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();


            //Toast.makeText(getActivity(), currentLatitude + " WORKS " + currentLongitude + "", Toast.LENGTH_LONG).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                Toast.makeText(getActivity(), result, Toast.LENGTH_SHORT).show();
                getCurrentlOcation();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == IMAGE_RESULT && resultCode == RESULT_OK) {
            switch (fileType) {
                case "profile_pic":
                    try {
                        Bitmap bitmap = CameraRotation.handleSamplingAndRotationBitmap(requireContext(), fileUri);
                        Glide.with(requireContext()).load(bitmap).apply(new RequestOptions().error(R.drawable.img_logo).placeholder(R.drawable.icon)).into(profile_pic);
                    } catch (IOException e) {
                        Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.img_logo).placeholder(R.drawable.icon))

                                .into(profile_pic);
                        e.printStackTrace();
                    }
                    file = compressImage(fileUri.getPath());
                    String profile_path = file.getPath();
                    finalFile = new File(profile_path);

                    break;
                case "identity_pic":
                    Glide.with(requireContext()).load(fileUri.toString()).apply(new RequestOptions().error(R.drawable.img_logo).placeholder(R.drawable.icon)).into(identification_img);
                    file = compressImage(fileUri.getPath());
                    String identity_path = file.getPath();
                    verificationIdFinalFile = new File(identity_path);
                    break;
            }
        }


        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try {
                inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                Bitmap bmp = BitmapFactory.decodeStream(inputStream);
//                profile_pic.setImageBitmap(bmp);

                //Convert bitmap to byte array
                Bitmap bitmap = bmp;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
                byte[] bitmapdata = bos.toByteArray();

//                String path = getRealPathFromURI(getImageUri(getActivity(), bmp));
//                finalFile = new File(path);
//
//                Log.d("Image Path", "++++++++++++++++" + path);

                switch (fileType) {
                    case "profile_pic":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(profile_pic);
                        String profile_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        finalFile = new File(profile_path);

                        break;
                    case "identity_pic":
                        Glide.with(requireContext()).asBitmap().load(bmp).into(identification_img);
                        String identity_path = compressImage(RealPathUtil.getRealPath(requireContext(), data.getData())).getPath();
                        verificationIdFinalFile = new File(identity_path);

                        break;

                }

                //write the bytes in file
                // FileOutputStream fos = new FileOutputStream(filetoupload);
                // fos.write(bitmapdata);
                //  fos.flush();
                //  fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


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

//            ExifInterface ei = new ExifInterface(file.getPath());
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_UNDEFINED);
//
//            Bitmap rotatedBitmap = null;
//            switch(orientation) {
//
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotatedBitmap = rotateImage(bitmap, 90);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotatedBitmap = rotateImage(bitmap, 180);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotatedBitmap = rotateImage(bitmap, 270);
//                    break;
//
//                case ExifInterface.ORIENTATION_NORMAL:
//                default:
//                    rotatedBitmap = bitmap;
//            }

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

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
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

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        if (!wallpaperDirectory.exists()) {  // have the object build the directory structure, if needed.
            wallpaperDirectory.mkdirs();
        }
        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getActivity(), new String[]{f.getPath()}, new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("TAG", "File Saved::---&gt;" + f.getAbsolutePath());
            finalFile = new File(f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    public void upload_pic(String type) {
        progressBar.setVisibility(View.VISIBLE);
        RequestParams params = new RequestParams();
        if (imageFile != null) {
            try {

                if (type.equals("jpg")) {
                    params.put("avatar", imageFile, "image/jpeg");
                } else if (type.equals("jpeg")) {
                    params.put("avatar", imageFile, "image/jpeg");
                } else if (type.equals("png")) {
                    params.put("avatar", imageFile, "image/png");
                } else {
                    params.put("avatar", imageFile, "image/gif");
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("catch", e.toString());
            }
        }
        Server.setHeader(SessionManager.getKEY());
        params.put("user_id", SessionManager.getUserId());
        Server.post("api/user/update/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.e("success", response.toString());

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        String rurl = response.getJSONObject("data").getString("avatar");
                        profile_pic.setImageBitmap(null);

                        Glide.with(ProfileFragment.this).load(rurl).apply(new RequestOptions().error(R.mipmap.ic_account_circle_black_24dp)).into(profile_pic);
                        SessionManager.setAvatar(rurl);
                        profileUpdateListener.update(rurl);

                        User user = SessionManager.getUser();
                        input_name.setText(user.getName());
                        input_email.setText(user.getEmail());
                        input_mobile.setText(user.getMobile());

                        Toast.makeText(getActivity(), getString(R.string.profile_uploaded), Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e("catch", e.toString());
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("fail", responseString);

                Toast.makeText(getActivity(), getString(R.string.profile_uploaded), Toast.LENGTH_LONG).show();

            }
        });

    }

    public void getUserInfo() {
        RequestParams params = new RequestParams();

        params.put("user_id", SessionManager.getUserId());

        try {

            User user = SessionManager.getUser();
            //  Glide.with(getActivity()).load(user.getAvatar()).apply(new RequestOptions().error(R.mipmap.ic_account_circle_black_24dp)).into(profile_pic);
            input_name.setText(getUserName());
            input_mobile.setText(getUserMobile());
            input_email.setText(getUserEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }


        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/profile/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status")) {

                        Gson gson = new Gson();
                        User user = gson.fromJson(response.getJSONObject("data").toString(), User.class);
                        user.setKey(SessionManager.getKEY());
                        SessionManager.setUser(gson.toJson(user));

                        input_name.setText(user.getName());
                        input_mobile.setText(user.getMobile());
                        input_email.setText(user.getEmail());
                        profileUpdateListener.update(user.getAvatar());
                        listener.name(user.getName());
                    } else {
                        Toast.makeText(getActivity(), response.getString("data"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), getString(R.string.error_occurred), Toast.LENGTH_LONG).show();

                    Log.d("catch", e.toString());
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                Toast.makeText(getActivity(), "fail", Toast.LENGTH_LONG).show();
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

    public interface ProfileUpdateListener {
        void update(String url);

    }

    public interface UpdateListener {
        void name(String name);

    }


    //change password dialog
    public void changepassword_dialog(String title) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.update_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        params.gravity = Gravity.BOTTOM;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;

        TextView tle = (TextView) dialog.findViewById(R.id.title);
        final EditText password = (EditText) dialog.findViewById(R.id.input_Password);
        final EditText confirm_password = (EditText) dialog.findViewById(R.id.input_confirmPassword);
        final EditText old_password = (EditText) dialog.findViewById(R.id.old_Password);

        AppCompatButton btn_change = (AppCompatButton) dialog.findViewById(R.id.change_password);

        // MediumFont(tle);
//        MediumFont(password);
//        MediumFont(confirm_password);
//        MediumFont(old_password);
        //BookFont(btn_change);
        tle.setText(title);
        //btn_change.setText(getString(R.string.change));


        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(getActivity(), view);
                }
                String newPassword = password.getText().toString().trim();
                String newConfirmpassword = confirm_password.getText().toString().trim();
                String oldPassword = old_password.getText().toString().trim();
                if (oldPassword.isEmpty()) {
                    old_password.setError(getString(R.string.old_pws_is_required));
                }
                if (password.getText().toString().trim().equals("")) {
                    password.setError(getString(R.string.newpwd_required));
                } else if (!confirm_password.getText().toString().trim().equals("")) {

                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        if (oldPassword.equals(SessionManager.getOldPassword()))
                            changeUserPassword(dialog, newPassword, newConfirmpassword);
                        else showAlert("Old password does not match!");

                    } else {
                        Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
                    }
                } else {
                    confirm_password.setError(getString(R.string.confirm_pws_is_required));
                }
            }
        });
        dialog.show();
    }

    //alert dialog
    private void showAlert(String message) {
        androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(requireContext());
        dialogBuilder.setMessage(message).setCancelable(false).setIcon(R.drawable.close_icon).setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        androidx.appcompat.app.AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    private void uploadImage() {
        if (checkPermissions()) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        } else {
            checkPermissions();
        }
    }

    //checking permission
    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissionsFile) {
            result = ContextCompat.checkSelfPermission(getActivity(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;

    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //change user password
    private void changeUserPassword(final Dialog dialog, String newPassword, String cNewPassword) {
        Map<String, String> details = new HashMap<>();
        details.put("new_password", newPassword);
        details.put("confirm_password", cNewPassword);

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<ChangePasswordResponse> call = apiService.changePassword("Bearer " + SessionManager.getKEY(), details);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();

                } else {
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    // dialog.cancel();
                }
            }

            @Override
            public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                dialog.cancel();
            }
        });
    }

    //updating user password
    private void updateUserProfile(String name,String lname,String title_name, String mobile) {


        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Updating data", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();


        RequestBody request_name = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody request_lname = RequestBody.create(MediaType.parse("text/plain"), lname);
        RequestBody request_title_name = RequestBody.create(MediaType.parse("text/plain"), title_name);

        RequestBody request_mobile = RequestBody.create(MediaType.parse("text/plain"), mobile);

        RequestBody request_countryCode = RequestBody.create(MediaType.parse("text/plain"), "+1");

        RequestBody request_identification_id = RequestBody.create(MediaType.parse("text/plain"), identityId);

        RequestBody request_identity_issue = RequestBody.create(MediaType.parse("text/plain"), identityIssueDate);

        RequestBody request_identity_expire = RequestBody.create(MediaType.parse("text/plain"), identityExpireDate);


        MultipartBody.Part fileToUpload, identityFile;
        //empty file
        RequestBody empty_file = RequestBody.create(MediaType.parse("image/*"), "");

        try {

            if (!verificationID_path.isEmpty()) {
                //identityFile = MultipartBody.Part.createFormData("verification_id", verificationIdFinalFile.getName(), RequestBody.create(MediaType.parse("image/*"), verificationIdFinalFile));
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), VerificationIdUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final int rotation = getImageOrientation(verificationID_path);
                bitmap = checkRotationFromCamera(bitmap, rotation);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                verificationIdFileToupload = MultipartBody.Part.createFormData("verification_id", "image.jpg", requestBody);

            } else {
                verificationIdFileToupload = MultipartBody.Part.createFormData("verification_id", "", empty_file);
            }

            if (!avatar_path.isEmpty()) {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), avatarUri);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final int rotation = getImageOrientation(avatar_path);
                bitmap = checkRotationFromCamera(bitmap, rotation);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                byte[] imageBytes = outputStream.toByteArray();

                RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                avtarFileToupload = MultipartBody.Part.createFormData("profile_pic", "image.jpg", requestBody);
            } else {
                avtarFileToupload = MultipartBody.Part.createFormData("profile_pic", "", empty_file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Log.e("expire", identityIssueDate + ", " + identityExpireDate);

//        Call<ProfileResponse> call = apiService.updateProfile("Bearer " + SessionManager.getKEY(), name, mobile, "+1");

        Call<ProfileResponse> call = apiService.updateProfile("Bearer " + SessionManager.getKEY(),
                avtarFileToupload, request_name,request_lname,request_title_name, request_mobile, request_countryCode, request_identification_id, request_identity_issue,
                request_identity_expire, verificationIdFileToupload);

//        Call<ProfileResponse> call = apiService.updateProfile("Bearer " + SessionManager.getKEY(),
//                avtarFileToupload, request_name, request_mobile, request_countryCode);
        call.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, retrofit2.Response<ProfileResponse> response) {
                ProfileResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    loading.cancel();
                    setUserName(input_name.getText().toString().trim());
                    setUserMobile(input_mobile.getText().toString().trim());
                    Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_LONG).show();
                   /* input_name.setText(getUserName());
                    input_mobile.setText(getUserMobile());
                    input_name.setText(jsonResponse.getData().getName());
                    input_mobile.setText(jsonResponse.getData().getMobile());*/
                    getProfile();

                } else {
                    Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    loading.cancel();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                loading.cancel();
            }
        });
    }

    //validate Identity document
    public boolean validateIdentityDoc() {

        if (fileType.equalsIgnoreCase("identity_pic") && verificationIdFinalFile.getName().isEmpty() || verificationIdFinalFile.getName().equalsIgnoreCase("")) {
            Toast.makeText(requireContext(), "Please Upload Identity Document", Toast.LENGTH_LONG).show();
            return false;
        } else if (identityIssueDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select identity issue Date", Toast.LENGTH_SHORT).show();
            return false;
        } else if (identityExpireDate.isEmpty()) {
            Toast.makeText(requireContext(), "Please select identity expire Date", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }

    //getting profile
    private void getProfile() {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Please wait...", false, false);

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<GetProfile> call = apiService.getProfile("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<GetProfile>() {
            @Override
            public void onResponse(Call<GetProfile> call, retrofit2.Response<GetProfile> response) {
                GetProfile jsonResponse = response.body();
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {
                        loading.cancel();

                        input_email.setText(jsonResponse.getData().getEmail());
                        input_mobile.setText(jsonResponse.getData().getMobile());
                        input_name.setText(jsonResponse.getData().getName());
                        input_lname.setText(jsonResponse.getData().getLastName());

                        ArrayAdapter<String> titleNameAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, title_names);
                        name_title.setAdapter(titleNameAdapter);
                        int spinnerPosition = titleNameAdapter.getPosition(jsonResponse.getData().getNameTitle());
                        name_title.setSelection(spinnerPosition);

                        //identity doc
                        if (jsonResponse.getData().getIdentificationDocID() != null) {
                            savedIdentityId = jsonResponse.getData().getIdentificationDocID();

                        } else {
                            savedIdentityId = "";
                        }
                        if (jsonResponse.getData().getIdentificationIssueDate() != null) {
                            identityIssueDate = jsonResponse.getData().getIdentificationIssueDate();
                            identity_issue_date.setText(identityIssueDate);
                        } else {
                            identityIssueDate = "";
                        }
                        if (jsonResponse.getData().getIdentificationExpiryDate() != null) {
                            identityExpireDate = jsonResponse.getData().getIdentificationExpiryDate();
                            identity_expire_date.setText(identityExpireDate);
                        } else {
                            identityExpireDate = "";
                        }

                        if (jsonResponse.getData().getVerificationId() != null) {
                            String identificationUrl = jsonResponse.getData().getVerificationId();
                            if (identificationUrl.isEmpty() || identificationUrl.equalsIgnoreCase("")) {
                                Glide.with(getActivity()).load(R.drawable.user_default).listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        imgProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        imgProgressBar.setVisibility(View.GONE);
                                        return false;
                                    }
                                }).apply(new RequestOptions().error(R.drawable.user_default).placeholder(R.drawable.user_default)).into(identification_img);

                            } else {
                                Glide.with(getActivity()).load(jsonResponse.getData().getVerificationId()).apply(new RequestOptions().error(R.drawable.user_default).placeholder(R.drawable.user_default)).into(identification_img);

                            }
                        }

                        if (verificationIdFinalFile != null && verificationIdFinalFile.exists()) {

                            Glide.with(requireContext()).load(verificationIdFinalFile.getPath()).apply(new RequestOptions().error(R.drawable.img_logo).placeholder(R.drawable.user_default)).into(identification_img);
                        }


                        if (jsonResponse.getData().getProfileImage() != null) {
                            String profileUrl = jsonResponse.getData().getProfileImage();
                            if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase("")) {
                                Glide.with(getActivity()).load(R.drawable.user_default).apply(new RequestOptions().error(R.drawable.user_default).placeholder(R.drawable.img_logo)).into(profile_pic);
                            } else {
                                Glide.with(getActivity()).load(jsonResponse.getData().getProfilePic()).into(profile_pic);
                            }
                        }
                        String name = jsonResponse.getData().getName();
                        setTitle(name);
                        getIdentity_details();
                    } else {
                        Toast.makeText(getActivity(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                        loading.cancel();
                    }
                } else {

                    loading.cancel();
                }
            }

            @Override
            public void onFailure(Call<GetProfile> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                loading.cancel();
            }
        });
    }

    //setting title
    public void setTitle(String title) {
        Activity activity = getActivity();
        if (activity instanceof HomeActivity) {
            ((HomeActivity) activity).setTitle(title);
        }
    }
}
