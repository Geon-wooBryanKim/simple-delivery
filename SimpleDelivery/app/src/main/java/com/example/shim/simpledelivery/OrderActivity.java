package com.example.shim.simpledelivery;

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.shim.simpledelivery.Model.ReverseGeoResponse;
import com.example.shim.simpledelivery.Network.NaverApiService;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    private NaverMap naverMap;

    private FusedLocationSource locationSource;

    private final String NAVER_MAPS_API_CLIENT_ID = "gx3ac897nw";

    private UiSettings uiSettings;

    private CameraPosition cameraPosition;

    private Button btn_position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        btn_position = (Button) findViewById(R.id.order_btn_camPosition);
        btn_position.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), String.valueOf(cameraPosition.target.latitude) +
                        ", " + String.valueOf(cameraPosition.target.longitude), Toast.LENGTH_SHORT).show();

                Log.d("LatLng: " , String.valueOf(cameraPosition.target.latitude) +", " + String.valueOf(cameraPosition.target.longitude));
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

        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.order_frag_map);
        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.order_frag_map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }


    @Override
    public void onMapReady(@NonNull final NaverMap naverMap) {
        //지도에 현위치 버튼 표시
        uiSettings = naverMap.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

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
                            ReverseGeoResponse reverseGeoResponse = response.body();
                            String area0 = reverseGeoResponse.getResults().get(0).getRegion().getArea0().getName();
                            String area1 = reverseGeoResponse.getResults().get(0).getRegion().getArea1().getName();
                            String area2 = reverseGeoResponse.getResults().get(0).getRegion().getArea2().getName();
                            String area3 = reverseGeoResponse.getResults().get(0).getRegion().getArea3().getName();
                            String area4 = reverseGeoResponse.getResults().get(0).getRegion().getArea4().getName();
                            String roadName = reverseGeoResponse.getResults().get(0).getLand().getName();
                            String roadNumber = reverseGeoResponse.getResults().get(0).getLand().getNumber1();
                            Toast.makeText(getApplicationContext(),area1 + " " + area2 + " " + area3 + " " + area4 + " " + roadName + " " + roadNumber, Toast.LENGTH_SHORT).show();
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
