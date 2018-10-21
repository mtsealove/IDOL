package kr.ac.inhagachon.www.idol;

import android.Manifest;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectMap extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    LatLng start_location;
    LatLng destination_location;
    LatLng select_location;

    double latitude;
    double longitude;
    double clatitude, clongitude;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //현재 위치 구하기
        settingGPS();
        getMyLocation();

        //구글맵 표시 및 위치 출력
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ListView adlb = findViewById(R.id.address);
        adlb.setVisibility(View.GONE);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        //기본적으로 표시될 위치
        double latitude, longitude;
        String title;
        try {
            latitude = this.clatitude;
            longitude = this.clongitude;
            title = "현재 위치";
        } catch (NullPointerException e) {
            latitude = 37.450754;
            longitude = 127.128837;
            title = "가천대학교";
        }


        final LatLng current = new LatLng(latitude, longitude);

        map.setOnMarkerClickListener(this);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(current);
        markerOptions.title(title);
        markerOptions.snippet("현재 위치 선택");
        map.addMarker(markerOptions);

        map.moveCamera(CameraUpdateFactory.newLatLng(current));
        map.animateCamera(CameraUpdateFactory.zoomTo(17));
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions moptions = new MarkerOptions();
                moptions.title("선택");
                Double latitude = latLng.latitude;
                Double longitude = latLng.longitude;
                moptions.snippet("위치 선택");
                moptions.position(new LatLng(latitude, longitude));
                map.addMarker(moptions);
            }
        });


        //검색 버튼 이벤트
        ImageView search = findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_location(map);
            }
        });
        EditText input = findViewById(R.id.input_location);
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        search_location(map);
                        break;
                    default:
                        return false;
                }
                return true;
            }
        });

    }

    public void search_location(GoogleMap map) {
        Geocoder geocoder = new Geocoder(this);
        EditText input_location = findViewById(R.id.input_location);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input_location.getWindowToken(), 0);
        String location = input_location.getText().toString();
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(location, 10);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "잘못된 주소입니다", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        String[] str0 = addressList.get(0).toString().split(",");
        address = str0[0].substring(str0[0].indexOf("\"") + 1, str0[0].length() - 2);

        String[][] str = new String[addressList.size()][];
        String[] ads = new String[addressList.size()];
        for (int i = 0; i < addressList.size(); i++) {
            str[i] = addressList.get(i).toString().split(",");
            ads[i] = str[i][0].substring(str[i][0].indexOf("\"") + 1, str[i][0].length() - 2);
        }


        double latitude0 = Double.parseDouble(str0[10].substring(str0[10].indexOf("=") + 1));
        double longitude0 = Double.parseDouble(str0[12].substring(str0[12].indexOf("=") + 1));
        latitude = latitude0;
        longitude = longitude0;

        //선택 위치 좌표
        select_location = new LatLng(latitude0, longitude0);
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.title("선택 위치");
        markerOptions1.snippet(address);
        markerOptions1.position(select_location);
        map.addMarker(markerOptions1);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(select_location, 17));
        TextView tmp_result = findViewById(R.id.tmp_result);

        //임시 텍스트 삭제
        tmp_result.setVisibility(View.GONE);
        //레이아웃에 새 뷰 추가
        ListView listView = findViewById(R.id.address);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, ads);
        listView.setAdapter(adapter);
        listView.setVisibility(View.VISIBLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = getIntent();
                boolean isStart = intent.getBooleanExtra("isStart", true);
                select_location(isStart);
                finish();
            }
        });

    }

    //선택 버튼 이벤트
    public void select_location(boolean isStart) {

        if (select_location == null)
            Toast.makeText(getApplicationContext(), "위치를 선택하세요", Toast.LENGTH_SHORT).show();
        else {
            //Toast.makeText(getApplicationContext(), "주소: "+address, Toast.LENGTH_SHORT).show();
            if (isStart) {
                Intent intent = new Intent();
                intent.putExtra("address", address);
                Main.slongitude = longitude;
                Main.slatitude = latitude;
                setResult(RESULT_OK, intent);
            } else {
                Intent intent = new Intent();
                intent.putExtra("address2", address);
                Main.dlongitude = longitude;
                Main.dlatitude = latitude;
                setResult(RESULT_OK, intent);
            }

        }
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
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                clongitude = currentLocation.getLongitude();
                clatitude = currentLocation.getLatitude();
            }
        }
        return currentLocation;
    }

    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    @Override
    public boolean onMarkerClick(Marker marker) {//마커를 클릭시 해당 위치 반환
        latitude = marker.getPosition().latitude;
        longitude = marker.getPosition().longitude;
        Geocoder geocoder = new Geocoder(this, Locale.KOREA);
        List<Address> addressList = null;
        try {
            if (geocoder != null)
                addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0)
                address = addressList.get(0).getAddressLine(0).toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        boolean isStart = intent.getBooleanExtra("isStart", true);
        select_location(isStart);
        finish();
        return true;
    }
}

