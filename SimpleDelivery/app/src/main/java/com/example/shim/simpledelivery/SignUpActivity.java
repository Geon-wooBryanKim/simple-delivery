package com.example.shim.simpledelivery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shim.simpledelivery.Model.User;
import com.example.shim.simpledelivery.Network.ErrandService;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    private EditText et_email;
    private EditText et_phone;
    private EditText et_password;
    private EditText et_password2;
    private Button btn_signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();

        //회원가입 버튼 클릭시
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력되지 않은 항목이 있을 때
                if(et_email.length() == 0 || et_phone.length() == 0 ||
                        et_password.length() == 0 || et_password2.length() == 0){
                    Toast.makeText(getApplicationContext(), "입력하지 않은 항목이 있습니다.", Toast.LENGTH_SHORT).show();
                }
                else{
                    //비밀번호 확인이 다른 경우
                    if(!et_password.getText().toString().equals(et_password2.getText().toString())){
                        Toast.makeText(getApplicationContext(), "비밀번호 확인이 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                        et_password.setText("");
                        et_password2.setText("");
                    }
                    //서버에 회원가입 요청 보내기
                    else{
                        String email = et_email.getText().toString();
                        String phone = et_phone.getText().toString();
                        String password = et_password.getText().toString();

                        User user = new User(email, phone, password);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://10.0.2.2:5050/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        ErrandService service = retrofit.create(ErrandService.class);

                        Call<ResponseBody> call = service.createUser(user);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "회원가입 완료", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "회원가입 실패", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Log.d("onFailure", t.getMessage());
                                Toast.makeText(getApplicationContext(), "서버와 통신 중 에러가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        });
    }

    private void init(){
        et_email = (EditText) findViewById(R.id.signup_et_email);
        et_phone = (EditText) findViewById(R.id.signup_et_phone);
        et_password = (EditText) findViewById(R.id.signup_et_password);
        et_password2 = (EditText) findViewById(R.id.signup_et_password2);
        btn_signUp = (Button) findViewById(R.id.signup_btn_signup);
    }
}
