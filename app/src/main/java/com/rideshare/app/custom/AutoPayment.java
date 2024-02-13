package com.rideshare.app.custom;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.rideshare.app.R;
import com.rideshare.app.Server.Server;
import com.rideshare.app.fragement.HomeFragment;
import com.rideshare.app.pojo.payment.CardDetail;
import com.rideshare.app.pojo.spend.PendingPojo;
import com.rideshare.app.Server.session.SessionManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class AutoPayment {
    private List<CardDetail> cardDetails;
    private PendingPojo pendingRequestPojo = null;
    private Context context;
    private HomeFragment homeFragment;
    private Dialog alertDialog;

    public AutoPayment(Context context, PendingPojo pendingPojo, HomeFragment homeFragment,Dialog dialog) {
        this.context = context;
        this.pendingRequestPojo = pendingPojo;
        cardDetails = new ArrayList<>();
        this.homeFragment = homeFragment;
        this.alertDialog = dialog;
        getCardLists();
    }


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

    private void getCardLists() {
        if (CheckConnection.haveNetworkConnection(context)) {

            final ProgressDialog loading = ProgressDialog.show(context, "", "Fetching card details", false, false);
            RequestParams params = new RequestParams();

            Server.setContetntType();
            Server.setHeader(SessionManager.getKEY());
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
                        List<CardDetail> list = gson.fromJson(response.getJSONArray("data").toString(),
                                new TypeToken<List<CardDetail>>() {
                                }.getType());
                        if (response.getJSONArray("data").length() != 0) {
                            cardDetails = list;

                            String card_id = getDefaultCard();
                            if (card_id != null) {
                                paymentUsingSavedCard(card_id);
                            } else {
                                Toast.makeText(context, "Please select your card.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                            }
                        }
                        else
                        {
                            Toast.makeText(context, "Please add card.", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        loading.cancel();
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
                    loading.cancel();
                }
            });


        } else {
            Toast.makeText(context, context.getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }


    public void paymentUsingSavedCard(String card_id) {
        if (CheckConnection.haveNetworkConnection(context)) {
            final ProgressDialog loading = ProgressDialog.show(context, "", "Making Payment", false, false);
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
                }


                Server.setContetntType();
                Server.setHeader(SessionManager.getKEY());
                Server.post(url, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);
                        loading.cancel();
                        try {
                            Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                            pendingRequestPojo.setRideId("0");
//                            HomeFragment homeFragment = new HomeFragment();
//                            if (context != null)
//                                ((HomeActivity) context).changeFragment(homeFragment, "Home");
                            alertDialog.dismiss();
                            homeFragment.endRide();
                            Log.e("Saved_card", response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        Toast.makeText(context, responseString, Toast.LENGTH_SHORT).show();
                        loading.cancel();
                        Log.e("Saved_card", responseString);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loading.cancel();
                    }
                });
            } else {
                Toast.makeText(context, "No Ride id", Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(context, context.getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }
}
