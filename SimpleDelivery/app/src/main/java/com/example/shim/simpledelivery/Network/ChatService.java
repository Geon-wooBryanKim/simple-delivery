package com.example.shim.simpledelivery.Network;

import com.example.shim.simpledelivery.Model.Message;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ChatService {
    //진행 전인 모든 심부름을 불러옴
    @GET("/message")
    Call<List<Message>> getMessage(@Header("x-access-token") String token);
}
