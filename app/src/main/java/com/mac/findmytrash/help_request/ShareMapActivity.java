package com.mac.findmytrash.help_request;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.mac.findmytrash.R;
import com.mac.findmytrash.map_location.Map_Fragment;
import com.mac.findmytrash.map_location.PrefConfig;

public class ShareMapActivity extends AppCompatActivity {

    private String latlang ;
    private String uid,name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_map);
        PrefConfig.SetPref(ShareMapActivity.this, "markerPref", "marker","1");


        latlang=getIntent().getStringExtra("latlang");
        uid=getIntent().getStringExtra("uid");
        name=getIntent().getStringExtra("name");

        String[] splitLatlang = latlang.split("-");

        String latitude = splitLatlang[0];
        String longitude = splitLatlang[1];

        openMapFragment(uid,latitude,longitude);
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
}