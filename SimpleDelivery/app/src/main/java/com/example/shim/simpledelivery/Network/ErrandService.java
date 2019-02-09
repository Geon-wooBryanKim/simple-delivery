package com.example.shim.simpledelivery.Network;

import com.example.shim.simpledelivery.Model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ErrandService {
    @POST("/user/signup")
    Call<ResponseBody> createUser(@Body User user);

    @FormUrlEncoded
    @POST("/user/login")
    Call<ResponseBody> login(@Field("email") String email, @Field("password") String password);
}