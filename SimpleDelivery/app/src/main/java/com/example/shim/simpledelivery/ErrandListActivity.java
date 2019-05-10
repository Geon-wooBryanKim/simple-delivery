package com.example.shim.simpledelivery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.shim.simpledelivery.Adapter.MyErrandAdapter;
import com.example.shim.simpledelivery.Adapter.MyOrderAdapter;

public class ErrandListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyErrandAdapter myErrandAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand_list);
        init();
    }

    public void init(){
        recyclerView = findViewById(R.id.errandListActivity_recyclerView);
        myErrandAdapter = new MyErrandAdapter(ErrandListActivity.this, MainActivity.myId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(myErrandAdapter);

    }
}
