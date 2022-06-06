package com.mac.findmytrash.map_location;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mac.findmytrash.R;


public class pinMarkInfo_Activity extends AppCompatActivity {
    private EditText area,address,purpose;
    private String Area,Address,Latitude,Longitude,PinCode;
    private TextView pinCode,latitude,longitude,next;
    private EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_mark_info);


        getIntentFromAnotherActivity();
        initialisingWidgets();
        settingMapInfo();
//
//        next.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(phoneNumber.getText().toString().isEmpty()||area.getText().toString().isEmpty()||address.getText().toString().isEmpty()||purpose.getText().toString().isEmpty()){
//                    Toast.makeText(getApplicationContext(), "fill all the form correctly !", Toast.LENGTH_SHORT).show();
//                }else{
//
//
//                    Intent intent = new Intent(getApplicationContext(), setLocationInfo_Activity.class);
//                    intent.putExtra("area", Area);
//                    intent.putExtra("address", Address);
//                    intent.putExtra("pinCode", PinCode);
//                    intent.putExtra("latitude", Latitude);
//                    intent.putExtra("longitude", Longitude);
//                    intent.putExtra("purpose", purpose.getText().toString());
//                    intent.putExtra("phoneNumber", phoneNumber.getText().toString());
//
//                    startActivity(intent);                }
//            }
//        });

    }





    private void getIntentFromAnotherActivity() {
        Area = getIntent().getStringExtra("area");
        Address = getIntent().getStringExtra("address");
        PinCode = getIntent().getStringExtra("pinCode");
        Latitude = getIntent().getStringExtra("latitude");
        Longitude = getIntent().getStringExtra("longitude");
    }

    private void initialisingWidgets() {
        area=findViewById(R.id.area);
        address=findViewById(R.id.address);
        latitude=findViewById(R.id.latitude);
        longitude =findViewById(R.id.longitude);
        pinCode=findViewById(R.id.pinCode);
        phoneNumber=findViewById(R.id.phoneNumber);
        next=findViewById(R.id.next);
        purpose=findViewById(R.id.purpose);
    }

    private void settingMapInfo() {
        area.setText(Area);
        address.setText(Address);
        pinCode.setText(PinCode);
        latitude.setText(Latitude);
        longitude.setText(Longitude);
        phoneNumber.requestFocus();
    }


}