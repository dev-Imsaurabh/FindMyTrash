package com.mac.findmytrash.FCM_experiment.API;

import static com.mac.findmytrash.FCM_experiment.Model.Constant.CONTENT_TYPE;
import static com.mac.findmytrash.FCM_experiment.Model.Constant.SERVER_KEY;

import com.mac.findmytrash.FCM_experiment.Model.PushNotification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public  interface ApiInterface {
    @Headers({"Authorization: key="+SERVER_KEY, "Content-Type:"+CONTENT_TYPE})
    @POST("fcm/send")
    Call<PushNotification>sendNotification(@Body PushNotification notification);
}
