package com.rideshare.app.acitivities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.AppCompatButton;

import android.provider.Settings;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.rideshare.app.databinding.LoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.SetCustomFont;
import com.rideshare.app.pojo.changepassword.ChangePasswordResponse;
import com.rideshare.app.Server.session.SessionManager;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.thebrownarrow.permissionhelper.ActivityManagePermission;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;

//Login Screen
public class LoginActivity extends ActivityManagePermission implements View.OnClickListener {

    FirebaseUser currentUser;
    private static final String TAG = "login";
    EditText input_email, input_password;
    AppCompatButton login;
    TextView as, txt_createaccount, forgot_password;
    private String token;
    Context context;
    SwipeRefreshLayout swipeRefreshLayout;
    private LoginBinding binding;
    boolean isPasswordVisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.login);
        bindView();
        passwordToggleSetup();
        // applyfonts();

        if (SessionManager.getKEY() == null) {
            Log.d("prf", "null");
        } else {
            Log.d("prf", SessionManager.getKEY().toString());
        }

        //several clickable parts of multiple texts in a single textview
        SpannableString ss = new SpannableString("Not a member? Sign Up");
        final ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(View textView) {
                // do some thing
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                finish();
            }
        };

        ss.setSpan(span1, 14, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        txt_createaccount.setText(ss);
        txt_createaccount.setMovementMethod(LinkMovementMethod.getInstance());
        txt_createaccount.setHighlightColor(Color.TRANSPARENT);

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {

                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        login(binding.edtEmail.getText().toString().trim(), binding.edtPassword.getText().toString().trim());
                        SessionManager.setOldPassword(input_password.getText().toString().trim());
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
                    }

                } else {
                    // do nothing
                }
            }
        });

        binding.btnSignUp.setOnClickListener(this);


        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.e(TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                token = task.getResult();
                SessionManager.setGcmToken(token);
            }
        });

       /* FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                SessionManager.setGcmToken(token);
            }
        });*/
    }

    //binding view
    public void bindView() {
        context = LoginActivity.this;
        as = (TextView) findViewById(R.id.as);
        forgot_password = (TextView) findViewById(R.id.txt_forgotpassword);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        txt_createaccount = (TextView) findViewById(R.id.txt_createaccount);
        input_email = (EditText) findViewById(R.id.input_email);
        input_password = (EditText) findViewById(R.id.input_password);
        login = (AppCompatButton) findViewById(R.id.login);

        binding.btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                    changepassword_dialog();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //validation
    public Boolean validate() {
        Boolean value = true;
        if (binding.edtEmail.getText().toString().equals("") && !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.edtEmail.getText().toString().trim()).matches()) {
            value = false;
            binding.edtEmail.setError(getString(R.string.email_invalid));
        } else {
            binding.edtEmail.setError(null);
        }

        if (binding.edtPassword.getText().toString().trim().equalsIgnoreCase("")) {
            value = false;
            binding.edtPassword.setError(getString(R.string.fiels_is_required1));
        } else {
            binding.edtPassword.setError(null);
        }
        return value;
    }

    public void applyfonts() {
        if (getCurrentFocus() != null) {
            SetCustomFont setCustomFont = new SetCustomFont();
            setCustomFont.overrideFonts(getApplicationContext(), getCurrentFocus());
        } else {
            Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
            Typeface font1 = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
            input_email.setTypeface(font1);
            input_password.setTypeface(font1);
            login.setTypeface(font);
            txt_createaccount.setTypeface(font);
            forgot_password.setTypeface(font);
        }
    }

    //Login Api
    public void login(String email, String password) {
        // val mId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("deviceId", deviceId);
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("utype", "1");
        params.put("gcm_token", token);
        params.put("device_type", "android");
        params.put("device_token", deviceId);
        SessionManager.setDeviceId(deviceId);
        Log.d("loginParam", params.toString());

        Server.post("login", params, new JsonHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                swipeRefreshLayout.setRefreshing(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.d("response", response.toString());
                progressDialog.dismiss();
                try {
                    if (response.has("status") && response.getBoolean("status")) {
                        CheckConnection checkConnection = new CheckConnection(LoginActivity.this);
                        checkConnection.isAnonymouslyLoggedIn();

                        //If card add is not compulsory
                        if (!response.getJSONObject("data").getBoolean("add_card")) {
                            Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                            SessionManager.setIsCardSaved(true);
                            SessionManager.setKEY(response.getString("token"));
                            SessionManager.setStatus(response.getBoolean("status"));
                            SessionManager.setUserId(response.getJSONObject("data").getString("user_id"));
                            SessionManager.setUserEmail(response.getJSONObject("data").getString("email"));
                            SessionManager.setUserName(response.getJSONObject("data").getString("name"));
                            SessionManager.setUserMobile(response.getJSONObject("data").getString("mobile"));
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                            finish();
                        } else //If card to be add
                        {
                            if (!response.getJSONObject("data").getBoolean("is_card")) {
                                SessionManager.setIsCardSaved(false);
                                SessionManager.setKEY(response.getString("token"));
                                Toast.makeText(LoginActivity.this, "Please Add Payment Details.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(LoginActivity.this, AddPaymentMethodActivity.class));
                            } else {
                                SessionManager.setIsCardSaved(true);
                                SessionManager.setKEY(response.getString("token"));
                                SessionManager.setStatus(response.getBoolean("status"));
                                SessionManager.setUserId(response.getJSONObject("data").getString("user_id"));
                                SessionManager.setUserEmail(response.getJSONObject("data").getString("email"));
                                SessionManager.setUserName(response.getJSONObject("data").getString("name"));
                                SessionManager.setUserMobile(response.getJSONObject("data").getString("mobile"));
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                            }
                        }


                    } else {
                        error_dialog(response.getString("message"));
                        // Toast.makeText(LoginActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, getString(R.string.contact_admin), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                progressDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Something went wrong...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //Reset Password
    public void resetPassword(String email, String OTP, String pass, final Dialog dialog) {

        Map<String, String> details = new HashMap<>();
        details.put("email", email);
        details.put("otp", OTP);
        details.put("password", pass);

        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<ChangePasswordResponse> call = apiService.resetPassword(details);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();
                    //  resetpassword_dialog();

                } else {
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
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

    //changePassword Dialog
    public void changepassword_dialog() {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.send_otp);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText email = (EditText) dialog.findViewById(R.id.input_email);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView message = (TextView) dialog.findViewById(R.id.message);

        AppCompatButton btn_send_otp = (AppCompatButton) dialog.findViewById(R.id.btn_send_otp);
        AppCompatButton btn_cancel = (AppCompatButton) dialog.findViewById(R.id.btn_cancel);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
//        btn_send_otp.setTypeface(font1);
//        btn_cancel.setTypeface(font1);
//        email.setTypeface(font);
//        title.setTypeface(font);
//        message.setTypeface(font);


        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LoginActivity.this.getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(LoginActivity.this, view);
                }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                    //dialog.cancel();
                    // resetPassword(email.getText().toString().trim(), dialog);
                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        sendOTP(email.getText().toString().trim(), dialog);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                    }


                } else {
                    email.setError(getString(R.string.email_invalid));
                    // Toast.makeText(LoginActivity.this, "email is invalid", Toast.LENGTH_LONG).show();
                }


            }
        });
        dialog.show();

    }

    //resetPasswordDialog
    public void resetpassword_dialog(String userEmail) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_reset_password);
        dialog.setCancelable(false);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final EditText email = (EditText) dialog.findViewById(R.id.input_email);
        email.setText(userEmail);
        final EditText otp = (EditText) dialog.findViewById(R.id.input_otp);
        final EditText password = (EditText) dialog.findViewById(R.id.input_Password);
        final EditText cpassword = (EditText) dialog.findViewById(R.id.input_confirmPassword);
        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView message = (TextView) dialog.findViewById(R.id.message);

        AppCompatButton btn_reset_password = (AppCompatButton) dialog.findViewById(R.id.btn_reset_password);
        AppCompatButton btn_cancel = (AppCompatButton) dialog.findViewById(R.id.btn_cancel);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");
//        btn_reset_password.setTypeface(font1);
//        btn_cancel.setTypeface(font1);
//        email.setTypeface(font);
//        title.setTypeface(font);
//        message.setTypeface(font);

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        btn_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LoginActivity.this.getCurrentFocus();
                if (view != null) {
                    CheckConnection.hideKeyboard(LoginActivity.this, view);
                }
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
                    //dialog.cancel();
                    // resetPassword(email.getText().toString().trim(), dialog);
                    if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
                        resetPassword(email.getText().toString().trim(), otp.getText().toString().trim(), password.getText().toString().trim(), dialog);
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
                    }

                } else {
                    email.setError(getString(R.string.email_invalid));
                    // Toast.makeText(LoginActivity.this, "email is invalid", Toast.LENGTH_LONG).show();
                }


            }
        });
        dialog.show();

    }

    //sendOtpApi
    private void sendOTP(String email, final Dialog dialog) {
        ProgressDialog progressOtpDialog = new ProgressDialog(this);
        progressOtpDialog.setMessage("Sending OTP...");
        progressOtpDialog.setCancelable(false);
        progressOtpDialog.show();
        Map<String, String> details = new HashMap<>();
        details.put("email", email);


        ApiNetworkCall apiService = ApiClient.getApiService();

        Call<ChangePasswordResponse> call = apiService.sendOTP(details);
        call.enqueue(new Callback<ChangePasswordResponse>() {
            @Override
            public void onResponse(Call<ChangePasswordResponse> call, retrofit2.Response<ChangePasswordResponse> response) {
                ChangePasswordResponse jsonResponse = response.body();
                if (jsonResponse.getStatus()) {
                    progressOtpDialog.dismiss();
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
                    dialog.cancel();
                    resetpassword_dialog(email);

                } else {
                    Toast.makeText(LoginActivity.this, jsonResponse.getMessage(), Toast.LENGTH_LONG).show();
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

    //error dialog
    public void error_dialog(String msg) {
        final Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.error_dialog);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView message = (TextView) dialog.findViewById(R.id.errorMsg);
        message.setText(msg);

        Button ok = dialog.findViewById(R.id.btnOk);

        Typeface font = Typeface.createFromAsset(getAssets(), "font/montserrat_regular.ttf");
        Typeface font1 = Typeface.createFromAsset(getAssets(), "font/montserrat_bold.ttf");

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void passwordToggleSetup() {
        binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        binding.passwordToggleBtn.setImageDrawable(getDrawable(R.drawable.ic_close_eye));
        binding.passwordToggleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPasswordVisible) {
                    binding.edtPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    binding.passwordToggleBtn.setImageDrawable(getDrawable(R.drawable.ic_open_eye));
                    isPasswordVisible = false;

                } else {
                    binding.edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    binding.passwordToggleBtn.setImageDrawable(getDrawable(R.drawable.ic_close_eye));
                    isPasswordVisible = true;
                }
                // Move the cursor to the end of the text
                binding.edtPassword.setSelection(binding.edtPassword.getText().length());
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignUp:
                Intent intent1 = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent1);
                overridePendingTransition(R.anim.motion_in, R.anim.motion_out);
                finish();
                break;

        }
    }
}
