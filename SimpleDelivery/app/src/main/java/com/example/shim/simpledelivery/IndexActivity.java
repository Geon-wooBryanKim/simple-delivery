package com.example.shim.simpledelivery;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class IndexActivity extends AppCompatActivity {

    private Button btn_errand;
    private Button btn_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_index);
        init();

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
