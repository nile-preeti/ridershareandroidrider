package com.rideshare.app.custom;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rideshare.app.fragement.HomeFragment;

public class GetDumaWorkManager extends Worker {

    HomeFragment fragment;
    public GetDumaWorkManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {

            fragment = new HomeFragment();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    String rideID = getInputData().getString("ride_id");
                    fragment.getRideStatus(rideID);
                    Log.e("WorkManager", "Success");
                }
            });
            return Result.success();
        }
        catch (Exception ex)
        {
            Log.e("WorkManager", "Failed: "+ex.getMessage());
            return  Result.failure();
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();

    }
}
