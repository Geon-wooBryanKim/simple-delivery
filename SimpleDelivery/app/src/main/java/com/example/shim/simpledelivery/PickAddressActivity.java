package com.example.shim.simpledelivery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shim.simpledelivery.Model.ReverseGeoResponse;
import com.example.shim.simpledelivery.Network.NaverApiService;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.util.FusedLocationSource;
import com.naver.maps.map.widget.LocationButtonView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PickAddressActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private NaverMap naverMap;
    private FusedLocationSource locationSource;
    private final String NAVER_MAPS_API_CLIENT_ID = "gx3ac897nw";
    private UiSettings uiSettings;
    private CameraPosition cameraPosition;
    private Button btn_position;
    private TextView tv_addr;
    private TextView tv_addrToggle;

    private String addr;
    private String roadAddr;
    private boolean addrFlag; //도로명주소를 보여줄지 지번주소를 보여줄지 결정하는 플래그

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_address);

        tv_addr = (TextView) findViewById(R.id.pickAddress_tv_addr);
        tv_addrToggle = (TextView) findViewById(R.id.pickAddress_tv_addrToggle);

        //클릭시 도로명 or 지번 주소 토글해서 보여줌
        tv_addrToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_addr.setText(addrFlag ? roadAddr : addr);
                tv_addrToggle.setText(addrFlag ? "지번 주소로 보기" : "도로명 주소로 보기");
                addrFlag = !addrFlag;
            }
        });

        btn_position = (Button) findViewById(R.id.pickAddress_btn_camPosition);
        btn_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PickAddressActivity.this, OrderActivity.class);
                double latitude = cameraPosition.target.latitude;
                double longitude = cameraPosition.target.longitude;

                //인텐트에 위도, 경도, 지번주소, 도로명주소를 넘겨줌.
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);
                intent.putExtra("addr", addr);
                intent.putExtra("roadAddr", roadAddr);

                startActivity(intent);
            }
        });

        //내위치 퍼미션을 획득했는지 확인
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        //네이버 지도 API 클라이언트 ID를 등록함
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(NAVER_MAPS_API_CLIENT_ID));

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.pickAddress_frag_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.pickAddress_frag_map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        //지도에 현위치 버튼 표시
//        uiSettings = naverMap.getUiSettings();
//        uiSettings.setLocationButtonEnabled(true);

        LocationButtonView locationButtonView = findViewById(R.id.pickAddress_locationBtnView);
        locationButtonView.setMap(naverMap);

        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Follow);

        //지도 카메라 이동시 호출되는 콜백 메서드
        naverMap.addOnCameraChangeListener(new NaverMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(int i, boolean b) {
                cameraPosition = naverMap.getCameraPosition();
            }
        });

        naverMap.addOnCameraIdleListener(new NaverMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                cameraPosition = naverMap.getCameraPosition();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://naveropenapi.apigw.ntruss.com/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();

                NaverApiService naverApiService = retrofit.create(NaverApiService.class);

                String coords = String.valueOf(cameraPosition.target.longitude) + "," + String.valueOf(cameraPosition.target.latitude); //longitude, latitude
                String output = "json"; //json or xml
                String order = "roadaddr,addr";
                Call<ReverseGeoResponse> call = naverApiService.getAddrWithLatLng(coords, output, order);

                call.enqueue(new Callback<ReverseGeoResponse>() {
                    @Override
                    public void onResponse(Call<ReverseGeoResponse> call, Response<ReverseGeoResponse> response) {
                        Log.d("Call Request =" , call.request().toString());
                        if(response.isSuccessful()){
                            if(response.body().getResults().size() == 0){
                                Toast.makeText(getApplicationContext(), "지정한 위치의 주소가 없습니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            ReverseGeoResponse reverseGeoResponse = response.body();
                            String area0 = reverseGeoResponse.getResults().get(0).getRegion().getArea0().getName();
                            String area1 = reverseGeoResponse.getResults().get(0).getRegion().getArea1().getName();
                            String area2 = reverseGeoResponse.getResults().get(0).getRegion().getArea2().getName();
                            String area3 = reverseGeoResponse.getResults().get(0).getRegion().getArea3().getName();
                            String area4 = reverseGeoResponse.getResults().get(0).getRegion().getArea4().getName();
                            String roadName = reverseGeoResponse.getResults().get(0).getLand().getName();
                            String roadNumber = reverseGeoResponse.getResults().get(0).getLand().getNumber1();
                            roadAddr = area1 + " " + area2 + " " + area3 + " " + area4 + " " + roadName + " " + roadNumber;
                            addr = "논현동 634-1";

                            tv_addr.setText(addrFlag ? addr : roadAddr);
                        }
                    }
                    @Override
                    public void onFailure(Call<ReverseGeoResponse> call, Throwable t) {
                        Log.d("onFailure", t.getMessage());
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE){
            if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {


                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }

                return ;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
