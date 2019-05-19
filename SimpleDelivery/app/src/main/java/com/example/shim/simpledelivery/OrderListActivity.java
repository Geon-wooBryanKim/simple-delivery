package com.example.shim.simpledelivery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.example.shim.simpledelivery.Adapter.ErrandAdapter;
import com.example.shim.simpledelivery.Adapter.MyOrderAdapter;
import com.example.shim.simpledelivery.Model.Errand;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MyOrderAdapter myOrderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        init();
    }

    public void init(){
        recyclerView = findViewById(R.id.orderList_recyclerView);
        myOrderAdapter = new MyOrderAdapter(OrderListActivity.this, MainActivity.myId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(myOrderAdapter);

    }
}
