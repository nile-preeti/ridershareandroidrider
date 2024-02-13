package com.rideshare.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rideshare.app.R;
import com.rideshare.app.databinding.SpendDetailsRecyclerLayoutBinding;
import com.rideshare.app.pojo.PendingRequestPojo;

import com.rideshare.app.pojo.spend.PendingPojo;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SpendAdapter extends RecyclerView.Adapter<SpendAdapter.SpendViewHolder> {

    private static final String SIGNATURE = "Cmyf0WdeV87XMRJS8rR9ZbeDbvI=";
    private String STATIC_MAP_API_ENDPOINT = "https://maps.googleapis.com/maps/api/staticmap?";
    private Context context;
    private Bitmap bitmap;
    private List<PendingPojo> pendingRequestPojos;

    public SpendAdapter(Context context, List<PendingPojo> pendingRequestPojos) {
        this.context = context;
        this.pendingRequestPojos = pendingRequestPojos;
    }

    @NonNull
    @Override
    public SpendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpendViewHolder(SpendDetailsRecyclerLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpendViewHolder holder, int position) {
        holder.binding.txtFrom.setText(pendingRequestPojos.get(holder.getAdapterPosition()).getPickupAdress());
        holder.binding.txtTo.setText(pendingRequestPojos.get(holder.getAdapterPosition()).getDropAddress());
        holder.binding.txtAmount.setText(NumberFormat.getCurrencyInstance(Locale.US).format(Double.parseDouble(pendingRequestPojos.get(holder.getAdapterPosition()).getAmount())));
        holder.binding.txtRideDetails.setText(String.format("%s miles.", pendingRequestPojos.get(holder.getAdapterPosition()).getDistance()));
        holder.binding.txtTime.setText(pendingRequestPojos.get(holder.getBindingAdapterPosition()).getTime());

//        SimpleDateFormat format = new SimpleDateFormat("MMM dd,yyyy  hh:mm a");
//        try {
//            SimpleDateFormat toFullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date fullDate = toFullDate.parse(pendingRequestPojos.get(holder.getAdapterPosition()).getTime());
//            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss a");
//            String shortTime = time.format(fullDate);
//            SimpleDateFormat date = new SimpleDateFormat("MMM dd, yyyy");
//
//            String shortTimedate = date.format(fullDate);
//            holder.binding.txtTime.setText(String.format("%s, %s", shortTimedate, shortTime));
//        } catch (ParseException e) {
//        }
        /*Comment Start*/
//        try {
//            double pickLat = Double.parseDouble(pendingRequestPojos.get(holder.getAdapterPosition()).getPickupLat());
//            double pickLong = Double.parseDouble(pendingRequestPojos.get(holder.getAdapterPosition()).getPickupLong());
//            double dropLat = Double.parseDouble(pendingRequestPojos.get(holder.getAdapterPosition()).getDropLat());
//            double dropLong = Double.parseDouble(pendingRequestPojos.get(holder.getAdapterPosition()).getDropLong());
//
//
//            loadStaticImage(holder.binding.mapImg, pendingRequestPojos.get(holder.getAdapterPosition()).getPikupLocation(), pendingRequestPojos.get(holder.getAdapterPosition()).getDropLocatoin(), pickLat, pickLong, dropLat, dropLong);
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
//        }
        /*Comment End*/
    }
    @Override
    public int getItemCount() {
        return pendingRequestPojos.size();
    }

    class SpendViewHolder extends RecyclerView.ViewHolder {
        private SpendDetailsRecyclerLayoutBinding binding;
        public SpendViewHolder(SpendDetailsRecyclerLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setData(List<PendingPojo> pendingRequestPojos1) {
        pendingRequestPojos = pendingRequestPojos1;
        notifyDataSetChanged();
    }

    public void loadStaticImage(ImageView imageView, String source, String destination, double pickupLat, double pickupLong, double dropLat, double dropLong) throws UnsupportedEncodingException {

        source = URLEncoder.encode(source, "UTF-8");
        destination = URLEncoder.encode(destination, "UTF-8");
//        String temp = STATIC_MAP_API_ENDPOINT + path + "&markers=" + marker_me + "&markers=" + marker_dest + "&key="+R.string.google_android_map_api_key;
        String temp = STATIC_MAP_API_ENDPOINT + "&style=feature:road.highway%7Celement:geometry%7Ccolor:0x255019%7Cvisibility:off&style=feature:landscape%7Celement:geometry.fill%7Ccolor:0xD8E9B8&path=color:0x0000ff%7Cweight:5%7C" + pickupLat + "," + pickupLong + "%7C" + dropLat + "," + dropLong + "&center=" + source + "&size=600x300&maptype=roadmap&markers=size:mid%7Ccolor:red%7Clabel:S%7C" + pickupLat + "," + pickupLong + "&markers=size:mid%7Ccolor:green%7Clabel:D%7C" + dropLat + "," + dropLong + "&key=" + context.getString(R.string.google_android_map_api_key_static) + "&map_id=" + context.getString(R.string.map_id);
        Picasso.with(context).load(temp).error(R.drawable.ic_map_error).into(imageView);
    }
}
