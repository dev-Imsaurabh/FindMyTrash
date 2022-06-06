package com.mac.findmytrash;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mac.findmytrash.FCM_experiment.Model.NotificationData;
import com.mac.findmytrash.FCM_experiment.Model.PushNotification;
import com.mac.findmytrash.FCM_experiment.SendNotification;
import com.mac.findmytrash.Login.PhoneNumber_Activity;
import com.mac.findmytrash.Touch.OnSwipeTouchListener;
import com.mac.findmytrash.help_request.HelpRequestActivity;
import com.mac.findmytrash.help_request.ShareMapActivity;
import com.mac.findmytrash.map_location.Map_Fragment;
import com.mac.findmytrash.map_location.PrefConfig;
import com.mac.findmytrash.map_location.TrashNearMeActivity;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;
    private CardView trash_near_me_btn;
    private TextView pinCode_txt;
    private RelativeLayout menu_layout;
    private ImageView close_drawer;
    private int drawerToggle = 0;
    private boolean mapToggle = false;
    private View open_drawer;
    private FrameLayout fragment;
    private CircleImageView show_map_btn, mark_trash_toggle;
    private TextView show_map_text;
    private ImageView logout_btn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String newUser;
    private DatabaseReference reference;
    private int marker_toggle = 0;
    private TextView user_name;
    private String showMapIntent;
    private View drawer_helper;
    private boolean isUserAvailable = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showMapIntent=getIntent().getStringExtra("showMap");

        PrefConfig.SetPref(MainActivity.this, "markerPref", "marker", "0");
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        hideStatusBar();
        requestPermissions();
        newUser = getIntent().getStringExtra("newUser");
        if (newUser != null) {
            if (newUser.equals("1")) {
                ShowDialog();
                PrefConfig.SetPref(MainActivity.this, "newUser", "new", "1");
            } else {
                if (PrefConfig.GetPref(MainActivity.this, "newUser", "new").equals("1")) {
                    ShowDialog();

                }
            }

        } else {

            if (PrefConfig.GetPref(MainActivity.this, "newUser", "new").equals("1")) {
                ShowDialog();

            }

        }

        if(user!=null){
            CheckHelpRequest();

        }

        trash_near_me_btn = findViewById(R.id.send_help_request_btn);
        pinCode_txt = findViewById(R.id.pinCode_txt);
        menu_layout = findViewById(R.id.menu_layout);
        close_drawer = findViewById(R.id.close_drawer);
        open_drawer = findViewById(R.id.open_drawer);
        fragment = findViewById(R.id.fragment);
        show_map_btn = findViewById(R.id.show_map_btn);
        logout_btn = findViewById(R.id.log_out_btn);
        show_map_text = findViewById(R.id.show_map_text);

        FirebaseMessaging.getInstance().subscribeToTopic(user.getUid());
        reference = FirebaseDatabase.getInstance().getReference();
        drawer_helper=findViewById(R.id.drawer_helper);
        user_name = findViewById(R.id.user_name);
        mark_trash_toggle = findViewById(R.id.mark_trash_toggle);
        if(showMapIntent!=null){
            fragment.setVisibility(View.VISIBLE);
            mapToggle = true;
            show_map_text.setText("Hide Map");


        }


        drawer_helper.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {

            }
            public void onSwipeLeft() {

                OpenDrawer();

            }

            public void onSwipeBottom() {

            }

        });




        menu_layout.setOnTouchListener(new OnSwipeTouchListener(this) {
            public void onSwipeTop() {
            }
            public void onSwipeRight() {
                DrawerClose();
            }
            public void onSwipeLeft() {
            }
            public void onSwipeBottom() {

            }

        });

        SetAnimation();


        ClickOnCloseDrawer();
        ClickOnOpenDrawer();
        ClickOnShowMApBtn();
        ClickOnTrashNearMeBtn();
        CLickOnLogoutBtn();
        ClickOnUserNameBtn();
        ClickOnMarkTrashBtn();


        if (PrefConfig.GetPref(MainActivity.this, "userPref", "username").equals("error")) {
            SetUserdetails();

        } else {
            user_name.setText(PrefConfig.GetPref(MainActivity.this, "userPref", "username"));
            FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+PrefConfig.GetPref(MainActivity.this,"userPref","userpin"));
        }


//        openMapFragment();


        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                DrawerClose();

            }
        }, 1000);


    }



    private void ClickOnMarkTrashBtn() {
        mark_trash_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (marker_toggle == 0) {
                    mark_trash_toggle.setBackground(getResources().getDrawable(R.drawable.green_circulatr_back));
                    marker_toggle = 1;
                    PrefConfig.SetPref(MainActivity.this, "markerPref", "marker", "1");
                    Toast.makeText(MainActivity.this, "Marker on", Toast.LENGTH_SHORT).show();
                } else {
                    mark_trash_toggle.setBackground(getResources().getDrawable(R.drawable.white_circulatr_back));
                    marker_toggle = 0;
                    Toast.makeText(MainActivity.this, "Marker off", Toast.LENGTH_SHORT).show();
                    PrefConfig.SetPref(MainActivity.this, "markerPref", "marker", "0");


                    Map_Fragment fragment = (Map_Fragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                    fragment.operateMapFromOutSide();

                }

            }
        });
    }

    private void ClickOnUserNameBtn() {
        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ShowDialog();

            }
        });
    }

    private void SetUserdetails() {

        if (user != null) {

            reference.child("User").child(user.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {


                    String name = snapshot.child("name").getValue(String.class);
                    String pinCode = snapshot.child("pinCode").getValue(String.class);
                    String uid = snapshot.child("uid").getValue(String.class);


                    if (name.equals("")) {
                        user_name.setText(user.getPhoneNumber());
                    } else {
                        user_name.setText(name);
                        FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+pinCode);
                        PrefConfig.SetPref(MainActivity.this, "userPref", "username", name);
                        PrefConfig.SetPref(MainActivity.this, "userPref", "userpin", pinCode);
                        PrefConfig.SetPref(MainActivity.this, "userPref", "useruid", uid);
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        } else {

        }


    }

    private void SetCode() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                pinCode_txt.setText(PrefConfig.GetPref(MainActivity.this, "pinCode", "code"));

            }
        }, 2000);
    }

    private void ShowDialog() {


        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.necessary_field_dialog_layout);

        EditText et_name = (EditText) dialog.findViewById(R.id.et_name);
        EditText et_pinCode = (EditText) dialog.findViewById(R.id.et_pinCode);

        if (!PrefConfig.GetPref(MainActivity.this, "userPref", "username").equals("error")) {
            et_name.setText(PrefConfig.GetPref(MainActivity.this, "userPref", "username"));
            et_pinCode.setText(PrefConfig.GetPref(MainActivity.this, "userPref", "userpin"));
            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/"+PrefConfig.GetPref(MainActivity.this, "userPref", "userpin"));
        }

        CardView proceedBtn = (CardView) dialog.findViewById(R.id.proceed_btn);
        proceedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reference.child("User").child(user.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {


                            if (et_name.getText().toString().isEmpty()) {
                                et_name.setError("Required Field");
                                et_name.requestFocus();

                            } else if (et_pinCode.getText().toString().isEmpty()) {
                                et_pinCode.setError("Required Field");
                                et_pinCode.requestFocus();

                            } else if (et_pinCode.getText().length() < 6) {
                                et_pinCode.setError("PinCode must be of 6 digits");
                                et_pinCode.requestFocus();

                            } else {

                                HashMap hp = new HashMap();
                                hp.put("name", et_name.getText().toString().trim());
                                hp.put("pinCode", et_pinCode.getText().toString().trim());

                                reference.child("User").child(user.getPhoneNumber()).updateChildren(hp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        PrefConfig.SetPref(MainActivity.this, "newUser", "new", "0");
                                        String pin = PrefConfig.GetPref(MainActivity.this, "userPref", "userpin");
                                        if (pin.equals("error")) {

                                            HashMap hp = new HashMap();
                                            hp.put("pin", et_pinCode.getText().toString());
                                            hp.put("phone", user.getPhoneNumber());

                                            reference.child("PinCodes").child(et_pinCode.getText().toString()).child(user.getPhoneNumber()).setValue(hp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    SetUserdetails();

//                                                    Toast.makeText(MainActivity.this, "Done", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();

                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });

                                        } else {

                                            HashMap hp = new HashMap();
                                            hp.put("pin", et_pinCode.getText().toString());
                                            hp.put("phone", user.getPhoneNumber());

                                            reference.child("PinCodes").child(pin).child(user.getPhoneNumber()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    reference.child("PinCodes").child(et_pinCode.getText().toString()).child(user.getPhoneNumber()).setValue(hp).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {


                                                            SetUserdetails();
                                                            dialog.dismiss();

//                                                            Toast.makeText(MainActivity.this, "done deleting", Toast.LENGTH_SHORT).show();

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });


                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                }
                                            });


                                        }


                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }

                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });

        dialog.show();

    }


    private void CLickOnLogoutBtn() {
        logout_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(PrefConfig.GetPref(MainActivity.this,"userPref","userpin"));
                auth.signOut();
                SharedPreferences sharedPreferences  = getSharedPreferences("userPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                Intent intent = new Intent(MainActivity.this, PhoneNumber_Activity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void ClickOnTrashNearMeBtn() {
        trash_near_me_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TrashNearMeActivity.class);
                intent.putExtra("pinCode", pinCode_txt.getText().toString());
                intent.putExtra("latitude", PrefConfig.GetPref(MainActivity.this, "latitudePref", "latitude"));
                intent.putExtra("longitude", PrefConfig.GetPref(MainActivity.this, "longitudePref", "longitude"));
                startActivity(intent);


            }
        });
    }

    private void ClickOnShowMApBtn() {
        show_map_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mapToggle) {
                    fragment.setVisibility(View.VISIBLE);
                    mapToggle = true;
                    show_map_text.setText("Hide Map");

                } else {
                    fragment.setVisibility(View.GONE);
                    mapToggle = false;
                    show_map_text.setText("Show Map");


                }
            }
        });
    }

    private void ClickOnOpenDrawer() {
        open_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              OpenDrawer();


            }
        });

    }

    private void OpenDrawer() {
        TranslateAnimation animate = new TranslateAnimation(menu_layout.getWidth() - 50, 0, 0, 0);
        animate.setDuration(500);
        animate.setFillAfter(true);
        menu_layout.startAnimation(animate);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                close_drawer.setRotation(360);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                    fab_open.setVisibility(View.VISIBLE);
//                    fab_open.animate().alpha(1.0f).setDuration(200);
                drawerToggle = 0;
                menu_layout.setVisibility(View.VISIBLE);
                open_drawer.setVisibility(View.GONE);
                drawer_helper.setVisibility(View.GONE);


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void ClickOnCloseDrawer() {
        close_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerClose();

            }
        });
    }


    private void DrawerClose() {
        if (drawerToggle == 0) {

            TranslateAnimation animate = new TranslateAnimation(0, menu_layout.getWidth() - 50, 0, 0);
            animate.setDuration(500);
            animate.setFillAfter(true);
            menu_layout.startAnimation(animate);
            animate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    close_drawer.setRotation(180);


                }

                @Override
                public void onAnimationEnd(Animation animation) {
//                    fab_open.setVisibility(View.VISIBLE);
//                    fab_open.animate().alpha(1.0f).setDuration(200);
                    drawerToggle = 1;
                    menu_layout.setVisibility(View.GONE);
                    open_drawer.setVisibility(View.VISIBLE);
                    drawer_helper.setVisibility(View.VISIBLE);

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
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


    private void openMapFragment() {
        Map_Fragment fragment = new Map_Fragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, fragment);
        transaction.commit();
        SetCode();
//        Toast.makeText(this, "done", Toast.LENGTH_SHORT).show();


    }


    private void SetAnimation() {


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                menu_layout.animate().alpha(1.0f).setDuration(500);

            }
        }, 1000);
    }


    private void requestPermissions() {
        // below line is use to request
        // permission in the current activity.
        Dexter.withActivity(this)
                // below line is use to request the number of
                // permissions which are required in our app.
                .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,
                        // below is the list of permissions
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                // after adding permissions we are
                // calling an with listener method.
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        // this method is called when all permissions are granted
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            // do you work now
                            openMapFragment();
//                            Toast.makeText(MainActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();

                        }
                        // check for permanent denial of any permission
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permanently,
                            // we will show user a dialog message.
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        // this method is called when user grants some
                        // permission and denies some of them.
                        permissionToken.continuePermissionRequest();
                    }
                }).withErrorListener(new PermissionRequestErrorListener() {
            // this method is use to handle error
            // in runtime permissions
            @Override
            public void onError(DexterError error) {
                // we are displaying a toast message for error message.
                Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
            }
        })
                // below line is use to run the permissions
                // on same thread and to check the permissions
                .onSameThread().check();
    }

    // below is the shoe setting dialog
    // method which is use to display a
    // dialogue message.
    private void showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permissions");

        // below line is our message for our dialog
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called on click on positive
                // button and on clicking shit button we
                // are redirecting our user from our app to the
                // settings page of our app.

//if you added fragment via layout xml

                dialog.cancel();
                // below is the intent from which we
                // are redirecting our user.
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // this method is called when
                // user click on negative button.
                dialog.cancel();
            }
        });
        // below line is used
        // to display our dialog
        builder.show();
    }


    @Override
    public void onBackPressed() {

        if (fragment.getVisibility() == View.VISIBLE) {
            fragment.setVisibility(View.GONE);
            mapToggle = false;
            show_map_text.setText("Show Map");

        } else {
            super.onBackPressed();

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        PrefConfig.SetPref(MainActivity.this, "markerPref", "marker", "0");

        mark_trash_toggle.setBackground(getResources().getDrawable(R.drawable.white_circulatr_back));
        marker_toggle=0;

    }

    @Override
    protected void onResume() {
        super.onResume();
        PrefConfig.SetPref(MainActivity.this, "markerPref", "marker", "0");

        CheckHelpRequest();
    }

    private void CheckHelpRequest() {
        String requestTracker = PrefConfig.GetPref(this, "helpPref", "helpmessage");
        if(!requestTracker.equals("error")){
            String[] splitMessageByComma = requestTracker.split(",");


            String name = splitMessageByComma[0];
            String displayMessage = splitMessageByComma[1];
            String phoneNumber = splitMessageByComma[2];
            String uid = splitMessageByComma[3];
            String timeStamp = splitMessageByComma[4];
            String latlang = splitMessageByComma[5];

            VerifyTimeStamp(name,displayMessage,phoneNumber,uid,timeStamp,latlang);




        }

    }

    private void VerifyTimeStamp(String name, String displayMessage, String phoneNumber, String uid, String timeStamp, String latlang) {
        Double checkDifference =System.currentTimeMillis()-Double.parseDouble(timeStamp);
        if(checkDifference<180000){
            if(!uid.equals(user.getUid())){
                ShowHelpDialog(name,displayMessage,phoneNumber,uid,timeStamp,latlang);

            }


        }else{
            SharedPreferences preferences = getSharedPreferences("helpPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            FirebaseMessaging.getInstance().unsubscribeFromTopic("cancel"+uid);
        }
    }

    private void ShowHelpDialog(String name, String displayMessage, String phoneNumber, String uid, String timeStamp, String latlang) {


        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.receive_help_dialog);

        FrameLayout help_btn = (FrameLayout) dialog.findViewById(R.id.help_btn);
        FrameLayout ignore_request = (FrameLayout) dialog.findViewById(R.id.ignore_request);
        TextView displayMsg = (TextView)dialog.findViewById(R.id.et_displayMsg);
        TextView user_name = (TextView) dialog.findViewById(R.id.user_name);
        user_name.setText(name);
        displayMsg.setText(displayMessage);


        ignore_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences preferences = getSharedPreferences("helpPref",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.commit();
                FirebaseMessaging.getInstance().unsubscribeFromTopic("cancel"+uid);
                dialog.dismiss();

            }
        });



        help_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ShareMapActivity.class);
                intent.putExtra("latlang",latlang);
                intent.putExtra("uid",uid);
                intent.putExtra("name",name);
                startActivity(intent);


                dialog.dismiss();
            }
        });


        dialog.show();




    }
}