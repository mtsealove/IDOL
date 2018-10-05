package kr.ac.inhagachon.www.idol;

import android.app.FragmentManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SelectMap extends AppCompatActivity implements OnMapReadyCallback {
    LatLng start_location;
    LatLng destination_location;
    LatLng select_location;
    String address;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_map);

            //구글맵 표시 및 위치 출력
            FragmentManager fragmentManager = getFragmentManager();
            MapFragment mapFragment = (MapFragment)fragmentManager
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            final Button selectbtn=(Button)findViewById(R.id.select_location);
            selectbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_location(true);
                    finish();
                }
            });

        }

        @Override
        public void onMapReady(final GoogleMap map) {
            //기본적으로 표시될 위치
            double latitude, longitude;
            String title;
            try {
                latitude=Main.latitude;
                longitude=Main.longitude;
                title="현재 위치";
            } catch (NullPointerException e) {
                latitude=37.56;
                longitude=126.97;
                title="서울";
            }

            final LatLng current = new LatLng(latitude, longitude);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(current);
            markerOptions.title(title);
            //markerOptions.snippet("한국의 수도");
            map.addMarker(markerOptions);

            map.moveCamera(CameraUpdateFactory.newLatLng(current));
            map.animateCamera(CameraUpdateFactory.zoomTo(17));
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    MarkerOptions moptions=new MarkerOptions();
                    moptions.title("선택");
                    Double latitude=latLng.latitude;
                    Double longitude=latLng.longitude;
                    moptions.position(new LatLng(latitude, longitude));
                    map.addMarker(moptions);
                }
            });

            final Geocoder geocoder=new Geocoder(this);

            //검색 버튼 이벤트
            Button search=(Button)findViewById(R.id.search);
            final EditText input_location=(EditText)findViewById(R.id.input_location);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InputMethodManager imm=(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(input_location.getWindowToken(), 0);
                    String location=input_location.getText().toString();
                    List<Address> addressList=null;
                    try {
                        addressList =geocoder.getFromLocationName(location, 10);
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(), "잘못된 주소입니다", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    String[] splitstr=addressList.get(0).toString().split(",");
                    address=splitstr[0].substring(splitstr[0].indexOf("\"")+1, splitstr[0].length()-2);

                    double latitude = Double.parseDouble(splitstr[10].substring(splitstr[10].indexOf("=") + 1));
                    double longitude = Double.parseDouble(splitstr[12].substring(splitstr[12].indexOf("=") + 1));

                    //선택 위치 좌표
                    select_location=new LatLng(latitude, longitude);
                    MarkerOptions markerOptions1=new MarkerOptions();
                    markerOptions1.title("선택 위치");
                    markerOptions1.snippet(address);
                    markerOptions1.position(select_location);
                    map.addMarker(markerOptions1);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(select_location, 17));
                }
            });
        }

        //선택 버튼 이벤트
    public void select_location(boolean isStart) {
        LayoutInflater inflater=getLayoutInflater();
        View main=inflater.inflate(R.layout.activity_main, null);
        TextView label;
        if(select_location==null) Toast.makeText(getApplicationContext(), "위치를 선택하세요", Toast.LENGTH_SHORT).show();
        else {
            if(isStart) {
                label=(TextView)main.findViewById(R.id.start_location);
            }
            else {
                label=(TextView)main.findViewById(R.id.destination_location);
            }
            label.setText(address);
        }
    }

}
