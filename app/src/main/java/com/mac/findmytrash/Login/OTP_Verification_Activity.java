package com.mac.findmytrash.Login;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mac.findmytrash.MainActivity;
import com.mac.findmytrash.R;
import com.mac.findmytrash.ReadOtp.OTP_Receiver;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OTP_Verification_Activity extends AppCompatActivity {
    private EditText input1, input2, input3, input4, input5, input6;
    private TextView intentPhoneNumber, resendOtpBtn;
    private String phoneNumber, backEndOtp, finalOtp, countryCode;
    private CardView proceedBtn;
    private ProgressBar progressBar;
    private DatabaseReference reference;
    private boolean available = false;
    private String key;
    private EditText editText;
    private OTP_Receiver otp_receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        requestPermissions();
        hideStatusBar();
        initialiseWidgets();
        checkValidation();
        autoOtpReceiver();
        autoNext();
        autoPrevious();
        resendOTP();

    }


    private void initialiseWidgets() {
        editText = findViewById(R.id.editText);
        input1 = findViewById(R.id.input1);
        input2 = findViewById(R.id.input2);
        input3 = findViewById(R.id.input3);
        input4 = findViewById(R.id.input4);
        input5 = findViewById(R.id.input5);
        input6 = findViewById(R.id.input6);
        progressBar = findViewById(R.id.progressBar);
        resendOtpBtn = findViewById(R.id.resendOtpBtn);
        intentPhoneNumber = findViewById(R.id.intentPhoneNumber);
        proceedBtn = findViewById(R.id.proceedBtn);
        resendOtpBtn = findViewById(R.id.resendOtpBtn);
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        backEndOtp = getIntent().getStringExtra("backEndOtp");
        countryCode = getIntent().getStringExtra("countryCode");
        intentPhoneNumber.setText(countryCode + "- " + phoneNumber);
        reference = FirebaseDatabase.getInstance().getReference().child("User");
    }

    private void checkValidation() {

        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!input1.getText().toString().isEmpty() && !input2.getText().toString().isEmpty() && !input3.getText().toString().isEmpty() && !input4.getText().toString().isEmpty() && !input5.getText().toString().isEmpty() && !input6.getText().toString().isEmpty()) {
                    finalOtp = input1.getText().toString() + input2.getText().toString() + input3.getText().toString() + input4.getText().toString() + input5.getText().toString() + input6.getText().toString();

                    CheckUserAvailable();


                } else {
                    Toast.makeText(OTP_Verification_Activity.this, "Enter all numbers", Toast.LENGTH_SHORT).show();


                }
            }
        });
    }


    private void autoPrevious() {

        input2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && input2.getText().toString().equals("")) {
                    input1.requestFocus();
                    input1.setText("");


                }
                return false;
            }
        });


        input3.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && input3.getText().toString().equals("")) {
                    input2.requestFocus();
                    input2.setText("");


                }
                return false;
            }
        });

        input4.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && input4.getText().toString().equals("")) {
                    input3.requestFocus();
                    input3.setText("");


                }
                return false;
            }
        });


        input5.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && input5.getText().toString().equals("")) {
                    input4.requestFocus();
                    input4.setText("");


                }
                return false;
            }
        });

        input6.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && input6.getText().toString().equals("")) {
                    input5.requestFocus();
                    input5.setText("");


                }
                return false;
            }
        });


    }


    private void autoNext() {
        input1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input2.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        input2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input3.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        input3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input4.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        input4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input5.requestFocus();
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
        input5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()) {
                    input6.requestFocus();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });


    }

    private void VerifyOTP() {

        progressBar.setVisibility(View.VISIBLE);
        proceedBtn.setVisibility(View.GONE);

        if (backEndOtp != null) {

//                        if(finalOtp.equals(backEndOtp)){
//
//                            Toast.makeText(OTP_Verification_Activity.this, "Verified", Toast.LENGTH_SHORT).show();
//
//
//                        }else{
//                            progressBar.setVisibility(View.VISIBLE);
//                            proceedBtn.setVisibility(View.GONE);
//                            Toast.makeText(OTP_Verification_Activity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
//                        }

            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(backEndOtp, finalOtp);

            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    proceedBtn.setVisibility(View.VISIBLE);

                    if (task.isSuccessful()) {
                        if (available) {
                            Intent intent = new Intent(OTP_Verification_Activity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("newUser", "0");
                            startActivity(intent);
                            overridePendingTransition(R.anim.fade_in, R.anim.slide_up);
                        } else {
                            Setdata();

                        }


                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    progressBar.setVisibility(View.GONE);
                    proceedBtn.setVisibility(View.VISIBLE);

                }
            });

        } else {
            progressBar.setVisibility(View.GONE);
            proceedBtn.setVisibility(View.VISIBLE);
            Toast.makeText(OTP_Verification_Activity.this, "OTP not received", Toast.LENGTH_SHORT).show();

        }



    }

    private void Setdata() {
        String finalKey = countryCode + phoneNumber;

        FirebaseAuth auth;
        FirebaseUser user;
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        HashMap hp = new HashMap();
        hp.put("name", "");
        hp.put("phone", user.getPhoneNumber());
        hp.put("uid", user.getUid());

        reference.child(finalKey.trim()).setValue(hp).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Intent intent = new Intent(OTP_Verification_Activity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("newUser", "1");
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.slide_up);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

    private void CheckUserAvailable() {

        String finalKey = countryCode + phoneNumber;
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        key = snapshot1.getKey();

                        if (key.equals(finalKey.trim())) {
                            available = true;
                        }
                    }

                }

                VerifyOTP();
//                if(!key.isEmpty()){
//                    VerifyOTP();
//                }else{
//                    CheckUserAvailable();
//                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void resendOTP() {
        resendOtpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(countryCode + phoneNumber, 60, TimeUnit.SECONDS, OTP_Verification_Activity.this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {

                    }

                    @Override
                    public void onCodeSent(@NonNull String newOTP, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(newOTP, forceResendingToken);
                        backEndOtp = newOTP;
                        Toast.makeText(OTP_Verification_Activity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    }
                });
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
//                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
    }


    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(OTP_Verification_Activity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(OTP_Verification_Activity.this, new String[]{
                    Manifest.permission.RECEIVE_SMS
            }, 100);
        }


    }


    private void autoOtpReceiver() {
        otp_receiver = new OTP_Receiver();
        this.registerReceiver(otp_receiver, new IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION));
        otp_receiver.initListener(new OTP_Receiver.OtpReceiverListener() {
            @Override
            public void onOtpSuccess(String otp) {
                int o1 = Character.getNumericValue(otp.charAt(0));
                int o2 = Character.getNumericValue(otp.charAt(1));
                int o3 = Character.getNumericValue(otp.charAt(2));
                int o4 = Character.getNumericValue(otp.charAt(3));
                int o5 = Character.getNumericValue(otp.charAt(4));
                int o6 = Character.getNumericValue(otp.charAt(5));

                input1.setText(String.valueOf(o1));
                input2.setText(String.valueOf(o2));
                input3.setText(String.valueOf(o3));
                input4.setText(String.valueOf(o4));
                input5.setText(String.valueOf(o5));
                input6.setText(String.valueOf(o6));
            }

            @Override
            public void onOtpTimeout() {
                Toast.makeText(OTP_Verification_Activity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (otp_receiver != null) {
            unregisterReceiver(otp_receiver);
        }
    }
}

