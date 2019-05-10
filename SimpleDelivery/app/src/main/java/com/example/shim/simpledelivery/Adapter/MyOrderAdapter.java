package com.example.shim.simpledelivery.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shim.simpledelivery.MainActivity;
import com.example.shim.simpledelivery.Model.Errand;
import com.example.shim.simpledelivery.Network.ErrandService;
import com.example.shim.simpledelivery.OrderDetailActivity;
import com.example.shim.simpledelivery.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyOrderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Errand> errandList = new ArrayList<>();
    private Context context;
    private Retrofit retrofit;
    private int myId;

    //OrderListActivity에서 사용할 생성자 구분하기 위해 int형 파라미터를 하나 더 추가함
    public MyOrderAdapter(Context context, int myId) {
        this.context = context;
        this.myId = myId;

        retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5050/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ErrandService service = retrofit.create(ErrandService.class);
        String token = context.getSharedPreferences("tokenInfo", 0).getString("token", "");

        Call<List<Errand>> call = service.getMyOrder(token);
        call.enqueue(new Callback<List<Errand>>() {
            @Override
            public void onResponse(Call<List<Errand>> call, Response<List<Errand>> response) {
                if(response.isSuccessful()){
                    errandList = response.body();
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Errand>> call, Throwable t) {

            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.errand_list_item, viewGroup, false);
        return new ErrandViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final Errand errand = errandList.get(i);
        if(errand.getBuyer_id() == myId) {
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, OrderDetailActivity.class);
                    intent.putExtra("errand", errandList.get(i));
                    context.startActivity(intent);
                }
            });
            ((ErrandViewHolder) viewHolder).tv_address.setText(errand.getDestination());
            ((ErrandViewHolder) viewHolder).tv_price.setText(String.valueOf(errand.getPrice()));
            ((ErrandViewHolder) viewHolder).tv_contents.setText(errand.getContents());
            ((ErrandViewHolder) viewHolder).btn_accpet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ErrandService errandService = retrofit.create(ErrandService.class);
                    final String token = context.getSharedPreferences("tokenInfo", 0).getString("token", "");
                    Call<ResponseBody> call = errandService.updateErrand(token, errand.getId());
                    call.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(context, "수락 완료", Toast.LENGTH_SHORT).show();
                                //해당 심부름의 porter_id와 상태 업데이트 완료 후에 주문자한테 푸시 알람 전송
                                call = errandService.sendFcm(token, errand.getBuyer_id());
                                call.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "fcm 전송 완료", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                        }
                    });
                    Toast.makeText(context, String.valueOf(i), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return errandList.size();
    }

    private class ErrandViewHolder extends RecyclerView.ViewHolder {
        TextView tv_address;
        TextView tv_price;
        TextView tv_contents;
        Button btn_accpet;
        public ErrandViewHolder(View view) {
            super(view);
            tv_address = view.findViewById(R.id.errandList_tv_address);
            tv_price = view.findViewById(R.id.errandList_tv_price);
            tv_contents = view.findViewById(R.id.errandList_tv_contents);
            btn_accpet = view.findViewById(R.id.errandList_btn_accept);
        }
    }
}
