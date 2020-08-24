package com.example.messengerapp.Fragments;

import com.example.messengerapp.Notifications.MyResponse;
import com.example.messengerapp.Notifications.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAjHklMP0:APA91bFN9JH-B0FyRwO1pNNnKmFOGR5m4y1_QUL8z-1fFflW4dl5Y_o2AxSqSRtCkGzcQgYyCPHhwCuPgP3RMfF5DrPuviBuKpFwV6eEB-ZSsPgZgp8aAEKa08FiGGMFZZSfqY6hb-jL"
    })

    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
