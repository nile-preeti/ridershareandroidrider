package com.rideshare.app.acitivities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.multidex.BuildConfig;

import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.rideshare.app.fragement.AboutUs;
import com.rideshare.app.fragement.HelpFragment;
import com.rideshare.app.fragement.PreAuthorizationPolicy;
import com.rideshare.app.fragement.SpendDetails;
import com.rideshare.app.pojo.CheckAppVersionResponse;
import com.rideshare.app.pojo.CheckDeviceTokenResponse;
import com.rideshare.app.pojo.Global;
import com.rideshare.app.pojo.LogoutResponse;
import com.rideshare.app.pojo.delete_account.DeleteAccount;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.fragement.AcceptedRequestFragment;
import com.rideshare.app.fragement.HomeFragment;
import com.rideshare.app.fragement.PaymentFragment;
import com.rideshare.app.fragement.PrivacyPolicyFragment;
import com.rideshare.app.fragement.ProfileFragment;
import com.rideshare.app.pojo.User;
import com.rideshare.app.pojo.getprofile.GetProfile;
import com.rideshare.app.Server.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rideshare.app.tracker.TimerService;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;

import static com.rideshare.app.Server.session.SessionManager.getUserEmail;
import static com.rideshare.app.Server.session.SessionManager.getUserName;
import static com.rideshare.app.Server.session.SessionManager.initialize;
import static com.mapbox.mapboxsdk.Mapbox.getApplicationContext;

//Home Activity
public class HomeActivity extends ActivityManagePermission implements NavigationView.OnNavigationItemSelectedListener, ProfileFragment.ProfileUpdateListener, ProfileFragment.UpdateListener {

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    public Toolbar toolbar;
    public TextView is_online, username, email, txtRiderRating;
    SwitchCompat switchCompat;
    LinearLayout linearLayout;
    NavigationView navigationView;
    private ImageView avatar;
    private ProgressBar avatarProgressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        initialize(getApplicationContext());

        BindView();
        Intent intent = getIntent();

        checkAppVersionApi();

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
    }

    //Side menu setup
    private void setupDrawer() {
        setSupportActionBar(toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        //  globatTitle = );
        getSupportActionBar().setTitle(getString(R.string.app_name));

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerStateChanged(int newState) {
                super.onDrawerStateChanged(newState);

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                closeKeyboard();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        //drawer.shouldDelayChildPressedState();


    }

    //closing keybord
    public void closeKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) this
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    this.getCurrentFocus().getWindowToken(),
                    0
            );

        } catch (NullPointerException ex) {

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    public void drawer_close() {
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        AcceptedRequestFragment acceptedRequestFragment = new AcceptedRequestFragment();
        Bundle bundle;

        switch (item.getItemId()) {
            case R.id.home:
                changeFragment(new HomeFragment(), getString(R.string.home));
                break;
            case R.id.pending_requests:
                bundle = new Bundle();
                bundle.putString("status", "PENDING");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, "Requests");
                break;
            case R.id.accepted_requests:
                bundle = new Bundle();
                bundle.putString("status", "ACCEPTED");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, "Requests");
                break;
            case R.id.completed_rides:
                bundle = new Bundle();
                bundle.putString("status", "COMPLETED");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, "Requests");
                break;
            case R.id.spend_activity:
                changeFragment(new SpendDetails(), "Spend Details");
                break;

            case R.id.payment:
                changeFragment(new PaymentFragment(), "Payment Method");
                break;

            case R.id.cancelled:
                bundle = new Bundle();
                bundle.putString("status", "CANCELLED");
                acceptedRequestFragment.setArguments(bundle);
                changeFragment(acceptedRequestFragment, "Requests");
                break;
            case R.id.profile:
                changeFragment(new ProfileFragment(), getString(R.string.profile));
                break;
            case R.id.menu_pre_authorized_policy:
                changeFragment(new PreAuthorizationPolicy(), "PreAuthorization Policy");
                break;
            case R.id.menu_privacy_policy:
                changeFragment(new PrivacyPolicyFragment(), "Privacy Policy");
                break;
            case R.id.menu_about_us:
                changeFragment(new AboutUs(), "About Us");
                break;
            case R.id.menu_help:
                changeFragment(new HelpFragment(), "Help");
                break;
            case R.id.logout:
                logout();
                break;
            case R.id.delete_account:
                showAlert(this, "Delete Account", "Are you sure you want to delete account? \n\nWhen you delete your account. you won\'t be able to retrieve the content and ride information on app.");
                break;
            default:
                break;
        }
        return true;
    }

    //showing Alert dialog
    public void showAlert(Context context, String title, String message) {
        new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_DARK)
                .setMessage(message)
                .setCancelable(false)
                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        deleteAccount();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton("No", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    //delet Account API
    public void deleteAccount() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Deleting Account.....");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<DeleteAccount> call =
                    apiService.deleteAccount("Bearer " + SessionManager.getKEY());
            call.enqueue(new Callback<DeleteAccount>() {
                @Override
                public void onResponse(Call<DeleteAccount> call, retrofit2.Response<DeleteAccount> response) {
                    DeleteAccount jsonResponse = response.body();
                    if (jsonResponse != null) {
                        if (jsonResponse.getStatus()) {
                            progressDialog.cancel();
                            Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                            SessionManager.logoutUser(getApplicationContext());
                            finish();
                        } else {
                            progressDialog.cancel();
                            Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<DeleteAccount> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();
                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }


    //Log out Api
    public void logout() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        if (CheckConnection.haveNetworkConnection(this)) {
            progressDialog.setMessage("Loading.....");
            progressDialog.setCancelable(false);
            progressDialog.show();
            Map<String, String> logoutStatus = new HashMap<>();

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<LogoutResponse> call =
                    apiService.logoutStatus("Bearer " + SessionManager.getKEY(), logoutStatus);
            call.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(Call<LogoutResponse> call, retrofit2.Response<LogoutResponse> response) {
                    LogoutResponse jsonResponse = response.body();
                    if (jsonResponse != null) {
                        if (jsonResponse.getStatus()) {
                            Toast.makeText(HomeActivity.this, "Logout Successfully", Toast.LENGTH_SHORT).show();
                            SessionManager.logoutUser(getApplicationContext());
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }

                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    Log.d("Failed", "RetrofitFailed");
                    progressDialog.cancel();

                }
            });
        } else {
            progressDialog.cancel();
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();

        }
    }

    //Replacing  Fragment
    public void changeFragment(final Fragment fragment, final String fragmenttag) {

        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    drawer_close();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame, fragment, fragmenttag);
                    fragmentTransaction.commit();
                    //   fragmentTransaction.addToBackStack(null);
                }
            }, 50);
        } catch (Exception e) {

        }

    }


    @Override
    public void update(String url) {
        if (!url.equals("")) {
            Glide.with(getApplicationContext())
                    .load(url).apply(new RequestOptions().error(R.drawable.images).placeholder(R.drawable.ridesharelogo)).into(avatar);
        }
    }

    @Override
    public void name(String name) {
        if (!name.equals("")) {
            username.setText(getUserName());
        }
    }

    @SuppressLint("ParcelCreator")
    public class CustomTypefaceSpan extends TypefaceSpan {

        private final Typeface newType;

        public CustomTypefaceSpan(String family, Typeface type) {
            super(family);
            newType = type;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            applyCustomTypeFace(ds, newType);
        }

        @Override
        public void updateMeasureState(TextPaint paint) {
            applyCustomTypeFace(paint, newType);
        }

        private void applyCustomTypeFace(Paint paint, Typeface tf) {
            int oldStyle;
            Typeface old = paint.getTypeface();
            if (old == null) {
                oldStyle = 0;
            } else {
                oldStyle = old.getStyle();
            }

            int fake = oldStyle & ~tf.getStyle();
            if ((fake & Typeface.BOLD) != 0) {
                paint.setFakeBoldText(true);
            }

            if ((fake & Typeface.ITALIC) != 0) {
                paint.setTextSkewX(-0.25f);
            }

            paint.setTypeface(tf);
        }
    }

    //Apply font
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    //font To TitleBar
    public void fontToTitleBar(String title) {
        try {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
            title = "<font color='#FFFFFF'>" + title + "</font>";
            SpannableString s = new SpannableString(title);
            s.setSpan(font, 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                toolbar.setTitle(Html.fromHtml(String.valueOf(s), Html.FROM_HTML_MODE_LEGACY));
            } else {
                toolbar.setTitle((Html.fromHtml(String.valueOf(s))));
            }
        } catch (Exception e) {
            Log.e("catch", e.toString());
        }
    }


    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = HomeActivity.this.getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible())
                    return fragment;
            }
        }
        return null;
    }

    //binding view
    public void BindView() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        switchCompat = (SwitchCompat) navigationView.getHeaderView(0).findViewById(R.id.online);
        avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.profile);
        avatarProgressBar = (ProgressBar) navigationView.getHeaderView(0).findViewById(R.id.avatar_progress);
        avatar.setOnClickListener(e -> {
            changeFragment(new ProfileFragment(), getString(R.string.profile));
        });
        linearLayout = (LinearLayout) navigationView.getHeaderView(0).findViewById(R.id.linear);
        is_online = (TextView) navigationView.getHeaderView(0).findViewById(R.id.is_online);
        username = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
        email = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email);
        txtRiderRating = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txtRiderRating);
        username.setText(getUserName());
        email.setText(getUserEmail());

        TextView version = (TextView) navigationView.getHeaderView(0).findViewById(R.id.version);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            String ver = pInfo.versionName;
            version.setText("V ".concat(ver));
        } catch (PackageManager.NameNotFoundException e) {

        }

        navigationView.setCheckedItem(R.id.home);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.home));
        setupDrawer();
        try {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
            // username.setTypeface(font);
            setProfile();
        } catch (Exception e) {

        }
        toolbar.setTitle("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        //  setProfile();
    }

    @Override
    protected void onPause() {
        startTimerService();
        super.onPause();

        HomeFragment.timeHandler.removeCallbacks(HomeFragment.timeRunnable);
        Global.setIsRunningInBackground(this, true);
        HomeFragment.canRun = false;
    }

    private void startTimerService() {
        if (HomeFragment.isRunning) {
            Global.setTimerSecondsPassed(this, HomeFragment.seconds);
            Intent serviceIntent = new Intent(this, TimerService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent);
            }
        }
    }

    //Profile set
    public void setProfile() {
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            getUserInfo();
            getProfile();
        } else {
            Toast.makeText(HomeActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
            String name = SessionManager.getName();
            String url = SessionManager.getAvatar();
            Log.d("AvatarUrl", url);
            username.setText(getUserName());
            email.setText(getUserEmail());
            Glide.with(getApplicationContext()).load(url).apply(new RequestOptions().error(R.mipmap.ic_account_circle_black_24dp).placeholder(R.drawable.ridesharelogo)).into(avatar);
        }
    }

    //getting user Info
    public void getUserInfo() {
        String uid = SessionManager.getUserId();
        RequestParams params = new RequestParams();
        params.put("user_id", uid);
        Server.setHeader(SessionManager.getKEY());
        Server.get("api/user/profile/format/json", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                try {
                    if (response.has("status") && response.getString("status").equalsIgnoreCase("success")) {

                        Gson gson = new Gson();

                        User user = gson.fromJson(response.getJSONObject("data").toString(), User.class);
                        user.setKey(SessionManager.getKEY());

                        SessionManager.setUser(gson.toJson(user));

                        Glide.with(HomeActivity.this).load(user.getAvatar()).apply(new RequestOptions().error(R.drawable.images).placeholder(R.drawable.ridesharelogo)).into(avatar);

                        username.setText(getUserName());
                        email.setText(getUserEmail());


                    }
                } catch (Exception e) {

                }
            }

        });

    }


    //BackPressed
    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawer_close();
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    //setting title
    public void setTitle(String title) {
        if (username != null) {
            username.setText(title);
            email.setText(getUserEmail());
        }
    }

    //getting profile
    private void getProfile() {

        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<GetProfile> call = apiService.getProfile("Bearer " + SessionManager.getKEY());
        call.enqueue(new Callback<GetProfile>() {
            @Override
            public void onResponse(Call<GetProfile> call, retrofit2.Response<GetProfile> response) {
                GetProfile jsonResponse = response.body();
//                Log.d("profileResponse",response.body().getData().getProfilePic().toString());
                // Log.d("profileResponse",response.body().getData().getProfileImage().toString());
                if (jsonResponse != null) {
                    if (jsonResponse.getStatus()) {
                        username.setText(jsonResponse.getData().getName());
                        email.setText(jsonResponse.getData().getEmail());
                        txtRiderRating.setText(jsonResponse.getData().getRiderTotalRating());

                        if (jsonResponse.getData().getProfileImage() != null) {
                            String profileUrl = jsonResponse.getData().getProfileImage();
                            Log.d("PRofileURL", profileUrl);
                            if (profileUrl.isEmpty() || profileUrl.equalsIgnoreCase("")) {
                                avatarProgressBar.setVisibility(View.GONE);

                                Glide.with(HomeActivity.this).load(R.drawable.user_default).apply(new RequestOptions().error(R.drawable.user_default).placeholder(R.drawable.ridesharelogo)).into(avatar);
                            } else {

                                Glide.with(HomeActivity.this).load(jsonResponse.getData().getProfilePic())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                avatarProgressBar.setVisibility(View.GONE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                avatarProgressBar.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .apply(new RequestOptions().placeholder(R.drawable.ridesharelogo).error(R.drawable.user_default)).into(avatar);
                            }
                        }
                        String name = jsonResponse.getData().getName();
                        setTitle(name);
                    } else {
                        Toast.makeText(HomeActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }


            }

            @Override
            public void onFailure(Call<GetProfile> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }

    public void checkAppVersionApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
            Map<String, String> param = new HashMap();
            param.put("device_type", "android");
            param.put("app_version", BuildConfig.VERSION_NAME.toString());
            param.put("user_type", "1");

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<CheckAppVersionResponse> call =
                    apiService.checkAppVersionApi("Bearer " + SessionManager.getKEY(), param);
            call.enqueue(new Callback<CheckAppVersionResponse>() {
                @Override
                public void onResponse(Call<CheckAppVersionResponse> call, retrofit2.Response<CheckAppVersionResponse> response) {
                    CheckAppVersionResponse jsonResponse = response.body();
                    Log.d("response", response.toString());
                    if (jsonResponse != null) {
                        Log.d("TakeResponse", jsonResponse.getMessage().toString());
                        if (jsonResponse.getStatus()) {
                            if (jsonResponse.getMessage().equals("please update App Version")) {
                                isUpdateAvailable(true);
                            } else {
                                isUpdateAvailable(false);
                            }
                        } else {
                            //progressDialog.cancel();
                            isUpdateAvailable(false);
                            Toast.makeText(getApplicationContext(), jsonResponse.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                }

                @Override
                public void onFailure(Call<CheckAppVersionResponse> call, Throwable t) {
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

    // Method to show the update dialog
    private void showUpdateDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("App Update Available");
        builder.setMessage("A new version of the app is available. Please update to enjoy the latest features.");
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle update button click
                // Redirect to Google Play Store or start download process
                final String appPackageName = getPackageName(); // package name of the app
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        builder.setNegativeButton("Remind Me Later", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle "Remind Me Later" button click
                // ..
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        android.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Method to check if an update is available
    private void isUpdateAvailable(boolean status) {
        // Implement the logic to check for updates
        if (status) {
            showUpdateDialog();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_navigation_items, menu);
        MenuItem help = menu.findItem(R.id.help);
        MenuItem home = menu.findItem(R.id.home);
        MenuItem pending_requests = menu.findItem(R.id.pending_requests);
        MenuItem accepted_requests = menu.findItem(R.id.accepted_requests);
        MenuItem completed_rides = menu.findItem(R.id.completed_rides);
        MenuItem cancelled = menu.findItem(R.id.cancelled);
        MenuItem spend_detail = menu.findItem(R.id.spend_activity);
        MenuItem payment = menu.findItem(R.id.payment);
        MenuItem profile = menu.findItem(R.id.profile);
        MenuItem menu_pre_authorized_policy = menu.findItem(R.id.menu_pre_authorized_policy);
        MenuItem menu_privacy_policy = menu.findItem(R.id.menu_privacy_policy);
        MenuItem menu_about_us = menu.findItem(R.id.menu_about_us);
        MenuItem menu_help = menu.findItem(R.id.menu_help);
        MenuItem deletAccount = menu.findItem(R.id.delete_account);
        MenuItem logout = menu.findItem(R.id.logout);

        home.setVisible(false);
        pending_requests.setVisible(false);
        accepted_requests.setVisible(false);
        completed_rides.setVisible(false);
        cancelled.setVisible(false);
        spend_detail.setVisible(false);
        payment.setVisible(false);
        profile.setVisible(false);
        menu_pre_authorized_policy.setVisible(false);
        menu_privacy_policy.setVisible(false);
        menu_about_us.setVisible(false);
        menu_help.setVisible(false);
        deletAccount.setVisible(false);
        logout.setVisible(false);

        help.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                changeFragment(new HelpFragment(), "Help");
                return false;
            }
        });
        help.setIcon(R.drawable.help);
        return true;
    }
}
