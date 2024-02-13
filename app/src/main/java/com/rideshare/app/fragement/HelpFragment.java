package com.rideshare.app.fragement;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.adapter.HelpQuestionCategoryAdapter;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.custom.SetCustomFont;
import com.rideshare.app.databinding.FragmentHelpBinding;
import com.rideshare.app.pojo.help_question.SubCategoryData;
import com.rideshare.app.pojo.help_question.SubCategoryResponse;
import com.rideshare.app.Server.session.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


//Help Fragment
public class HelpFragment extends Fragment {


    private FragmentHelpBinding binding;
    private HelpQuestionCategoryAdapter mAdapter;

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.help);
        item.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHelpBinding.inflate(getLayoutInflater(), container, false);
        View rootView = binding.getRoot();
        setHasOptionsMenu(true);
        SetCustomFont font = new SetCustomFont();

        font.overrideFonts(requireContext(), rootView);
        ((HomeActivity) getActivity()).fontToTitleBar("Help");
        binding.questionHelpRv.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));

        if (CheckConnection.haveNetworkConnection(requireContext())) {
            getQuestionSubCategory("7");
        } else {
            Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
        }
        return rootView;
    }

    //getting Question sub category
    public void getQuestionSubCategory(String id) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Fetching data", false, false);
        String auth_token = "Bearer " + SessionManager.getKEY();
        ApiNetworkCall apiService = ApiClient.getApiService();
        Call<SubCategoryResponse> call = apiService.getQuestionSubCategory(auth_token, id);
        call.enqueue(new Callback<SubCategoryResponse>() {
            @Override
            public void onResponse(Call<SubCategoryResponse> call, retrofit2.Response<SubCategoryResponse> response) {
                SubCategoryResponse jsonResponse = response.body();
                loading.cancel();
                if (jsonResponse.getStatus()) {
                    List<SubCategoryData> categoryData = jsonResponse.getData();
                    mAdapter = new HelpQuestionCategoryAdapter(categoryData, new HelpQuestionCategoryAdapter.ItemClicked() {
                        @Override
                        public void onItemClicked(int id, String question, String email) {
                            Bundle bundle = new Bundle();
                            HelpSubmitFragment helpSubmitFragment = new HelpSubmitFragment();
                            bundle.putInt("id", id);
                            bundle.putString("question", question);
                            bundle.putString("email", email);
                            helpSubmitFragment.setArguments(bundle);
                            ((HomeActivity) requireContext()).changeFragment(helpSubmitFragment, "submit_question");
                        }
                    });

                    binding.questionHelpRv.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();


                }
            }


            @Override
            public void onFailure(Call<SubCategoryResponse> call, Throwable t) {
                Log.d("Failed", "RetrofitFailed");
                loading.cancel();
            }
        });
    }

}