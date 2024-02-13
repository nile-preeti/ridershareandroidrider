package com.rideshare.app.fragement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.pojo.spend.PendingPojo;
import com.rideshare.app.pojo.spend.PendingPojoResponse;
import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.adapter.AcceptedRequestAdapter;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.SetCustomFont;
import com.rideshare.app.Server.session.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by android on 10/3/17.
 */

//Accepted Request
public class AcceptedRequestFragment extends Fragment {
    private View view;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    String userid = "";
    String key = "";
    private boolean loading = true;
    TextView txt_error;
    String status;
    ImageView noDataImg;
    private AcceptedRequestAdapter acceptedRequestAdapter;
    private List<PendingPojo> list;
    int visibleItemCount, totalItemCount, pastVisibleItems;
    int pageNumber = 1, perPage = 10, totalNumberOfPage = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.accepted_request_fragment, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        setHasOptionsMenu(true);
        bindView();

        return view;
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }
    public void bindView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        txt_error = (TextView) view.findViewById(R.id.txt_error);
        noDataImg = view.findViewById(R.id.noDataImg);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        list = new ArrayList<>();
        acceptedRequestAdapter = new AcceptedRequestAdapter(list);
        recyclerView.setAdapter(acceptedRequestAdapter);

        userid = SessionManager.getUserId();
        key = SessionManager.getKEY();

        Bundle bundle = getArguments();
        if (bundle != null) {
            status = bundle.getString("status");
            ((HomeActivity) getActivity()).fontToTitleBar(setTitle(status));
        }
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);

        pageNumber = 1;perPage=10;

        callAcceptedRequestApi(String.valueOf(perPage), String.valueOf(pageNumber));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            if (totalNumberOfPage > list.size())
                                callAcceptedRequestApi(String.valueOf(perPage), String.valueOf(pageNumber));
                            loading = true;
                            //Do pagination.. i.e. fetch new data

                        }
                    }
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

//calling accept request Api
    public void callAcceptedRequestApi(String perPage, String pageNo) {
        if (CheckConnection.haveNetworkConnection(requireActivity())) {
            getAcceptedRequest(userid, status, key, perPage, pageNo);
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
    }

//getting accepted request
    public void getAcceptedRequest(String id, String status, String key, String perPage, String pageNo) {
        swipeRefreshLayout.setRefreshing(true);
        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<PendingPojoResponse> call = apiService.getSpendDetails("Bearer " + key, status, "", "", perPage, pageNo);

        call.enqueue(new Callback<PendingPojoResponse>() {
            @Override
            public void onResponse(Call<PendingPojoResponse> call, Response<PendingPojoResponse> response) {
                PendingPojoResponse pojoResponse = response.body();

                assert pojoResponse != null;
                try {
                    totalNumberOfPage = pojoResponse.getTotalRecord();

                } catch (Exception ex) {

                }
                if (pojoResponse.getStatus()) {
                    if (pojoResponse.getStatus() && pojoResponse.getData().size() == 0) {
                        txt_error.setVisibility(View.VISIBLE);
                        noDataImg.setVisibility(View.VISIBLE);
                    } else {
                        txt_error.setVisibility(View.GONE);
                        noDataImg.setVisibility(View.GONE);

                        if (totalNumberOfPage >= list.size()) {
                            list.addAll(pojoResponse.getData());
                            acceptedRequestAdapter.setData(list);
                            pageNumber++;
                            loading = true;

                        }
                    }
                } else {
                    txt_error.setVisibility(View.VISIBLE);
                    noDataImg.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<PendingPojoResponse> call, Throwable t) {
                txt_error.setVisibility(View.VISIBLE);
                noDataImg.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }

        });
    }

    //setting title
    private String setTitle(String s) {
        String title = null;
        switch (s) {
            case "ACCEPTED":
                title = getString(R.string.accepted_request);
                break;
            case "PENDING":
                title = getString(R.string.pending_request);
                break;
            case "CANCELLED":
                title = getString(R.string.cancelled_request);
                break;
            case "COMPLETED":
                title = getString(R.string.completed_request);
                break;

        }
        return title;
    }

}
