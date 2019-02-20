package com.example.shim.simpledelivery;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.shim.simpledelivery.Model.Errand;
import com.example.shim.simpledelivery.Network.ErrandService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderActivity extends AppCompatActivity {
    EditText et_detailAddr;
    EditText et_errandPrice;
    EditText et_productPrice;
    EditText et_contents;

    Button btn_complete;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        init();

        btn_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://10.0.2.2:5050/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                String destination = getIntent().getStringExtra("roadAddr");
                double latitude = getIntent().getDoubleExtra("latitude", 0.0);
                double longitude = getIntent().getDoubleExtra("longitude", 0.0);
                int price = Integer.valueOf(et_errandPrice.getText().toString()) + Integer.valueOf(et_productPrice.getText().toString());
                String contents = et_contents.getText().toString();

                Errand errand = new Errand(destination, latitude, longitude, price, contents);

                ErrandService service = retrofit.create(ErrandService.class);
                String token = getSharedPreferences("tokenInfo",0).getString("token", "");
                Call<ResponseBody> call = service.createErrand(token, errand);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful()){

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }
        });

    }

    private void init(){
        et_detailAddr = findViewById(R.id.order_et_detailAddr);
        et_errandPrice = findViewById(R.id.order_et_errandPrice);
        et_productPrice = findViewById(R.id.order_et_productPrice);
        et_contents = findViewById(R.id.order_et_contents);
        btn_complete = findViewById(R.id.order_btn_complete);
    }
}
