package com.mac.findmytrash.FCM_experiment;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mac.findmytrash.MainActivity;
import com.mac.findmytrash.R;
import com.mac.findmytrash.help_request.ShareMapActivity;
import com.mac.findmytrash.map_location.PrefConfig;

import java.util.Random;

public class FCMService extends FirebaseMessagingService {
    private final String CHANNEL_ID = "trash_id";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);


        String getHelpPref = PrefConfig.GetPref(this, "helpPref", "helpmessage");
        String[] splitMessageByComma = getHelpPref.split(",");
        String uid = null;
        try {
            String name = splitMessageByComma[0];
            String displayMessage = splitMessageByComma[1];
            String phoneNumber = splitMessageByComma[2];
            uid = splitMessageByComma[3];
            String timeStamp = splitMessageByComma[4];
            String latlang = splitMessageByComma[5];
        } catch (Exception e) {
            e.printStackTrace();
        }

        String fullMessage = message.getData().get("message");



        if (fullMessage.contains("help")) {
            String[] splitMessageByComma1 = fullMessage.split(",");
            String name1 = splitMessageByComma1[0];
            String displayMessage1 = splitMessageByComma1[1];
            String phoneNumber1 = splitMessageByComma1[2];
            String uid1 = splitMessageByComma1[3];
            String timeStamp1 = splitMessageByComma1[4];
            String latlang1 = splitMessageByComma1[5];

            PrefConfig.SetPref(this, "helpPref", "helpmessage", fullMessage);

            if(!PrefConfig.GetPref(this, "tempTopic", "topic").equals("error")){
                FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/"+"cancel"+PrefConfig.GetPref(this, "tempTopic", "topic"));
                PrefConfig.SetPref(this, "tempTopic", "topic", uid1);
                FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+"cancel"+uid1);
            }else {
                FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+"cancel"+uid1);

            }



            FirebaseMessaging.getInstance().subscribeToTopic("/topics/"+"cancel"+uid);
            FirebaseAuth auth= FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if(!user.getUid().equals(uid1)){
                if(System.currentTimeMillis()-Double.parseDouble(timeStamp1)<3000){
                    ShowNotification(displayMessage1, message);
//                    ShowHelpDialog();

                }

            }

        } else if(fullMessage.equals("cancel"+uid)) {


            SharedPreferences preferences = getSharedPreferences("helpPref",MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.commit();
            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/"+"cancel"+uid);

        }else  if(fullMessage.contains("sharing")){
            PrefConfig.SetPref(this,"rPref","rece",fullMessage);
        }


    }

    private void ShowNotification(String displayMessage, RemoteMessage message) {

        Intent intent = new Intent(this, MainActivity.class);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = new Random().nextInt();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getBaseContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        Notification notification;
        notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(message.getData().get("title"))
                .setContentText(displayMessage)
                .setSmallIcon(R.drawable.custom_marker2)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();

        notificationManager.notify(notificationId, notification);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager notificationManager) {

        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, "trashName", NotificationManager.IMPORTANCE_HIGH);

        notificationChannel.setDescription("MyDesc");
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.WHITE);

        notificationManager.createNotificationChannel(notificationChannel);

    }



    private void ShowHelpDialog() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getBaseContext().startActivity(intent);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 /* request code */, intent,PendingIntent.FLAG_UPDATE_CURRENT);



    }
}
