package com.example.shim.simpledelivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shim.simpledelivery.Model.Errand;
import com.example.shim.simpledelivery.Network.ErrandService;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class ChatActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private EditText et_input;
    private Button btn_send;
    private Socket socket;
    private Errand errand;
    private String userId;
    private String destinationId;

    {
        try {
            socket = IO.socket("http://10.0.2.2:6060/");

        } catch (URISyntaxException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        socket.connect();
        socket.on("new message", onNewMessage);
        socket.emit("update socket_id", userId);

//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("http://10.0.2.2:5050/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        ErrandService service = retrofit.create(ErrandService.class);
//        String token = sharedPreferences.getString("token", "");
//        if (!TextUtils.isEmpty(token)) {
//            Call<ResponseBody> call = service.getMyId(token);
//
//            call.enqueue(new Callback<ResponseBody>() {
//                @Override
//                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                    if (response.isSuccessful()) {
//
//                        JSONObject jsonObject = null;
//                        try {
//                            jsonObject = new JSONObject(response.body().string());
//                            userId = jsonObject.get("id").toString().trim();
//                            Log.d("ChatActivity userId: ", userId);
//
//                            socket.emit("update socket_id", userId);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    } else {
//                        Toast.makeText(getApplicationContext(), "토큰이 만료되었거나 유효하지 않습니다. 다시 로그인해주세요", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ResponseBody> call, Throwable t) {
//
//                }
//            });
//        }
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    String message;
                    try {
                        message = object.getString("message");
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        return;
                    }
                }
            });
        }
    };

    private void init() {
        errand = (Errand) getIntent().getSerializableExtra("errand");
        if(errand.getBuyer_id() == MainActivity.myId){
            userId = String.valueOf(errand.getBuyer_id());
            destinationId = String.valueOf(errand.getPorter_id());
        }
        else{
            userId = String.valueOf(errand.getPorter_id());
            destinationId = String.valueOf(errand.getBuyer_id());
        }
        sharedPreferences = getSharedPreferences("tokenInfo", 0);
        et_input = findViewById(R.id.chat_et_input);
        btn_send = findViewById(R.id.chat_btn_send);
    }

    private void sendMessage() {
        String message = et_input.getText().toString().trim();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        socket.emit("sendToSomeone", message, destinationId);
        et_input.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("new message", onNewMessage);
    }
}
