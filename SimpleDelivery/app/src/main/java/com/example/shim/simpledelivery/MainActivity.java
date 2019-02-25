package com.example.shim.simpledelivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shim.simpledelivery.Network.ErrandService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Headers;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private EditText et_email;
    private EditText et_password;

    private Button btn_login;
    private Button btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        /*
        sharedPreferences에 유저의 token이 존재하면 token 유효성 검사 이후에 로그인 기능을 건너 뛰고
        존재하지 않으면 회원가입 액티비티로 이동, 토큰은 존재하지만 유효성 검사에 실패할 경우에는 다시 로그인하여 새로운 토큰을 부여받음
        */
        if(sharedPreferences.contains("token")){
            //서버에 토큰 유효성 검사 후에 IndexActivity로 이동
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://10.0.2.2:5050/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ErrandService service = retrofit.create(ErrandService.class);
            String token = sharedPreferences.getString("token","");
            Call<ResponseBody> call = service.getMyId(token);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()){
                        Log.d("response message: ", response.message());
                        Log.d("response toString: ", response.toString());
                        Log.d("response raw: ", response.raw().toString());
                        startActivity(new Intent(MainActivity.this, IndexActivity.class));
                    }else{
                        Toast.makeText(getApplicationContext(),"토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {

                }
            });
        }
        else{
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
        }

        //로그인 버튼 클릭시
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_email.length() == 0 || et_password.length() == 0){
                    Toast.makeText(getApplicationContext(), "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                //서버에 로그인 요청 보내기
                else{
                    //안드로이드 에뮬레이터에서는 10.0.0.2가 localhost라고 한다.
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://10.0.2.2:5050/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    ErrandService service = retrofit.create(ErrandService.class);

                    Call<ResponseBody> call = service.login(et_email.getText().toString(), et_password.getText().toString());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if(response.isSuccessful()){
                                try {
                                    try {
                                        //리스폰스 바디의 "success" 가 true or false로 넘어 오는데 이 값을 가지고 로그인 성공여부를 판단함
                                        JSONObject jsonObject = new JSONObject(response.body().string());
                                        Boolean isLoginSuccess = jsonObject.getBoolean("success");
                                        if(isLoginSuccess){
                                            String token = jsonObject.getString("token");
                                            sharedPreferences.edit().putString("token", token).commit();
                                            Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(MainActivity.this, IndexActivity.class));
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "이메일 혹은 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "서버와 통신에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        //회원가입 버튼 클릭시
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init(){
        sharedPreferences = getSharedPreferences("tokenInfo", 0);
        et_email = (EditText) findViewById(R.id.main_et_email);
        et_password = (EditText) findViewById(R.id.main_et_password);
        btn_login = (Button) findViewById(R.id.main_btn_login);
        btn_signUp = (Button) findViewById(R.id.main_btn_signup);
    }
}
