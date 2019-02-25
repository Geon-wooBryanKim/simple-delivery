package com.example.shim.simpledelivery;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shim.simpledelivery.Network.ErrandService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class IndexActivity extends AppCompatActivity {

    private Button btn_errand;
    private Button btn_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        init();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TEST", "getInstanceId failed", task.getException());
                            return;
                        }

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://10.0.2.2:5050/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        ErrandService service = retrofit.create(ErrandService.class);
                        String jwtToken = getSharedPreferences("tokenInfo", 0).getString("token","");

                        String token = task.getResult().getToken();
                        Call<ResponseBody> call = service.updateFcmToken(jwtToken, token);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if (response.isSuccessful()){
                                    Log.d("FCM SERVICE : ", "FCM Token is updated");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("FCM SERVICE : ", t.getMessage());
                            }
                        });
                        // Get new Instance ID token

                        Log.d("TEST", token);
                    }
                });

        //심부름하기 버튼 클릭시 심부름 모드 전환
        btn_errand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, ErrandActivity.class));
            }
        });

        //주문하기 버튼 클릭시 주문자 모드 전환
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(IndexActivity.this, PickAddressActivity.class));
            }
        });
    }

    private void init() {
        btn_errand = (Button) findViewById(R.id.index_btn_errand);
        btn_order = (Button) findViewById(R.id.index_btn_order);
    }
}
