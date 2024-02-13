package com.rideshare.app.fragement;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rideshare.app.R;
import com.rideshare.app.Server.session.SessionManager;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.custom.SetCustomFont;
import com.rideshare.app.pojo.spend.PendingPojo;


public class RatingFragment extends Fragment {
    private View view;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_rating, container, false);
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
    private void bindView() {
        ((HomeActivity) getActivity()).fontToTitleBar("Rating");
        SetCustomFont setCustomFont = new SetCustomFont();
        setCustomFont.overrideFonts(getActivity(), view);

//        btnMakePayment = view.findViewById(R.id.btn_make_payment);
//        btnAddCard = view.findViewById(R.id.btn_add_card);
//        txt_error = view.findViewById(R.id.txt_error);
//        noDataImg = view.findViewById(R.id.noDataImg);
//        txtInstruction = view.findViewById(R.id.tv_card_delete_instruction);
//
//
//        btnMakePayment.setOnClickListener(e -> {
//            String card_id = getDefaultCard();
//            if (card_id != null) {
//                paymentUsingSavedCard(card_id);
//            } else {
//                Toast.makeText(getContext(), "Please add card first.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        btnAddCard.setOnClickListener(e -> {
//            addNewCard();
//        });

//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
//
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                swipeRefreshLayout.setRefreshing(false);
//            }
//        });
    }
}