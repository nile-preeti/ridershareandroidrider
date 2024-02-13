package com.rideshare.app.fragement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.connection.ApiClient;
import com.rideshare.app.connection.ApiNetworkCall;
import com.rideshare.app.custom.CheckConnection;
import com.rideshare.app.databinding.FragmentHelpSubmitBinding;
import com.rideshare.app.pojo.help_question.SubmitAnswerResponse;
import com.rideshare.app.Server.session.SessionManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

//submiting help
public class HelpSubmitFragment extends Fragment {

    private static final int REQUEST_CODE = 1009;
    private FragmentHelpSubmitBinding binding;
    private String question = "", id = "", email="";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHelpSubmitBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            question = getArguments().getString("question").trim();
            email = getArguments().getString("email").trim();
            id = String.valueOf(getArguments().getInt("id"));
        }

        binding.txtTitle.setText(question);
        binding.tvMailHelp.setText(email);
        binding.tvPhoneHelp.setOnClickListener(e->{
            if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.CALL_PHONE},
                        REQUEST_CODE);

            } else {

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", binding.tvPhoneHelp.getText().toString(), null));
                startActivity(intent);

            }
        });
        binding.tvMailHelp.setOnClickListener(e->{
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto",binding.tvMailHelp.getText().toString(), null));

            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        });


        binding.btnSubmit.setOnClickListener(e->{
            if (CheckConnection.haveNetworkConnection(requireContext())) {
                if(binding.txtDescription.getText().toString().isEmpty())
                {
                    Toast.makeText(requireContext(), "Please write a short description", Toast.LENGTH_SHORT).show();
                }
                else {
                    submitAnswer(id, binding.txtDescription.getText().toString());
                }
            } else {
                Toast.makeText(getActivity(), getString(R.string.network), Toast.LENGTH_LONG).show();
            }
        });


        return view;
    }

//submiting Answers
    public void submitAnswer(String id, String answer) {
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "", "Submitting Query", false, false);
        ApiNetworkCall apiService = ApiClient.getApiService();
        Map<String, String> params = new HashMap<>();
        params.put("question_id", id);
        params.put("answer", answer);
        Call<SubmitAnswerResponse> call = apiService.submitAnswer("Bearer " + SessionManager.getKEY(), params);
        call.enqueue(new Callback<SubmitAnswerResponse>() {
            @Override
            public void onResponse(Call<SubmitAnswerResponse> call, retrofit2.Response<SubmitAnswerResponse> response) {
                loading.cancel();
                SubmitAnswerResponse jsonResponse = response.body();
                if (jsonResponse != null) {

                    if (jsonResponse.getStatus()) {
                        Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        ((HomeActivity)requireActivity()).changeFragment(new HelpFragment(),"Help");
                    } else {
                        Toast.makeText(requireContext(), jsonResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }


            @Override
            public void onFailure(Call<SubmitAnswerResponse> call, Throwable t) {
                loading.cancel();
                Log.d("Failed", "RetrofitFailed");
            }
        });
    }
}