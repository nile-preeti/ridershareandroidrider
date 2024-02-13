package com.rideshare.app.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.rideshare.app.R;
import com.rideshare.app.pojo.CountryCode;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<CountryCode> {

    private LayoutInflater inflater;
    private List<CountryCode> countryCodeList;

    public CustomSpinnerAdapter(Context context, List<CountryCode> countryCodeList) {
        super(context, 0, countryCodeList);
        this.countryCodeList = countryCodeList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_country_code, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textCountryName);
        TextView textViewCode = convertView.findViewById(R.id.textCountryCode);
        TextView textViewPhoneCode = convertView.findViewById(R.id.textCountryPhoneCode);

        CountryCode countryCode = countryCodeList.get(position);

        textViewName.setText(countryCode.getName());
        textViewCode.setText(countryCode.getCode());
        textViewPhoneCode.setText(countryCode.getPhone_code());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_country_code, parent, false);
        }

        TextView textViewName = convertView.findViewById(R.id.textCountryName);
        TextView textViewCode = convertView.findViewById(R.id.textCountryCode);
        TextView textViewPhoneCode = convertView.findViewById(R.id.textCountryPhoneCode);


        CountryCode countryCode = countryCodeList.get(position);

        textViewName.setText(countryCode.getName());
        textViewCode.setText(countryCode.getCode());
        textViewPhoneCode.setText(countryCode.getPhone_code());

        return convertView;
    }
}
