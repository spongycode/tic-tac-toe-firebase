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
            "Authorization:key=AAAAwhY0LpI:APA91bGRoWE6dN1rVt7D4XxCuAN_rQzCUgPVfTh2pFytjjuOIGhQnRbPhxJ3d5DMWRmMDak5f5ySB3kH8t-XyGiXnL0djS__LWE0OfCd3z2Acy_2CdfuxH9l7tN817vrwYvDtX5Zxrns"
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
