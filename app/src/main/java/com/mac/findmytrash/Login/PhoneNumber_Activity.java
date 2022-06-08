package com.mac.findmytrash.Login;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.mac.findmytrash.MainActivity;
import com.mac.findmytrash.R;


import java.util.concurrent.TimeUnit;

public class PhoneNumber_Activity extends AppCompatActivity {
    private EditText countryCodeBox, phoneNumberBox;
    private CardView signUpBtn,login_btn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private int authToggle=0;
    private int state =0;
    private TextView  info;
    private TextView login_info;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        hideStatusBar();
        initialisingWidgets();
        ClickOnSignupBtn();
        ClickOnLoginBtn();
    }

    private void ClickOnLoginBtn() {
    login_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(state==0){
                login_info.setText("Signup");
                info.setText("Find My bin\nLogin");
                state=1;
                authToggle=1;
                showAnimation();
            }else{
                login_info.setText("Login");
                info.setText("Find My bin\nSignUp");

                state=0;
                authToggle=0;
                showAnimation();
            }
        }
    });
    }

    private void ClickOnSignupBtn() {
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();

            }
        });

    }


    private void initialisingWidgets() {
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        countryCodeBox = findViewById(R.id.countryCodeBox);
        phoneNumberBox = findViewById(R.id.phoneNumberBox);
        signUpBtn = findViewById(R.id.signup_btn);
        login_btn=findViewById(R.id.login_btn);
        view=findViewById(R.id.view);
        progressBar = findViewById(R.id.progressBar);
        login_info=findViewById(R.id.login_info);
        info=findViewById(R.id.info);
    }

    private void checkValidation() {
        if (!phoneNumberBox.getText().toString().isEmpty() && !countryCodeBox.getText().toString().isEmpty()) {
            if (phoneNumberBox.getText().toString().trim().length() == 10 && countryCodeBox.getText().toString().trim().length() == 3) {

                progressBar.setVisibility(View.VISIBLE);
                signUpBtn.setVisibility(View.GONE);
                OtpOProcessStart();

            } else {
                Toast.makeText(PhoneNumber_Activity.this, "Please check your phone number and country code !", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(PhoneNumber_Activity.this, "Please enter phone number and country code !", Toast.LENGTH_SHORT).show();
        }

    }

    private void OtpOProcessStart() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCodeBox.getText().toString().trim() + phoneNumberBox.getText().toString().trim(), 60, TimeUnit.SECONDS, PhoneNumber_Activity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                progressBar.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.VISIBLE);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                progressBar.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.VISIBLE);

                Toast.makeText(PhoneNumber_Activity.this, "error: " + e.getMessage(), Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onCodeSent(@NonNull String backEndOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(backEndOtp, forceResendingToken);

                progressBar.setVisibility(View.GONE);
                signUpBtn.setVisibility(View.VISIBLE);
                Intent intent = new Intent(PhoneNumber_Activity.this, OTP_Verification_Activity.class);
                intent.putExtra("phoneNumber", phoneNumberBox.getText().toString());
                intent.putExtra("backEndOtp", backEndOtp);
                intent.putExtra("countryCode", countryCodeBox.getText().toString());
                intent.putExtra("authCode",String.valueOf(authToggle));
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onStart() {

        if(user!=null){

            Intent intent = new Intent(PhoneNumber_Activity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        super.onStart();
    }


    public void hideStatusBar() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }

    private void showAnimation(){

        view.animate().alpha(1.0f);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                view.animate().alpha(0.0f);

            }
        },200);

    }
}