package com.mac.findmytrash.FCM_experiment;

import android.content.Context;
import android.widget.Toast;

import com.mac.findmytrash.FCM_experiment.API.ApiUtilities;
import com.mac.findmytrash.FCM_experiment.Model.PushNotification;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotification {
    public static void Send(PushNotification pushNotification, Context context){


        ApiUtilities.getClient().sendNotification(pushNotification).enqueue(new Callback<PushNotification>() {
            @Override
            public void onResponse(Call<PushNotification> call, Response<PushNotification> response) {
                if(response.isSuccessful()){
                    Toast.makeText(context, "successful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<PushNotification> call, Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();


            }
        });


    }
}
