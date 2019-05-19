package com.example.shim.simpledelivery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.example.shim.simpledelivery.Adapter.ErrandAdapter;
import com.example.shim.simpledelivery.Model.Errand;

import java.util.ArrayList;

public class ErrandActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ErrandAdapter errandAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_errand);
        init();
    }

    private void init(){
        recyclerView = findViewById(R.id.errand_recyclerView);
        errandAdapter = new ErrandAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(errandAdapter);
    }
}
