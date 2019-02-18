package com.example.shim.simpledelivery.Network;

import com.example.shim.simpledelivery.Model.ReverseGeoResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface NaverApiService {
    @Headers({
            "X-NCP-APIGW-API-KEY-ID: gx3ac897nw",
            "X-NCP-APIGW-API-KEY: 4AdEQI8u0X41lzdS1FdBQwxUSztg23wctO2Xi778"
    })
    @GET("map-reversegeocode/v2/gc")
    Call<ReverseGeoResponse> getAddrWithLatLng(
            @Query("coords") String coords, @Query("output") String ouput, @Query("orders") String orders);
}
