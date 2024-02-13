package com.rideshare.app.fragement;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.adapter.SpendAdapter;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.Utils;
import com.rideshare.app.databinding.FragmentSpendDetailsBinding;
import com.rideshare.app.databinding.SpendDatePickerBinding;
import com.rideshare.app.pojo.spend.PendingPojo;
import com.rideshare.app.pojo.spend.PendingPojoResponse;
import com.rideshare.app.Server.session.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//spend Details
public class SpendDetails extends Fragment {
    private boolean loading = true;
    int visibleItemCount, totalItemCount, pastVisibleItems;
    private FragmentSpendDetailsBinding spendDetailsBinding;
    private SpendAdapter mAdapter;
    private List<PendingPojo> pendingPojoList;
    private String userid = "", key = "", status = "COMPLETED";
    Utils utils = new Utils();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
    String fromDate = "", toDate = "";
    int pageNumber = 1, perPage = 10, totalNumberOfPage = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        spendDetailsBinding = FragmentSpendDetailsBinding.inflate(getLayoutInflater(), container, false);
        View rootView = spendDetailsBinding.getRoot();
        setHasOptionsMenu(true);
        BindView(rootView);
        return rootView;
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }
    //binding view
    private void BindView(View rootView) {

        ((HomeActivity) getActivity()).fontToTitleBar("Spend Details");
        userid = SessionManager.getUserId();
        key = SessionManager.getKEY();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        spendDetailsBinding.recyclerviewSpendDetails.setLayoutManager(linearLayoutManager);
        spendDetailsBinding.recyclerviewSpendDetails.setHasFixedSize(false);
        pendingPojoList = new ArrayList<>();
        mAdapter = new SpendAdapter(requireContext(), pendingPojoList);
        spendDetailsBinding.recyclerviewSpendDetails.setAdapter(mAdapter);


        spendDetailsBinding.swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                spendDetailsBinding.swipeRefresh.setRefreshing(false);
            }
        });

        spendDetailsBinding.recyclerviewSpendDetails.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int size = totalNumberOfPage / perPage;
                if (dy > 0) {
                    visibleItemCount = linearLayoutManager.getChildCount();
                    totalItemCount = linearLayoutManager.getItemCount();
                    pastVisibleItems = linearLayoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            loading = false;
                            if (totalNumberOfPage > pendingPojoList.size())
                                callAcceptedRequestApi(fromDate, toDate, String.valueOf(perPage), String.valueOf(pageNumber));
                            loading = true;
                            //Do pagination.. i.e. fetch new data

                        }


                    }
                }


            }

        });


        spendDetailsBinding.dateImg.setOnClickListener(e -> {
            showDatePickerDialog();
        });
        callAcceptedRequestApi(fromDate, toDate, String.valueOf(perPage), String.valueOf(pageNumber));

    }


    //showing date dialog
    public void showDatePickerDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setCancelable(false);
        SpendDatePickerBinding binding = SpendDatePickerBinding.inflate(requireActivity().getLayoutInflater());
        View dialogView = binding.getRoot();
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();


        Calendar formCalender = Calendar.getInstance();
        fromDate = "";
        toDate = "";

        binding.tvSelectFromDate.setOnClickListener(e -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), android.app.AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    formCalender.set(year, month, dayOfMonth, 0, 0);
                    fromDate = format.format(formCalender.getTime());
                    binding.tvSelectFromDate.setText((utils.getCurrentDateInSpecificFormat(fromDate)));
                }
            }, formCalender.get(Calendar.YEAR), formCalender.get(Calendar.MONTH), formCalender.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        Calendar toCalender = Calendar.getInstance();
        binding.tvSelectToDate.setOnClickListener(e -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),android.app.AlertDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    toCalender.set(year, month, dayOfMonth, 0, 0);
                    toDate = format.format(toCalender.getTime());
                    binding.tvSelectToDate.setText((utils.getCurrentDateInSpecificFormat(toDate)));
                }
            }, toCalender.get(Calendar.YEAR), toCalender.get(Calendar.MONTH), toCalender.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.show();

        });


        binding.btnCancelPaymentHistory.setOnClickListener(e -> {

            alertDialog.dismiss();
        });


        binding.btnSubmitPaymentHistory.setOnClickListener((e -> {
            if (fromDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please Select from date", Toast.LENGTH_SHORT).show();
            } else if (toDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please Select to date", Toast.LENGTH_SHORT).show();
            } else {
                pendingPojoList.clear();
                pageNumber = 1;
                spendDetailsBinding.txtTotalAmount.setText("Spend Amount: $0.00");
                callAcceptedRequestApi(fromDate, toDate, String.valueOf(perPage), String.valueOf(pageNumber));
                alertDialog.dismiss();
            }
        }));

        alertDialog.show();
    }


    @Override
    public void onResume() {
        super.onResume();

    }


    //Accepted request Api
    public void callAcceptedRequestApi(String fromDate, String toDate, String perPage, String pageNo) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            getAcceptedRequest(userid, status, key, fromDate, toDate, perPage, pageNo);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

    //gettting accepted request Api
    public void getAcceptedRequest(String id, String status, String key, String fromDate, String toDate, String perPage, String pageNo) {
        spendDetailsBinding.swipeRefresh.setRefreshing(true);
        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<PendingPojoResponse> call = apiService.getSpendDetails("Bearer " + key, status, fromDate, toDate, perPage, pageNo);

        call.enqueue(new Callback<PendingPojoResponse>() {
            @Override
            public void onResponse(Call<PendingPojoResponse> call, Response<PendingPojoResponse> response) {
                PendingPojoResponse pojoResponse = response.body();

                assert pojoResponse != null;
                try {
                    totalNumberOfPage = pojoResponse.getTotalRecord();
                    spendDetailsBinding.txtTotalAmount.setText(String.format("Spend Amount: $%s", pojoResponse.getTotalEarning()));
                } catch (Exception ex) {

                }
                if (pojoResponse.getStatus()) {
                    if (pojoResponse.getStatus() && pojoResponse.getData().size() == 0) {
                        spendDetailsBinding.txtError.setVisibility(View.VISIBLE);
                        spendDetailsBinding.noDataImg.setVisibility(View.VISIBLE);
                    } else {
                        spendDetailsBinding.txtError.setVisibility(View.GONE);
                        spendDetailsBinding.noDataImg.setVisibility(View.GONE);

                        if (totalNumberOfPage > pendingPojoList.size()) {
                            pendingPojoList.addAll(pojoResponse.getData());
                            mAdapter.setData(pendingPojoList);
                            pageNumber++;
                            loading = true;

                        }
                    }
                } else {
                    spendDetailsBinding.txtError.setVisibility(View.VISIBLE);
                    spendDetailsBinding.noDataImg.setVisibility(View.VISIBLE);
                }
                spendDetailsBinding.swipeRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<PendingPojoResponse> call, Throwable t) {
                spendDetailsBinding.txtError.setVisibility(View.VISIBLE);
                spendDetailsBinding.noDataImg.setVisibility(View.VISIBLE);
                spendDetailsBinding.swipeRefresh.setRefreshing(false);
            }
        });


    }


}