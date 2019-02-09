package com.example.shim.simpledelivery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shim.simpledelivery.Network.ErrandService;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_password;

    private Button btn_login;
    private Button btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

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
                                    if(Boolean.parseBoolean(response.body().string())){
                                        Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(), "이메일 혹은 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

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
        et_email = (EditText) findViewById(R.id.main_et_email);
        et_password = (EditText) findViewById(R.id.main_et_password);
        btn_login = (Button) findViewById(R.id.main_btn_login);
        btn_signUp = (Button) findViewById(R.id.main_btn_signup);
    }
}
