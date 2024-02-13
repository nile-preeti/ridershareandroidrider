package com.rideshare.app.custom;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CurrentCountry {
    public static String getCountryName(Context context, String longitude, String latitude)
    {
        List<Address> addresses=null;
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(latitude), Double.parseDouble( longitude), 1);
            String countryName = addresses.get(0).getCountryName();
            String countryCode = addresses.get(0).getCountryCode();
            return countryName;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
}
