package com.rideshare.app.acitivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.pojo.CheckDeviceTokenResponse;
import com.rideshare.app.pojo.UpdateLoginLogoutResponse;
import com.rideshare.app.pojo.payment.DefaultCardResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.LoginUser;
import com.rideshare.app.Server.session.SessionManager;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;

//Add Payment Methods
public class AddPaymentMethodActivity extends AppCompatActivity {
    Toolbar toolbar;
    String gcmToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_payment_method);
        updateLoginLogotApi();
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }

                        // Get new FCM registration token
                        gcmToken = task.getResult();
                        SessionManager.setGcmToken(gcmToken);
                        // Log and toast
                      /*  String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show()*/
                        ;
                    }
                });
        if (SessionManager.getIsCardSaved()){

        }else{

        }


        bindView();
    }

    //backpressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (SessionManager.getIsCardSaved()){
            Intent i = new Intent(AddPaymentMethodActivity.this,SplashActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private EditText etNameOnCard, etCardNumber, etExpiryDate, etCVV, etBillingAddress;
    private Button btnAdd, btnCancel;
    private String password = "";

    //binding view
    private void bindView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Add Payment Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        etNameOnCard = findViewById(R.id.et_name_on_card);
        etCardNumber = findViewById(R.id.et_card_number);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        etCVV = findViewById(R.id.et_cvv);
        btnAdd = findViewById(R.id.btn_add);
        btnCancel = findViewById(R.id.btn_cancel);
        etBillingAddress = findViewById(R.id.et_billing_address);
        password = getIntent().getStringExtra("Password");


        etCardNumber.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position
            // is every 4th symbol beginning with 0
            private static final char DIVIDER = ' ';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS),
                            DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });


        etExpiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() == 2) {
                    if (start == 2 && before == 1 && !s.toString().contains("/")) {
                        etExpiryDate.setText("" + s.toString().charAt(0));
                        etExpiryDate.setSelection(1);
                    } else {
                        etExpiryDate.setText(s + "/");
                        etExpiryDate.setSelection(3);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etNameOnCard.getText().toString();
                String cardNumber = etCardNumber.getText().toString();
                String expiryDate = etExpiryDate.getText().toString();
                String cvv = etCVV.getText().toString();
                String billingAddress = etBillingAddress.getText().toString();
                if (billingAddress.isEmpty())
                    billingAddress = "";
                if (name.isEmpty()) {
                    showAlert("Enter Card Holder Name.");

                } else if (cardNumber.isEmpty()) {

                    showAlert("Enter Valid Card Number.");

                } else if (expiryDate.isEmpty()) {
                    showAlert("Enter Valid Expire Date.");
                } else if (cvv.isEmpty()) {
                    showAlert("Enter Valid CVV Number.");
                } else {
                    String[] date = expiryDate.split("/");
                    if (date.length < 2) {
                        etExpiryDate.setError("Please Enter Valid Date Expire Date.");
                    } else {

                        //If all Conditions are valid
                        Card card = Card.create(cardNumber.trim(), Integer.valueOf(date[0]),
                                Integer.valueOf(date[1]), cvv);
                        if (!card.validateCard()) {
                            Toast.makeText(AddPaymentMethodActivity.this, "Invalid card", Toast.LENGTH_SHORT).show();
                        } else {
                            if (CheckConnection.haveNetworkConnection(AddPaymentMethodActivity.this)) {
                                String finalBillingAddress = billingAddress;
                                new Stripe(AddPaymentMethodActivity.this, Server.SPIKE_KEY)
                                        .createCardToken(card, new ApiResultCallback<Token>() {
                                            @Override
                                            public void onSuccess(@NonNull Token token) {
                                                //  Toast.makeText(getContext(), "Token: " + token, Toast.LENGTH_SHORT).show();
                                                saveCard(token.getId(), cardNumber, date[1], date[0], name, token.getCard().getBrand().name(), finalBillingAddress);

                                                Log.e("add", token.toString());
                                            }

                                            @Override
                                            public void onError(@NonNull Exception e) {
                                                Toast.makeText(AddPaymentMethodActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(AddPaymentMethodActivity.this, "Internet is not available.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
                //End of If/Else
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    //save card
    private void saveCard(String token, String cardNumber, String date, String month, String name, String cardType, String billingAddress) {


        if (CheckConnection.haveNetworkConnection(AddPaymentMethodActivity.this)) {
            final ProgressDialog loading = ProgressDialog.show(AddPaymentMethodActivity.this, "", "Please wait...", false, false);
            RequestParams params = new RequestParams();
            params.put("card_number", cardNumber);
            params.put("expiry_month", month);
            params.put("stripeToken", token);
            params.put("expiry_date", date);
            params.put("card_holder_name", name);
            params.put("card_type", cardType);
            params.put("billing_address", billingAddress);

            Server.setContetntType();
            Server.setHeader(SessionManager.getKEY());
            Server.post("add_customer", params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    //swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    loading.cancel();
                    try {
                        Toast.makeText(AddPaymentMethodActivity.this, response.getString("message"), Toast.LENGTH_SHORT).show();
                        SessionManager.setStatus(response.getBoolean("status"));
                        Gson gson = new Gson();
                        DefaultCardResponse defaultCardResponse = gson.fromJson(response.toString(), DefaultCardResponse.class);
                        if (defaultCardResponse.getData()==null) {
                            Toast.makeText(getApplicationContext(), defaultCardResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                        } else {
                            String card_id = defaultCardResponse.getData().get(0).getId();
                            makeDefaultCard(card_id, true);

                        }

                        Log.e("Card_Added", response.toString());
                    } catch (JSONException e) {
                        loading.cancel();
                        e.printStackTrace();
                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    loading.cancel();
                    Toast.makeText(AddPaymentMethodActivity.this, responseString, Toast.LENGTH_SHORT).show();
                    Log.e("Payment_Response", responseString);
                }

                @Override
                public void onFinish() {
                    super.onFinish();
                    loading.cancel();
                }
            });

        } else {
            Toast.makeText(AddPaymentMethodActivity.this, getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //showing Alert dialog
    private void showAlert(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AddPaymentMethodActivity.this);
        dialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setIcon(R.drawable.close_icon)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


    //making card default
    public void makeDefaultCard(String cardId, boolean isChecked) {
        if (CheckConnection.haveNetworkConnection(this)) {

            final ProgressDialog loading = ProgressDialog.show(AddPaymentMethodActivity.this, "", "Please wait...", false, false);
            RequestParams params = new RequestParams();
            params.put("card_id", cardId);
            if (isChecked)
                params.put("is_default", "1");
            else
                params.put("is_default", "2");

            Server.setContetntType();
            Server.setHeader(SessionManager.getKEY());
            Server.post("make_default", params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    super.onStart();
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    loading.cancel();
                    // try {
                    //  Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_SHORT).show();jitesh rawal
                    Log.e("make_default", response.toString());
                    SessionManager.setIsCardSaved(true);
                    Toast.makeText(AddPaymentMethodActivity.this, "Making default card", Toast.LENGTH_SHORT).show();
                    LoginUser.login(AddPaymentMethodActivity.this, AddPaymentMethodActivity.this, SessionManager.getUserEmail(), password, gcmToken);
                    Intent intent = new Intent(AddPaymentMethodActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                    //                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    loading.cancel();
//                    Toast.makeText(AddPaymentMethodActivity.this, responseString, Toast.LENGTH_SHORT).show();
                    Log.e("Payment_Response", responseString);
                }

                @Override
                public void onFinish() {
                    super.onFinish();

                        loading.cancel();

                }
            });


        } else {

            Toast.makeText(this, getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    public void updateLoginLogotApi() {

        // final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        if (CheckConnection.haveNetworkConnection(getApplicationContext())) {
            // progressDialog.setMessage("Session checking.....");
            //progressDialog.setCancelable(false);
            //progressDialog.show();
            Map<String,String> params = new HashMap();
            params.put("status", "2");

            ApiNetworkCall apiService = ApiClient.getApiService();

            Call<UpdateLoginLogoutResponse> call =
                    apiService.updateLoginLogoutApi("Bearer " + SessionManager.getKEY(),params);
            call.enqueue(new Callback<UpdateLoginLogoutResponse>() {
                @Override
                public void onResponse(Call<UpdateLoginLogoutResponse> call, retrofit2.Response<UpdateLoginLogoutResponse> response) {
                    UpdateLoginLogoutResponse jsonResponse = response.body();
                    Log.d("UpdateLoginLogoutResponse",response.toString());
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

}