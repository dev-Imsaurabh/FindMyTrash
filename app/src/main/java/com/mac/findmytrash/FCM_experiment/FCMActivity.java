package com.mac.findmytrash.FCM_experiment;

import static com.mac.findmytrash.FCM_experiment.Model.Constant.TOPIC;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.mac.findmytrash.FCM_experiment.Model.NotificationData;
import com.mac.findmytrash.FCM_experiment.Model.PushNotification;
import com.mac.findmytrash.R;
import com.mac.findmytrash.map_location.PrefConfig;

public class FCMActivity extends AppCompatActivity {

    private EditText title, message;
    private String title_item, message_item;
    private Button send_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmactivity);
        String pref =PrefConfig.GetPref(this, "helpPref", "helpmessage");
        Toast.makeText(this,pref, Toast.LENGTH_SHORT).show();
        String notiData = getIntent().getStringExtra("notiData");
        if (notiData != null) {
            Toast.makeText(this, "notiDataAvaialbe", Toast.LENGTH_SHORT).show();
        }
        title = findViewById(R.id.title);
        message = findViewById(R.id.message);
        send_btn = findViewById(R.id.send_btn);

//        FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC);


        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title_item = title.getText().toString().trim();
                message_item = message.getText().toString().trim();

                PushNotification pushNotification = new PushNotification(new NotificationData(title_item, message_item), TOPIC);
                SendNotification.Send(pushNotification, FCMActivity.this);


            }
        });
    }

}