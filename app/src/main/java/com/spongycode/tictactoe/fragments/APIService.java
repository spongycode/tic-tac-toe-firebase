package com.spongycode.tictactoe.fragments;

import com.spongycode.tictactoe.Notifications.MyResponse;
import com.spongycode.tictactoe.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAwhY0LpI:APA91bFRoy9XCuT0yGtweg1ia39bZ-We0YAK0aTixxf9rCO1Kc9AULXrGwLFhzS4fgi5G_e8hzOy4v6IWB1m5b5F9ACAILb1f8mTYpRqt9XZLgsg_r0Nppj65ss9qw4bx5yimME8_k6m"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
