package com.rideshare.app.custom;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.rideshare.app.R;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.List;

public class LocationListener implements View.OnClickListener {

    private Context context;
    private Activity activity;
    public LocationListener(Context context, Activity activity)
    {
        this.context= context;
        this.activity = activity;
    }
    @Override
    public void onClick(View v) {
        if (!Places.isInitialized()) {

            Places.initialize(context.getApplicationContext(),
                    context.getResources().getString(R.string.google_android_map_api_key));
        }
        List<Place.Field> fields =
                Arrays.asList(Place.Field.ID,
                        Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(context);

        activity.startActivityForResult(intent, Constants.PLACE_AUTOCOMPLETE_REQUEST_CODE);
    }
}
