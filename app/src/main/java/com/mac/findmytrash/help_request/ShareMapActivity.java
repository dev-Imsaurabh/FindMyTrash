package com.mac.findmytrash.help_request;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessaging;
import com.mac.findmytrash.MainActivity;
import com.mac.findmytrash.R;
import com.mac.findmytrash.map_location.Map_Fragment;
import com.mac.findmytrash.map_location.PrefConfig;

public class ShareMapActivity extends AppCompatActivity {

    private String latlang ;
    private String uid,name;
    private Handler handler;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_map);
        PrefConfig.SetPref(ShareMapActivity.this, "markerPref", "marker","1");

        CheckInRealTime();


        latlang=getIntent().getStringExtra("latlang");
        uid=getIntent().getStringExtra("uid");
        name=getIntent().getStringExtra("name");

        String[] splitLatlang = latlang.split("-");

        String latitude = splitLatlang[0];
        String longitude = splitLatlang[1];

        openMapFragment(uid,latitude,longitude);
    }

    private void CheckInRealTime() {

       handler = new Handler();

        final Runnable runnable = new Runnable() {
            public void run() {
                if(!PrefConfig.GetPref(ShareMapActivity.this, "tempTopic", "topic").equals(uid)){
                    handler.removeCallbacksAndMessages(null);
                    MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
                    mp.start();

                    Dialog dialog1 = new Dialog(ShareMapActivity.this);
                    dialog1.setCancelable(false);
                    dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog1.setContentView(R.layout.session_expired_warning_dialog);


                    CardView gobackBtn = (CardView) dialog1.findViewById(R.id.go_back_btn);
                    gobackBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            dialog1.dismiss();
                            finish();
                        }
                    });

                    dialog1.show();




                }else{
                    handler.postDelayed(this, 1000);

                }


            }
        };
        handler.post(runnable);
    }


    private void openMapFragment(String uid, String latitude, String longitude) {
        Bundle bundle = new Bundle();
        bundle.putString("uid",uid);
        bundle.putString("cus_latitude",latitude);
        bundle.putString("cus_longitude",longitude);
        bundle.putString("share","1");
        bundle.putString("name",name);
        Map_Fragment fragment = new Map_Fragment();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();


    }

    @Override
    public void onBackPressed() {
        showDialog();
    }


    private  void showDialog(){
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
        mp.start();
        final Dialog dialog = new Dialog(ShareMapActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.close_warning_dialog);

        CardView yes_back_btn = (CardView) dialog.findViewById(R.id.yes_back_btn);
        CardView no_ba_btn = (CardView) dialog.findViewById(R.id.no_back_btn);

        yes_back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

                if(PrefConfig.GetPref(ShareMapActivity.this, "tempTopic", "topic").equals(uid)){
                    SharedPreferences preferences = getSharedPreferences("helpPref",MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.commit();
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("cancel"+uid);
                    PrefConfig.SetPref(ShareMapActivity.this,"userHelp","user","true");
                    finish();
                }else{
                    finish();
                }



            }
        });

        no_ba_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }
}