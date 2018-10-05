package kr.ac.inhagachon.www.idol;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class Main extends AppCompatActivity {
    static double latitude;
    static double longitude;
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        //사용자 환영 토스트 출력, 비회원은 안함
        if(!Load.accounts[Account.current_index].name.equals("비회원"))
        Toast.makeText(getApplicationContext(), Load.accounts[Account.current_index].name+"님 환영합니다", Toast.LENGTH_SHORT).show();

        //위치정보 출력

        //시작주소의 현재위치 찾기 버튼
        Button SlocationBTN=(Button)findViewById(R.id.getLocation);
        SlocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingGPS();
                Location location=getMyLocation();
                TextView start_Location=(TextView)findViewById(R.id.start_location);
                Print_Location(location, start_Location);
            }
        });

        //목적지의 현재위치 찾기 버튼
        Button DlocationBTN=(Button)findViewById(R.id.getLocation2);
        DlocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingGPS();
                Location location=getMyLocation();
                TextView destination_location=(TextView)findViewById(R.id.destination_location);
                Print_Location(location, destination_location);
            }
        });


    }

    public void select_freight_kind(View v) { //화물 크기 선택 메서드

        LayoutInflater inflater=getLayoutInflater();
        final View layout=inflater.inflate(R.layout.dialog_select_freight, null);
        //텍스트 변경을 위한 버튼 생성
        final Button btn=(Button)findViewById(R.id.select_freight_kind);
        //선택 버튼
        Button small=(Button)layout.findViewById(R.id.small);
        Button medium=(Button)layout.findViewById(R.id.medium);
        Button large=(Button)layout.findViewById(R.id.large);
        Button extra_large=(Button)layout.findViewById(R.id.extralarge);

        small.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText("화물 종류: 소형");
            }
        });
        medium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText("화물 종류: 중형");
            }
        });
        large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText("화물 종류: 대형");
            }
        });
        extra_large.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn.setText("화물 종류: 초대형");
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
        builder.setTitle("화물 종류 선택")
                .setView(layout)
                .setCancelable(false)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                    }
                });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void LogOut(View v) { //로그아웃 메서드
        try { //로그인 유지 해제
            File logind=new File(getFilesDir()+Load.LoginFile);
            BufferedWriter bw=new BufferedWriter(new FileWriter(logind, false));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent login_page=new Intent(Main.this, Login.class);
        startActivity(login_page);
        finish();
        //다시 로그인 페이지로 이동

    }

    private LocationManager locationManager;
    private LocationListener locationListener;
    /**
     * 사용자의 위치를 수신
     */
    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MInteger.REQUEST_CODE_LOCATION);
        }
        else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
                Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
            }
        }
        return currentLocation;
    }

    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // TODO 위도, 경도로 하고 싶은 것
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    //현재위치 버튼
    public void Print_Location(Location location, TextView current_Location) { //TextView를 현재 주소로 변경하는 메서드
        if(location!=null) {
            latitude=location.getLatitude();
            longitude=location.getLongitude();

            Geocoder geocoder=new Geocoder(getApplicationContext());
            List<Address> list=null;
            try {
                list=geocoder.getFromLocation(latitude, longitude, 10);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "위치 변환 오류", Toast.LENGTH_SHORT).show();
            }
            if(list!=null) {
                if(list.size()==0) Toast.makeText(getApplicationContext(), "해당되는 주소가 없습니다", Toast.LENGTH_SHORT).show();
                else {
                    String result_location=list.get(0).toString().split("\"")[1];
                    current_Location.setText(result_location);
                }
            }
        }
        else Toast.makeText(getApplicationContext(), "현재위치를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
    }

    public void move_map(View v) {
        Intent map=new Intent(Main.this, SelectMap.class);
        startActivity(map);
    }

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() { //뒤로가기 2번 눌러 종료
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            System.exit(0);
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
