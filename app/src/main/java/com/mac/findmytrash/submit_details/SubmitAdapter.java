package com.mac.findmytrash.submit_details;

import static com.mac.findmytrash.MainActivity.AVERAGE_RADIUS_OF_EARTH_KM;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.IOUtils;
import com.mac.findmytrash.MainActivity;
import com.mac.findmytrash.NearBy.NearByActivity;
import com.mac.findmytrash.R;
import com.mac.findmytrash.map_location.PrefConfig;
import com.mac.findmytrash.map_location.TrashNearMeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;


public class SubmitAdapter extends RecyclerView.Adapter<SubmitAdapter.SubmitViewAdapter> {
    private Context context;
    private ArrayList<SubmitModel> list;

    public SubmitAdapter(Context context, ArrayList<SubmitModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SubmitViewAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.near_by_layout_item, parent, false);
        return new SubmitViewAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubmitViewAdapter holder, int position) {

        SubmitModel submitModel = list.get(position);
        holder.name_txt.setText("Marked by:- " + submitModel.getSubmitName());
        holder.pin_txt.setText(submitModel.getSubmitPin());
        holder.near_txt.setText("Near " + submitModel.getSubmitAddress());
//        double dist = calculateDistanceInKilometer(Double.parseDouble(PrefConfig.GetPref(context, "latitudePref", "latitude")), Double.parseDouble(PrefConfig.GetPref(context, "longitudePref", "longitude")), Double.parseDouble(submitModel.getSubmitLat()), Double.parseDouble(submitModel.getSubmitLong()));



        float[] results = new float[1];
        double sourceLat = Double.parseDouble(PrefConfig.GetPref(context, "latitudePref", "latitude"));
        double sourceLong= Double.parseDouble(PrefConfig.GetPref(context, "longitudePref", "longitude"));
        Location.distanceBetween(sourceLat,sourceLong, Double.parseDouble(submitModel.getSubmitLat()), Double.parseDouble(submitModel.getSubmitLong()), results);

        double value = results[0]/1000;
        value = Double.parseDouble(new DecimalFormat("##.###").format(value));
        if (value < 1) {
            double finavalue = value * 1000;
            holder.distance_txt.setText("Away approx "+String.valueOf((String.valueOf((int)finavalue)+" m")));

        } else {
            double kmValue = value;

            kmValue = Double.parseDouble(new DecimalFormat("##.#").format(value));


            holder.distance_txt.setText("Away approx "+String.valueOf(kmValue)+ " km");


        }

//        holder.distance_txt.setText(String.valueOf(results[0]));

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDialog(submitModel);
//                Intent intent = new Intent(context, NearByActivity.class);
//                intent.putExtra("lat",submitModel.getSubmitLat());
//                intent.putExtra("lon",submitModel.getSubmitLong());
//                intent.putExtra("address",submitModel.getSubmitAddress());
//                context.startActivity(intent);
//                PerformOperation(submitModel.getSubmitAddress(),submitModel.getSubmitLat(),submitModel.getSubmitLong());
            }
        });


    }

    @Override
    public int getItemCount() {
        int size;
        if (list.size() > 20) {
            size = 20;
        } else {
            size = list.size();
        }

        return size;
    }


    public float calculateDistanceInKilometer(double userLat, double userLng,
                                              double venueLat, double venueLng) {

        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return (float) (AVERAGE_RADIUS_OF_EARTH_KM * c);
    }


    private void PerformOperation(String address, String lat, String lon) {
        String baseUrl = "https://www.google.com/maps/dir/";

        String removeComma = address.replaceAll(",", "");
        String removeDoubleSpace = removeComma.replaceAll("  ", " ");
        String addPlus = removeDoubleSpace.replaceAll(" ", "+");
        String finalAddress = addPlus.replaceAll("/", "%2F");

        LatLng source = new LatLng(Double.parseDouble(PrefConfig.GetPref(context, "latitudePref", "latitude")), Double.parseDouble(PrefConfig.GetPref(context, "longitudePref", "longitude")));


        String addSource = String.valueOf(source.latitude) + "," + String.valueOf(source.longitude) + "/";
        String addAddress = finalAddress + "/";
        String addDestination = "@" + lat + "," + lon;


        String finalUrl = baseUrl + addSource + addAddress + addDestination;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalUrl));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);


    }


    private void ShowDialog(SubmitModel submitModel) {

        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.ask_nav_dialog);

        CardView walking_btn = (CardView) dialog.findViewById(R.id.walking_btn);
        CardView driving_btn = (CardView) dialog.findViewById(R.id.driving_btn);


        walking_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri gmmIntentUri = Uri.parse("google.navigation:q=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong()+"&mode=w");
               //String url = "https://www.google.com/maps/dir/?api=1&destination=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong() + "&travelmode=driving";

               Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
               mapIntent.setPackage("com.google.android.apps.maps");
               context.startActivity(mapIntent);

               dialog.dismiss();
           }
       });

       driving_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri gmmIntentUri = Uri.parse("google.navigation:q=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong());
               //String url = "https://www.google.com/maps/dir/?api=1&destination=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong() + "&travelmode=driving";

               Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
               mapIntent.setPackage("com.google.android.apps.maps");
               context.startActivity(mapIntent);
               dialog.dismiss();
           }
       });

       dialog.show();





    }


    public class SubmitViewAdapter extends RecyclerView.ViewHolder {
        private TextView name_txt, pin_txt, near_txt, distance_txt;
        private CardView card;

        public SubmitViewAdapter(@NonNull View itemView) {
            super(itemView);
            name_txt = itemView.findViewById(R.id.name_txt);
            pin_txt = itemView.findViewById(R.id.pin_txt);
            near_txt = itemView.findViewById(R.id.near_txt);
            distance_txt = itemView.findViewById(R.id.distance_txt);
            card = itemView.findViewById(R.id.card);
        }
    }
}

