package com.rideshare.app.fragement;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rideshare.app.acitivities.AddPaymentMethodActivity;
import com.rideshare.app.pojo.payment.DefaultCardResponse;
import com.rideshare.app.pojo.spend.PendingPojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.adapter.PaymentMethodAdapter;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.SetCustomFont;
import com.rideshare.app.interfaces.CheckOptionClick;
import com.rideshare.app.pojo.payment.CardDetail;
import com.rideshare.app.Server.session.SessionManager;
import com.stripe.android.ApiResultCallback;

import com.stripe.android.Stripe;

import com.stripe.android.model.Card;

import com.stripe.android.model.Token;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

//Payment fragment
public class PaymentFragment extends Fragment {

    private View view;
    String userid = "";
    String key = "";
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    PaymentMethodAdapter mAdapter;
    List<CardDetail> cardDetails;
    Button btnAddCard, btnMakePayment;
    private SetCustomFont setCustomFont = new SetCustomFont();
    private boolean cardSelected = false;
    private int cardPosition = -1;
    private PendingPojo pendingRequestPojo = null;
    private TextView txt_error, txtInstruction;
    private ImageView noDataImg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_payment, container, false);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().show();
        setHasOptionsMenu(true);
        bindView();
        return view;
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }
    //binding view
    private void bindView() {
        ((HomeActivity) getActivity()).fontToTitleBar("Payment Method");
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);

        userid = SessionManager.getUserId();
        key = SessionManager.getKEY();
        btnMakePayment = view.findViewById(R.id.btn_make_payment);
        btnAddCard = view.findViewById(R.id.btn_add_card);
        txt_error = view.findViewById(R.id.txt_error);
        noDataImg = view.findViewById(R.id.noDataImg);
        txtInstruction = view.findViewById(R.id.tv_card_delete_instruction);

        noCardStatus();

        Bundle bundle = getArguments();
        if (bundle != null) {
            pendingRequestPojo = (PendingPojo) bundle.getSerializable("data");
            btnMakePayment.setVisibility(View.VISIBLE);

        } else {
            btnMakePayment.setVisibility(View.GONE);
        }

        btnMakePayment.setOnClickListener(e -> {
            String card_id = getDefaultCard();
            if (card_id != null) {
                paymentUsingSavedCard(card_id);
            } else {
                Toast.makeText(getContext(), "Please add card first.", Toast.LENGTH_SHORT).show();
            }
//            else {
//                makePayment();
//            }
        });

        btnAddCard.setOnClickListener(e -> {
            addNewCard();
        });
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_payment);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    //no card status
    private void noCardStatus() {
        if (cardDetails == null) {
            txt_error.setVisibility(View.VISIBLE);
            noDataImg.setVisibility(View.VISIBLE);
            txtInstruction.setVisibility(View.GONE);
        } else {
            if (cardDetails.size() == 0) {
                txt_error.setVisibility(View.VISIBLE);
                noDataImg.setVisibility(View.VISIBLE);
                txtInstruction.setVisibility(View.GONE);
            } else {
                txt_error.setVisibility(View.GONE);
                noDataImg.setVisibility(View.GONE);
                txtInstruction.setVisibility(View.VISIBLE);
            }
        }
    }

    //getting card list
    private void getCardLists() {
        if (CheckConnection.haveNetworkConnection(requireContext())) {


            RequestParams params = new RequestParams();

            Server.setContetntType();
            Server.setHeader(SessionManager.getKEY());
            Server.get("card_list", params, new JsonHttpResponseHandler() {
                @Override
                public void onStart() {
                    swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    // Toast.makeText(requireContext(), "Card List ....", Toast.LENGTH_SHORT).show();
                    Gson gson = new GsonBuilder().create();
                    try {
                        List<CardDetail> list = gson.fromJson(response.getJSONArray("data").toString(),
                                new TypeToken<List<CardDetail>>() {
                                }.getType());
                        if (response.getJSONArray("data").length() != 0) {
                            cardDetails = list;
                            noCardStatus();
//                            if (cardDetails != null) {
//                                if (cardDetails.size() > 0) {
//                                    for (int i = 0; i < cardDetails.size(); i++) {
//                                        if (cardDetails.get(i).getIsDefault().equals("1")) {
//                                          //  makeDefaultCard(cardDetails.get(i).getId(), false);
//                                        }
//                                    }
//                                }
//                            }
                            mAdapter = new PaymentMethodAdapter(cardDetails, new CheckOptionClick() {
                                @Override
                                public void onClick(int position, boolean isChecked) {
                                    cardPosition = position;
                                    if (isChecked)
                                        cardSelected = true;
                                    else
                                        cardSelected = false;
                                    makeDefaultCard(cardDetails.get(position).getId(), cardSelected);
                                }
                            });
                            recyclerView.setAdapter(mAdapter);
                            if (cardDetails != null)
                                mAdapter.notifyDataSetChanged();
                        } else {
                            //  Toast.makeText(requireContext(), "No Saved Card found.", Toast.LENGTH_SHORT).show();
                            Log.e("Card_LIST", "Empty");
                            cardDetails = list;
                            mAdapter = new PaymentMethodAdapter(cardDetails, new CheckOptionClick() {
                                @Override
                                public void onClick(int position, boolean isChecked) {
                                    cardPosition = position;
                                    if (isChecked)
                                        cardSelected = true;
                                    else
                                        cardSelected = false;
                                    makeDefaultCard(cardDetails.get(position).getId(), cardSelected);
                                }
                            });
                            recyclerView.setAdapter(mAdapter);
                            mAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("Card_LIST", "Json Error");
                    }
                    Log.e("Card_List", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Log.e("Payment_Response", responseString);
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
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //make payment
    private void makePayment() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_new_card_layout, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        AlertDialog alertDialog = dialogBuilder.create();
        setCustomFont.overrideFonts(requireContext(), dialogView);
        EditText etNameOnCard = dialogView.findViewById(R.id.et_name_on_card);
        EditText etCardNumber = dialogView.findViewById(R.id.et_card_number);
        EditText etExpiryDate = dialogView.findViewById(R.id.et_expiry_date);
        EditText etBillingAddress = dialogView.findViewById(R.id.et_billing_address);
        EditText etCVV = dialogView.findViewById(R.id.et_cvv);
        Button btnAdd = dialogView.findViewById(R.id.btn_add);
        Button btnPay = dialogView.findViewById(R.id.btn_pay);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);

        btnPay.setVisibility(View.VISIBLE);
        btnAdd.setVisibility(View.GONE);
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
//                if (start == 1 && start + count == 2 && !s.toString().contains("/")){
//                    etExpiryDate.setText(s.toString() + "/");
//                } else if (start == 3 && start - before == 2 && s.toString().contains("/")){
//                    etExpiryDate.setText(s.toString().replace("/", ""));
//                }
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


        // etCardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cardNumber = etCardNumber.getText().toString();
                String[] date = etExpiryDate.getText().toString().split("/");
                String cvv = etCVV.getText().toString();
                String name = etNameOnCard.getText().toString();


                if (isFilled(cardNumber, date, cvv, name)) {
                    String month = date[0];
                    String year = date[1];
                    Card card = Card.create(etCardNumber.getText().toString().trim(), Integer.valueOf(month),
                            Integer.valueOf(year), etCVV.getText().toString());
                    if (!card.validateCard()) {
                        Toast.makeText(requireContext(), "Invalid card", Toast.LENGTH_SHORT).show();
                    } else {
                        new Stripe(requireContext(), Server.SPIKE_KEY).createCardToken(card, new ApiResultCallback<Token>() {
                            @Override
                            public void onSuccess(@NonNull Token token) {
                                //  Toast.makeText(getContext(), "Token: " + token, Toast.LENGTH_SHORT).show();
                                makePayment(token.getId());
                                Log.e("token", token.toString());
                                alertDialog.dismiss();
                            }

                            @Override
                            public void onError(@NonNull Exception e) {

                            }
                        });
                    }
                } else {
                    Toast.makeText(requireContext(), "Please fill all details", Toast.LENGTH_LONG).show();
                }

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }

    //validation
    private boolean isFilled(String cardNumber, String[] date, String cvv, String name) {

        if (cardNumber.isEmpty()) {
            showAlert("Enter valid card number.");
            return false;
        }
        if (date.length < 2) {
            showAlert("Enter valid expire date.");
            return false;
        }
        if (cvv.isEmpty()) {
            showAlert("Enter valid cvv.");
            return false;
        }
        if (name.isEmpty()) {
            showAlert("Enter card holder name.");
            return false;
        }
        return true;
    }

    //error
    public static void setErrorMsg(String msg, EditText viewId) {
        int ecolor = Color.WHITE; // whatever color you want
        String estring = msg;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(estring);
        ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
        viewId.setError(ssbuilder);

    }

    //adding new card
    private void addNewCard() {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.add_new_card_layout);
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
//        LayoutInflater inflater = this.getLayoutInflater();
//        View dialogView = inflater.inflate(R.layout.add_new_card_layout, null);
//        dialogBuilder.setView(dialogView);
//        dialogBuilder.setCancelable(false);
//        AlertDialog alertDialog = dialogBuilder.create();


//        setCustomFont.overrideFonts(requireContext(), dialogView);
        EditText etNameOnCard = dialog.findViewById(R.id.et_name_on_card);
        EditText etCardNumber = dialog.findViewById(R.id.et_card_number);
        EditText etExpiryDate = dialog.findViewById(R.id.et_expiry_date);
        EditText etBillingAddress = dialog.findViewById(R.id.et_billing_address);
        EditText etCVV = dialog.findViewById(R.id.et_cvv);
        Button btnAdd = dialog.findViewById(R.id.btn_add);
        Button btnPay = dialog.findViewById(R.id.btn_pay);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);


        btnPay.setVisibility(View.GONE);
        btnAdd.setVisibility(View.VISIBLE);
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
//                if (start == 1 && start + count == 2 && !s.toString().contains("/")){
//                    etExpiryDate.setText(s.toString() + "/");
//                } else if (start == 3 && start - before == 2 && s.toString().contains("/")){
//                    etExpiryDate.setText(s.toString().replace("/", ""));
//                }
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


        // etCardNumber.addTextChangedListener(new FourDigitCardFormatWatcher());
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = etNameOnCard.getText().toString();
                String cardNumber = etCardNumber.getText().toString();
                String expiryDate = etExpiryDate.getText().toString();
                String cvv = etCVV.getText().toString();
                String billingAddress = etBillingAddress.getText().toString();

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
                            Toast.makeText(requireContext(), "Invalid card", Toast.LENGTH_SHORT).show();
                        } else {
                            if (CheckConnection.haveNetworkConnection(requireContext())) {
                                new Stripe(requireContext(), Server.SPIKE_KEY)
                                        .createCardToken(card, new ApiResultCallback<Token>() {
                                            @Override
                                            public void onSuccess(@NonNull Token token) {
                                                //  Toast.makeText(getContext(), "Token: " + token, Toast.LENGTH_SHORT).show();
                                                saveCard(token.getId(), cardNumber, date[1], date[0], name, token.getCard().getBrand().name(), billingAddress);

                                                Log.e("add", token.toString());
                                                dialog.dismiss();

                                            }

                                            @Override
                                            public void onError(@NonNull Exception e) {
                                                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            } else {
                                Toast.makeText(requireContext(), "Internet is not available.", Toast.LENGTH_SHORT).show();
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
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //default card
    public String getDefaultCard() {
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

    //doing payment
    public void paymentUsingSavedCard(String card_id) {
        final ProgressDialog loading = ProgressDialog.show(getContext(), "", "Making Payment...", false, false);
        if (CheckConnection.haveNetworkConnection(requireContext())) {

            if (pendingRequestPojo != null) {
                RequestParams params = new RequestParams();
                String url = "payment";
                if (pendingRequestPojo.getPaymentStatus().equalsIgnoreCase("tip_amount")) {
                    url = "pay_tip_amount";
                    params.put("ride_id", pendingRequestPojo.getRideId());
                    params.put("tip_amount", pendingRequestPojo.getAmount());
                    params.put("card_id", card_id);
                } else {
                    params.put("ride_id", pendingRequestPojo.getRideId());
                    params.put("amount", pendingRequestPojo.getAmount());
                    params.put("card_id", card_id);
                    params.put("txn_id",pendingRequestPojo.getTxnId());
                }


                Server.setContetntType();
                Server.setHeader(SessionManager.getKEY());
                Server.post(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        try {
                            loading.cancel();
                            Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                            pendingRequestPojo.setRideId("0");
                            HomeFragment homeFragment = new HomeFragment();
                            if (requireContext() != null)
                                ((HomeActivity) requireContext()).changeFragment(homeFragment, "Home");
                            Log.e("Saved_card", response.toString());
                        } catch (JSONException e) {
                            loading.cancel();
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        loading.cancel();
                        Toast.makeText(requireContext(), responseString, Toast.LENGTH_SHORT).show();
                        Log.e("Saved_card", responseString);
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
                loading.cancel();
                Toast.makeText(requireContext(), "No Ride id", Toast.LENGTH_SHORT).show();
            }

        } else {
            loading.cancel();
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //convertFourDigit
    private String convertTwoDigitToFourDigitYear(String digit) throws ParseException {
        DateFormat sdfp = new SimpleDateFormat("yy");
        Date d = sdfp.parse(digit);
        DateFormat sdff = new SimpleDateFormat("yyyy");
        String date = sdff.format(d);
        return date;
    }

    //saving card
    private void saveCard(String token, String cardNumber, String date, String month,
                          String name, String cardType, String billingAddress) {

        if (CheckConnection.haveNetworkConnection(requireContext())) {

            try {
                date = convertTwoDigitToFourDigitYear(date);
            } catch (ParseException ex) {

            }
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
                    swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);

                    // try {
                    //Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    //New change on 14_09_20222

//                        getCardLists();
                    Gson gson = new Gson();
                    DefaultCardResponse defaultCardResponse = gson.fromJson(response.toString(), DefaultCardResponse.class);
                    if (defaultCardResponse.getData() == null) {
                        Toast.makeText(requireContext(), defaultCardResponse.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    } else {
                        makeDefaultCard(defaultCardResponse.getData().get(0).getId(), true);
                        Log.e("Card_Added", response.toString());
                    }
                    // }
//                    catch (JSONException e) {
//                        e.printStackTrace();
//                    }


                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(requireContext(), responseString, Toast.LENGTH_SHORT).show();
                    Log.e("Payment_Response", responseString);
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
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getCardLists();


        Log.e("onResume", "Payment : Get Card List");
    }

    //making payment
    public void makePayment(String token) {
        if (CheckConnection.haveNetworkConnection(requireContext())) {

            if (pendingRequestPojo != null) {
                RequestParams params = new RequestParams();
                String url = "payment";
                if (pendingRequestPojo.getPaymentStatus().equalsIgnoreCase("tip_amount")) {
                    url = "pay_tip_amount";
                    params.put("ride_id", pendingRequestPojo.getRideId());
                    params.put("tip_amount", pendingRequestPojo.getAmount());
                    params.put("stripeToken", token);
                } else {
                    params.put("ride_id", pendingRequestPojo.getRideId());
                    params.put("amount", pendingRequestPojo.getAmount());
                    params.put("stripeToken", token);
                }

                Server.setContetntType();
                Server.setHeader(SessionManager.getKEY());
                Server.post(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        swipeRefreshLayout.setRefreshing(true);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        try {
                            Toast.makeText(requireContext(), response.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                            pendingRequestPojo.setRideId("0");
                            if (requireContext() != null)
                                ((HomeActivity) requireContext()).changeFragment(new HomeFragment(), "Home");
                            Log.e("Payment_Response", response.toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(requireContext(), responseString, Toast.LENGTH_SHORT).show();
                        Log.e("Payment_Response", responseString);
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
                Toast.makeText(requireContext(), "No ride id ", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //making default card
    public void makeDefaultCard(String cardId, boolean isChecked) {
        if (CheckConnection.haveNetworkConnection(requireContext())) {


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
                    swipeRefreshLayout.setRefreshing(true);
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    super.onSuccess(statusCode, headers, response);
                    // try {
                    //  Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                    Log.e("make_default", response.toString());
                    getCardLists();

//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                    Toast.makeText(requireContext(), responseString, Toast.LENGTH_SHORT).show();
                    Log.e("Payment_Response", responseString);
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
            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }


    ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            // Toast.makeText(requireContext(), "on Move", Toast.LENGTH_SHORT).show();
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
            // Toast.makeText(requireContext(), "on Swiped ", Toast.LENGTH_SHORT).show();
            //Remove swiped item from list and notify the RecyclerView
            int position = viewHolder.getAdapterPosition();
            if (cardDetails != null) {
                String card_id = cardDetails.get(position).getId();
                deleteCard(card_id);
            }
            mAdapter.notifyDataSetChanged();

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                // Get RecyclerView item from the ViewHolder
                View itemView = viewHolder.itemView;

                Paint p = new Paint();
                p.setColor(getResources().getColor(R.color.red));
                if (dX > 0) {
                    /* Set your color for positive displacement */

                    // Draw Rect with varying right side, equal to displacement dX
                    c.drawRoundRect((float) itemView.getLeft(), (float) itemView.getTop(), dX,
                            (float) itemView.getBottom(), 10, 10, p);
                } else {
                    /* Set your color for negative displacement */

                    // Draw Rect with varying left side, equal to the item's right side plus negative displacement dX
                    c.drawRoundRect((float) itemView.getRight() + dX, (float) itemView.getTop(),
                            (float) itemView.getRight(), (float) itemView.getBottom(), 10, 10, p);
                }


            }
        }
    };


    //alert dialog
    private void showAlert(String message) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
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

    //deleting card
    private void deleteCard(String card_id) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());

        dialogBuilder
                .setMessage("Do you really want to delete this card?")
                .setCancelable(false)
                .setIcon(R.drawable.close_icon)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (CheckConnection.haveNetworkConnection(requireContext())) {


                            RequestParams params = new RequestParams();
                            params.put("card_id", card_id);
                            Server.setContetntType();
                            Server.setHeader(SessionManager.getKEY());
                            Server.post("delete_card", params, new JsonHttpResponseHandler() {
                                @Override
                                public void onStart() {
                                    swipeRefreshLayout.setRefreshing(true);
                                }

                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    super.onSuccess(statusCode, headers, response);
                                    try {
                                        Toast.makeText(requireContext(), response.getString("message"), Toast.LENGTH_SHORT).show();

                                        Log.e("make_default", response.toString());
                                        getCardLists();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                    super.onFailure(statusCode, headers, responseString, throwable);
                                    //Toast.makeText(requireContext(), responseString, Toast.LENGTH_SHORT).show();
                                    Log.e("delete_card", responseString);
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
                            Toast.makeText(requireContext(), getString(R.string.network), Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();


    }


    public void validateEditText(EditText myEdit) {
        if (TextUtils.isEmpty(myEdit.getText().toString())) {
            // set the warning
            myEdit.requestFocus();

            // show error icon
            myEdit.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_error_icon, 0);

            // remove the error icon on key press
            myEdit.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {

                    EditText eText = (EditText) v;

                    // remove error sign
                    eText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                    // remove this key handler
                    eText.setOnKeyListener(null);

                    return false;
                }
            });
        }
    }

}