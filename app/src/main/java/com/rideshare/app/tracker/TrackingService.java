package com.rideshare.app.tracker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rideshare.app.R;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;


import static com.rideshare.app.Server.session.SessionManager.getUserEmail;
import static com.rideshare.app.Server.session.SessionManager.initialize;

//Tracking service
public class TrackingService extends Service {

    private static final String TAG = TrackingService.class.getSimpleName();
    String userEmail = "";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            buildNotification();
        }

        // loginToFirebase();
    }

    //starting service
    public int onStartCommand(Intent intent, int flags, int startId) {
        initialize(getApplicationContext());
        String ride_id = intent.getStringExtra("ride_id");
        String user_email = intent.getStringExtra("user_email");
        String user_name = intent.getStringExtra("user_name");

        Log.d("ride_id", "----------" + ride_id);
        Log.d("user_email", "-------" + user_email);
        Log.d("user_email", "-------" + user_email);

        userEmail = getUserEmail();
        Log.d("email", userEmail);
        createuser(user_email, ride_id, user_name);
        return START_STICKY;
    }

    //building notification
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void buildNotification() {
        String stop = "stop";
        registerReceiver(stopReceiver, new IntentFilter(stop));
        PendingIntent broadcastIntent = PendingIntent.getBroadcast(
                this, 0, new Intent(stop), PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the persistent notification
        String NOTIFICATION_CHANNEL_ID = "com.icanstudioz.taxi";
        String channelName = "Ocory";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setSmallIcon(R.drawable.ridesharelogo)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    //Broadcast reciver
    protected BroadcastReceiver stopReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "received stop broadcast");
            // Stop the service when the notification is tapped
            unregisterReceiver(stopReceiver);
            stopSelf();
        }
    };

    //creating user
    private void createuser(String email, String rideId, String name) {
        String memail = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        Log.d("createFirebase", "Fire" + email);
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");
                    //loginToFirebase();
                    requestLocationUpdates(name, rideId);
                } else {
                    Log.d(TAG, "firebase auth failed");
                    loginToFirebase(email, rideId, name);
                }
            }
        });
    }

    //login firebase
    private void loginToFirebase(String email, String rideId, String name) {
        // Authenticate with Firebase, and request location updates
        // String email = getString(R.string.firebase_email);
        String password = getString(R.string.firebase_password);
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "firebase auth success");
                    requestLocationUpdates(name, rideId);
                } else {
                    Log.d(TAG, "firebase auth failed");
                    Log.e(TAG, "onComplete: auth Failed=" + task.getException().getMessage());

                }
            }
        });
    }

    //request Location updates
    private void requestLocationUpdates(String name, String rideId) {
        LocationRequest request = new LocationRequest();
        request.setInterval(300 * 1000);
        request.setFastestInterval(300 * 1000);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
        final String path = name + rideId + "/" + rideId; //"rides/"+name + rideId + "/" + rideId;

        //  final String path = getString(R.string.firebase_path) + "/" + getString(R.string.transport_id);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            // Request location updates and when an update is
            // received, store the location in Firebase
            client.requestLocationUpdates(request, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path);
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        Log.d(TAG, "location update " + location);
                        ref.setValue(location);
                    }
                }
            }, null);
        }
    }


}

