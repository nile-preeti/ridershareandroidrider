package com.rideshare.app.fragement;

import static com.rideshare.app.Server.Server.BASE_URL;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rideshare.app.R;
import com.rideshare.app.acitivities.HomeActivity;
import com.rideshare.app.custom.SetCustomFont;


public class BookingCharges extends Fragment {
    private View rootView;
    final static String URL = BASE_URL+"app-booking-charges";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem help = menu.findItem(R.id.help);
        help.setVisible(false);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_booking_charges, container, false);
        setHasOptionsMenu(true);
        ((HomeActivity) getActivity()).fontToTitleBar("Booking Charges");
        WebView webView = rootView.findViewById(R.id.webview_privacy_policy);
        // Enable Javascript
        WebSettings webSettings = webView.getSettings();

        webView.setWebViewClient(new WebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebViewClient());
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl(URL);
        SetCustomFont font = new SetCustomFont();

        font.overrideFonts(requireContext(), rootView);

        return rootView;
    }
}