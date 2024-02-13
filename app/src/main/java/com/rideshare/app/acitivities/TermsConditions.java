package com.rideshare.app.acitivities;

import static com.rideshare.app.Server.Server.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.widget.Toolbar;

import com.rideshare.app.R;
import com.rideshare.app.custom.SetCustomFont;

public class TermsConditions extends AppCompatActivity {
    final static String URL = BASE_URL + "app-terms-condition";
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Terms & Conditions");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        WebView webView = (WebView) findViewById(R.id.webview_terms_conditions);
        // Enable Javascript
        WebSettings webSettings = webView.getSettings();

        webView.setWebViewClient(new WebViewClient());
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(false);
        webView.setWebViewClient(new WebClient());
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.loadUrl(URL);
        SetCustomFont font = new SetCustomFont();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(TermsConditions.this, RegisterActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            // Do something with the event here
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (url.startsWith("mailto:")) {
                    String[] emailsend = url.split(":");
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{emailsend[1]});
                    intent.setType("message/rfc822");
                    startActivity(Intent.createChooser(intent, "Choose an Email client :"));
                }

                if (url.startsWith("http")) {
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
    }
}