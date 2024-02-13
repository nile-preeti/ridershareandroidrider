package com.rideshare.app.acitivities;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.rideshare.app.tracker.TrackingService;

//tracking
public class TrackingActivity extends Activity {

    private static final int PERMISSIONS_REQUEST = 1;
    String ride_id="",user_email="",user_name="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        ride_id = intent.getStringExtra("ride_id");
        user_email = intent.getStringExtra("user_email");
        user_name = intent.getStringExtra("user_name");

        Log.d("ride_id", "------------"+ride_id);
        Log.d("user_email", "-------"+user_email);
        Log.d("user_name", "-------"+user_name);

        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show();
            finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }
    }

    //starting tracking service
    private void startTrackerService() {
        Intent serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.putExtra("ride_id", ride_id);
        serviceIntent.putExtra("user_email", user_email);
        serviceIntent.putExtra("user_name", user_name);
        startService(serviceIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        } else {
            finish();
        }
    }
}
