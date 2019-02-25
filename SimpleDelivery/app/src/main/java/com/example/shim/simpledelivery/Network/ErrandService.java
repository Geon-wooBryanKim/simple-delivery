package com.example.shim.simpledelivery.Network;

import com.example.shim.simpledelivery.Model.Errand;
import com.example.shim.simpledelivery.Model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ErrandService {
    //저장된 모든 유저를 불러옴
    @GET("/user")
    Call<ResponseBody> getUser(@Header("x-access-token") String token);

    //현재 유저의 id를 받아옴 (토큰 유효성 검사용)
    @GET("/me")
    Call<ResponseBody> getMyId(@Header("x-access-token") String token);

    //Fcm 토큰을 업데이트 함
    @PATCH("/me")
    Call<ResponseBody> updateFcmToken(@Header("x-access-token") String token, @Query("fcmToken") String fcmToken);

    //진행 전인 모든 심부름을 불러옴
    @GET("/errand")
    Call<List<Errand>> getErrand(@Header("x-access-token") String token);

    //특정 심부름의 porter_id에 현재 유저의 id를 업데이트하고 상태를 진행중으로 업데이트 함
    @PATCH("/errand")
    Call<ResponseBody> updateErrand(@Header("x-access-token") String token, @Query("id") int id);

    //새로운 심부름을 등록함
    @POST("/errand")
    Call<ResponseBody> createErrand(@Header("x-access-token") String token, @Body Errand errand);

    //새로운 유저를 등록함
    @POST("/user/signup")
    Call<ResponseBody> createUser(@Body User user);

    //로그인 성공 시 JWT토큰을 받음
    @FormUrlEncoded
    @POST("/user/login")
    Call<ResponseBody> login(@Field("email") String email, @Field("password") String password);

    @POST("/fcm")
    Call<ResponseBody> sendFcm(@Header("x-access-token") String token, @Query("buyer_id") int buyerId);
}
