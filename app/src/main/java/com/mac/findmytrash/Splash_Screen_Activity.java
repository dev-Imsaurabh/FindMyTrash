package com.mac.findmytrash;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.mac.findmytrash.Login.PhoneNumber_Activity;
import com.skyfishjy.library.RippleBackground;

import de.hdodenhof.circleimageview.CircleImageView;


public class Splash_Screen_Activity extends AppCompatActivity {


    private static int time = 1500;
    private ImageView india_map,trash;
    private CircleImageView pin_location_image;
    private Animation fade;
    private RippleBackground rippleBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_splash_screen);
        pin_location_image=findViewById(R.id.location_pin);
//        india_map=findViewById(R.id.india_map);
//        trash=findViewById(R.id.trash);
        rippleBackground=findViewById(R.id.ripple_effect);
        rippleBackground.startRippleAnimation();


//        this.getWindow().getDecorView().setSystemUiVisibility(
//                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        hideStatusBar();
//        Splash_image = findViewById(R.id.splash_image);

        fade = AnimationUtils.loadAnimation(Splash_Screen_Activity.this, R.anim.fade_in);
        pin_location_image.setAnimation(fade);
//        india_map.setAnimation(fade);
//        trash.setAnimation(fade);
//        Splash_image.setAnimation(fade);
        getSplash();

    }

    private void getSplash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Splash_Screen_Activity.this, PhoneNumber_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in,R.anim.slide_up);
                finish();

            }
        }, time);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
}