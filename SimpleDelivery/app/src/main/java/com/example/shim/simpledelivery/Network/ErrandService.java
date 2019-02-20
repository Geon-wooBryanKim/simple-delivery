package com.example.shim.simpledelivery.Network;

import com.example.shim.simpledelivery.Model.Errand;
import com.example.shim.simpledelivery.Model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ErrandService {
    @GET("/user")
    Call<ResponseBody> getUser(@Header("x-access-token") String token);

    @GET("/me")
    Call<ResponseBody> getMyId(@Header("x-access-token") String token);

    @POST("/errand")
    Call<ResponseBody> createErrand(@Header("x-access-token") String token, @Body Errand errand);

    @POST("/user/signup")
    Call<ResponseBody> createUser(@Body User user);

    @FormUrlEncoded
    @POST("/user/login")
    Call<ResponseBody> login(@Field("email") String email, @Field("password") String password);
}
