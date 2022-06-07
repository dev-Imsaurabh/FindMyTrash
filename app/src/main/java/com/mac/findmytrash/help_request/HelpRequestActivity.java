package com.mac.findmytrash.help_request;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mac.findmytrash.FCM_experiment.Model.NotificationData;
import com.mac.findmytrash.FCM_experiment.Model.PushNotification;
import com.mac.findmytrash.FCM_experiment.SendNotification;
import com.mac.findmytrash.R;
import com.mac.findmytrash.map_location.PrefConfig;
import com.mac.findmytrash.map_location.TrashNearMeActivity;
import com.mac.findmytrash.submit_details.SubmitModel;
import com.skyfishjy.library.RippleBackground;

public class HelpRequestActivity extends AppCompatActivity {
    private RippleBackground rippleBackground;
    private CardView close_request_btn;
    private TextView timer_txt;
    private ProgressBar req_progressBar;
    private int time = 0;
    private int maxTime = 180;
    private CardView portalCard;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private  boolean no_help=false;
    private boolean  time_out =false;
    Handler handler;

    Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_request);
        PrefConfig.SetPref(HelpRequestActivity.this,"requestPref","request",String.valueOf(System.currentTimeMillis()));
        hideStatusBar();
        rippleBackground = findViewById(R.id.ripple_effect);
        close_request_btn = findViewById(R.id.close_req_btn);
        timer_txt = findViewById(R.id.text_timer);
        req_progressBar = findViewById(R.id.req_progressBar);
        portalCard = findViewById(R.id.portalCard);
        req_progressBar.setMax(maxTime);
        req_progressBar.setProgress(time);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        rippleBackground.startRippleAnimation();

        OpenDrawer();
        ClickOnCloseButton();


    }

    private void ClickOnCloseButton() {
        close_request_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             ShowDialog();
            }
        });
    }

    private void UpdateProgressBarAndTimer() {


        handler = new Handler();

        runnable = new Runnable() {
            public void run() {
                time++;
                if (time <= maxTime) {
                    req_progressBar.setProgress(time);
                    timer_txt.setText("Portal will open for : " + String.valueOf(maxTime - time) + " sec more");
                    String rPref = PrefConfig.GetPref(HelpRequestActivity.this,"rPref","rece");

                    if(!no_help){

                        if(!rPref.equals("error")){
                            MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.discord);
                            mp.start();
                            no_help=true;
                            handler.removeCallbacksAndMessages(null);
                            String topic ="/topics/"+ PrefConfig.GetPref(HelpRequestActivity.this,"pinCode","code");
//                            String topic ="/topics/"+ "250001";

                            PushNotification pushNotification = new PushNotification(new NotificationData("cancel","cancel"+user.getUid()),topic);
                            SendNotification.Send(pushNotification,HelpRequestActivity.this);
                            Toast.makeText(HelpRequestActivity.this, "removed", Toast.LENGTH_SHORT).show();
                            DrawerClose();
                            ViewLocationDialog(rPref);



                        }

                    }



                    handler.postDelayed(this, 1000);
                } else {
                    CallNoUserFoundFunction();
                }


            }
        };
        handler.post(runnable);


    }

    private void ViewLocationDialog(String rPref) {
        time_out=true;
        String[] splitMessageByComma = rPref.split(",");

            String latitude = splitMessageByComma[0];
            String longitude = splitMessageByComma[1];
            String name = splitMessageByComma[2];
            String displayMessage = splitMessageByComma[3];







        final Dialog dialog = new Dialog(HelpRequestActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.view_on_map_dialog);

        FrameLayout view_btn = (FrameLayout) dialog.findViewById(R.id.view_btn);
        FrameLayout ignore_btn = (FrameLayout) dialog.findViewById(R.id.ignore_btn);
        TextView rece_username = (TextView) dialog.findViewById(R.id.rece_username);
        TextView et_message = (TextView) dialog.findViewById(R.id.et_message);

        rece_username.setText(name);
        et_message.setText(displayMessage);


        view_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              ShowViaDialog(latitude,longitude);

            }
        });

        ignore_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });


        dialog.show();



    }

    private void CallNoUserFoundFunction() {
        time_out=true;
        handler.removeCallbacksAndMessages(null);
        String topic ="/topics/"+ PrefConfig.GetPref(HelpRequestActivity.this,"pinCode","code");
//                            String topic ="/topics/"+ "250001";

        PushNotification pushNotification = new PushNotification(new NotificationData("cancel","cancel"+user.getUid()),topic);
        SendNotification.Send(pushNotification,HelpRequestActivity.this);
        DrawerClose();
        TimoutDialog();



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


    private void OpenDrawer() {
        TranslateAnimation animate = new TranslateAnimation(0, 0, portalCard.getHeight() + 400, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        portalCard.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {

                UpdateProgressBarAndTimer();


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);

    }


    private void DrawerClose() {

        TranslateAnimation animate = new TranslateAnimation(0, 0, 0, portalCard.getHeight() - 50);
        animate.setDuration(500);
        animate.setFillAfter(true);
        portalCard.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                portalCard.setVisibility(View.GONE);
                close_request_btn.setEnabled(false);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }



    private void ShowDialog() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
        mp.start();

        final Dialog dialog = new Dialog(HelpRequestActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.warning_dialog);

        CardView close_portal_btn = (CardView) dialog.findViewById(R.id.close_portal_btn);
        CardView close_btn = (CardView) dialog.findViewById(R.id.close_btn);


        close_portal_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String topic ="/topics/"+ PrefConfig.GetPref(HelpRequestActivity.this,"pinCode","code");
//                String topic ="/topics/"+ "250001";

                PushNotification pushNotification = new PushNotification(new NotificationData("cancel","cancel"+user.getUid()),topic);
                SendNotification.Send(pushNotification,HelpRequestActivity.this);
                dialog.dismiss();
                finish();
            }
        });

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });


        dialog.show();





    }

    @Override
    public void onBackPressed() {
        if(time_out){

         TimoutDialog();

        }else{
            ShowDialog();

        }

    }

    private void TimoutDialog() {
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.error_sound);
        mp.start();
        final Dialog dialog = new Dialog(HelpRequestActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.time_out_dialog);

        CardView time_out_btn = (CardView) dialog.findViewById(R.id.close_timeout_btn);
        time_out_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();

            }
        });
        dialog.show();
    }


    private void ShowViaDialog(String latitude,String longitude) {

        final Dialog dialog = new Dialog(HelpRequestActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.ask_nav_dialog);

        CardView walking_btn = (CardView) dialog.findViewById(R.id.walking_btn);
        CardView driving_btn = (CardView) dialog.findViewById(R.id.driving_btn);


        walking_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," +longitude+"&mode=w");
                //String url = "https://www.google.com/maps/dir/?api=1&destination=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong() + "&travelmode=driving";

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                dialog.dismiss();
                finish();
            }
        });

        driving_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude+ "," + longitude);

                //String url = "https://www.google.com/maps/dir/?api=1&destination=" + submitModel.getSubmitLat() + "," + submitModel.getSubmitLong() + "&travelmode=driving";

                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();





    }

}

