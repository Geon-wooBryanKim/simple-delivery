package com.example.shim.simpledelivery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shim.simpledelivery.Adapter.MessageAdapter;
import com.example.shim.simpledelivery.Model.Errand;
import com.example.shim.simpledelivery.Model.Message;
import com.example.shim.simpledelivery.Network.ChatService;
import com.example.shim.simpledelivery.Network.ErrandService;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
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

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5050/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ChatService service = retrofit.create(ChatService.class);
        String token = sharedPreferences.getString("token","");
        Call<List<Message>> call = service.getMessage(token);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                List<Message> messageList = response.body();
                messageAdapter = new MessageAdapter(messageList, ChatActivity.this);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {

            }
        });
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Gson gson = new Gson();
                    Message message = gson.fromJson(args[0].toString(), Message.class);
//                    JSONObject object = (JSONObject) args[0];
//                    String message;

//                        message.setContents(object.getString("contents"));
//                        message.setSender_id(object.getInt("sender_id"));
//                        message.setReceiver_id(object.getInt("reciever_id"));
                        messageAdapter.addItem(message);
//                        message = object.getString("message");
//                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

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
        recyclerView = findViewById(R.id.chat_recyclerView);
        et_input = findViewById(R.id.chat_et_input);
        btn_send = findViewById(R.id.chat_btn_send);
    }

    private void sendMessage() {
        Message message = new Message();
        message.setSender_id(MainActivity.myId);
        message.setReceiver_id(Integer.valueOf(destinationId));
        message.setContents(et_input.getText().toString().trim());
        Gson gson = new Gson();
//        String message = et_input.getText().toString().trim();
        if (TextUtils.isEmpty(message.getContents())) {
            return;
        }
        socket.emit("sendToSomeone", gson.toJson(message), destinationId);
        et_input.setText("");
        messageAdapter.addItem(message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        socket.disconnect();
        socket.off("new message", onNewMessage);
    }
}
