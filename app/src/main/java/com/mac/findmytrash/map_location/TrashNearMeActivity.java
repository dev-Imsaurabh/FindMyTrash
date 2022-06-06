package com.mac.findmytrash.map_location;

import static com.mac.findmytrash.MainActivity.AVERAGE_RADIUS_OF_EARTH_KM;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.findmytrash.FCM_experiment.Model.NotificationData;
import com.mac.findmytrash.FCM_experiment.Model.PushNotification;
import com.mac.findmytrash.FCM_experiment.SendNotification;
import com.mac.findmytrash.MainActivity;
import com.mac.findmytrash.R;
import com.mac.findmytrash.help_request.HelpRequestActivity;
import com.mac.findmytrash.submit_details.SubmitAdapter;
import com.mac.findmytrash.submit_details.SubmitModel;

import java.util.ArrayList;
import java.util.Collections;

public class TrashNearMeActivity extends AppCompatActivity {

    private RecyclerView rec_location;
    private SubmitAdapter adapter;
    private ArrayList<SubmitModel> list;
    private DatabaseReference reference;
    private TextView no_info;
    private LottieAnimationView animationView;
    private CardView send_help_request_btn,view_onMapBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash_near_me);
        hideStatusBar();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();


        reference = FirebaseDatabase.getInstance().getReference();
        no_info=findViewById(R.id.no_info);


        rec_location = findViewById(R.id.rec_nearByLocations);
        animationView = findViewById(R.id.animationView);
        send_help_request_btn=findViewById(R.id.send_help_request_btn);
        view_onMapBtn=findViewById(R.id.view_on_map);
        send_help_request_btn=findViewById(R.id.send_help_request_btn);

        rec_location.setLayoutManager(new LinearLayoutManager(this));
        rec_location.setHasFixedSize(true);

        ClickOnSendHelpRequestBtn();


        GetData();

        view_onMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefConfig.SetPref(TrashNearMeActivity.this,"trashMap","map","1");
                Intent intent = new Intent(TrashNearMeActivity.this, MainActivity.class);
                intent.putExtra("showMap","1");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_down,R.anim.fade_out);

            }
        });

    }

    private void ClickOnSendHelpRequestBtn() {

        send_help_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialog();
            }
        });


    }

    private void GetData() {
        list = new ArrayList<>();
        reference.child("Area").child(PrefConfig.GetPref(TrashNearMeActivity.this, "pinCode", "code")).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        SubmitModel data = snapshot1.getValue(SubmitModel.class);
                        list.add(data);


                        for (int i = 0; i < list.size(); i++) {
                            for (int j = i + 1; j < list.size(); j++) {

                                double location1 = calculateDistanceInKilometer(Double.parseDouble(list.get(i).getSubmitLat()), Double.parseDouble(list.get(i).getSubmitLong()), Double.parseDouble(PrefConfig.GetPref(TrashNearMeActivity.this, "latitudePref", "latitude")), Double.parseDouble(PrefConfig.GetPref(TrashNearMeActivity.this, "longitudePref", "longitude")));
                                double location2 = calculateDistanceInKilometer(Double.parseDouble(list.get(j).getSubmitLat()), Double.parseDouble(list.get(j).getSubmitLong()), Double.parseDouble(PrefConfig.GetPref(TrashNearMeActivity.this, "latitudePref", "latitude")), Double.parseDouble(PrefConfig.GetPref(TrashNearMeActivity.this, "longitudePref", "longitude")));

                                if (location1 > location2) {

                                    Collections.<SubmitModel>swap(list,i,j);
                                }


                            }
                        }


                    }


                    adapter = new SubmitAdapter(TrashNearMeActivity.this, list);
                    adapter.notifyDataSetChanged();
                    rec_location.setAdapter(adapter);
                    animationView.setVisibility(View.GONE);


                }else {
                    animationView.setVisibility(View.GONE);
                    no_info.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                animationView.setVisibility(View.GONE);

            }
        });
    }


    public void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
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


    private void ShowDialog() {


        final Dialog dialog = new Dialog(TrashNearMeActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.send_help_dialog);

        CardView send_request_btn = (CardView) dialog.findViewById(R.id.send_request);
        TextView user_name = (TextView) dialog.findViewById(R.id.user_name);
        user_name.setText(PrefConfig.GetPref(TrashNearMeActivity.this,"userPref","username"));


        send_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name =PrefConfig.GetPref(TrashNearMeActivity.this,"userPref","username");
                String latitude = PrefConfig.GetPref(TrashNearMeActivity.this,"latitudePref","latitude");
//                String latitude = "29.017652732110413";
                String longitude = PrefConfig.GetPref(TrashNearMeActivity.this,"longitudePref","longitude");
//                String longitude = "77.7619469538331";
                String topic ="/topics/"+PrefConfig.GetPref(TrashNearMeActivity.this,"pinCode","code");
//                String topic ="/topics/"+"250001";
                String phoneNumber =user.getPhoneNumber();
                String timeStamp = String.valueOf(System.currentTimeMillis());
                String uid = user.getUid();
                String displayMessage ="Hey I am "+name+" need help for finding dustbin 🗑 in you area 😊";

                String message = name+","+displayMessage+","+phoneNumber+","+uid+","+timeStamp+","+latitude+"-"+longitude;


                PushNotification pushNotification = new PushNotification(new NotificationData("New request",message),topic);
                SendNotification.Send(pushNotification,TrashNearMeActivity.this);
               String rPref = PrefConfig.GetPref(TrashNearMeActivity.this,"rPref","rece");
//                Toast.makeText(TrashNearMeActivity.this, rPref, Toast.LENGTH_SHORT).show();
                SharedPreferences preferences = getSharedPreferences("rPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();


                Intent intent = new Intent(TrashNearMeActivity.this, HelpRequestActivity.class);
                startActivity(intent);

                dialog.dismiss();
            }
        });


        dialog.show();





    }


}
