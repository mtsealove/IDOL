package kr.ac.inhagachon.www.idol;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class LowBenefit extends AppCompatActivity implements OnMapReadyCallback {
    LatLng[] flexlatlng;
    String start_address, destination_address;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_low_benefit);
        Resources res=getResources();
        String[] sname=res.getStringArray(R.array.flex_name);
        String[] sflexlat=res.getStringArray(R.array.flex_lat);
        String[] sflexlon=res.getStringArray(R.array.flex_lon);
        double[] flexlat=new double[sflexlat.length];
        double[] flexlon=new double[sflexlon.length];
        flexlatlng=new LatLng[sname.length];


        //위도와 경도를 숫자화 xml에서 가져옴
        for(int i=0; i<sflexlat.length; i++) {
            flexlat[i]=Double.parseDouble(sflexlat[i]);
            flexlon[i]=Double.parseDouble(sflexlon[i]);
            flexlatlng[i]=new LatLng(flexlat[i], flexlon[i]); //위경도 인스턴스 생성
        }

        //화면상에 표시될 주소
        TextView slocation=(TextView)findViewById(R.id.slocation);
        TextView dlocation=(TextView)findViewById(R.id.dlocation);
        //메인 페이지에서 표시될 주소를 가져옴
        Intent intent=getIntent();
        start_address=intent.getStringExtra("saddress");
        destination_address=intent.getStringExtra("daddress");
        slocation.setText("출발: "+start_address);
        dlocation.setText("도착: "+destination_address);

        //금액
        Button pay=(Button)findViewById(R.id.pay);
        pay.setText(Main.final_bill+"원");

        //지도 이용 설정
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    static int[] area; //경유지를 지날 리스트
    @Override
    public void onMapReady(GoogleMap map) {
        //Main클래스에서 위치를 가져옴
        double slatitude, slongitude;
            slatitude = Main.slatitude;
            slongitude = Main.slongitude;
        double dlatitude, dlongitude;
            dlatitude=Main.dlatitude;
            dlongitude=Main.dlongitude;
        //위치 객체 생성
        final LatLng start=new LatLng(slatitude, slongitude);
        final LatLng desitnation=new LatLng(dlatitude, dlongitude);

        //마커 표시
        MarkerOptions mo1=new MarkerOptions();
        mo1.position(start);
        mo1.title("출발");
        MarkerOptions mo2=new MarkerOptions();
        mo2.position(desitnation);
        mo2.title("도착");
        map.addMarker(mo1);
        map.addMarker(mo2);

        //그림 그리기
        PolylineOptions polylineOptions=new PolylineOptions();
        //중간지점 통과
        polylineOptions.add(start);
        //거리 계산
        /*
        double[] distance=new double[flexlatlng.length];
        for(int i=0; i<flexlatlng.length; i++) {
            distance[i]=distance(start, flexlatlng[i]);
        }
        for(int i=0; i<flexlatlng.length; i++) { //계산된 거리를 바탕으로 정렬
            for(int j=0; j<i; j++) {
                if(distance[i]<distance[j]) {
                    double tmp=distance[i];
                    distance[i]=distance[j];
                    distance[j]=tmp;
                    LatLng latLngtmp=flexlatlng[i];
                    flexlatlng[i]=flexlatlng[j];
                    flexlatlng[j]=latLngtmp;
                }
            }
        }

        for(int i=0; i<flexlatlng.length; i++) //가까운 위치부터 경유
            polylineOptions.add(flexlatlng[i]);
            */
        //근처에 있는 지점을 설정하여 주소가 해당 문자가 포함되면 지나게 설정
        Resources res=getResources();
        String[] nearby=res.getStringArray(R.array.nearby);
        double distance=0; //거리를 구할 변수
        for(int i=0; i<nearby.length; i++) {
            if(start_address.contains(nearby[i])) {
                    if(distance(start, desitnation)>distance(start, flexlatlng[i])&&distance+distance(start, flexlatlng[i])<distance(start, desitnation))
                        polylineOptions.add(flexlatlng[i]);
            }
        }
        for(int i=0; i<nearby.length; i++) {
            if(destination_address.contains(nearby[i])) {
                if(distance(start, desitnation)>distance(desitnation, flexlatlng[i])&&distance+distance(desitnation, flexlatlng[i])<distance(desitnation, start))
                    polylineOptions.add(flexlatlng[i]);
            }
        }

        polylineOptions.add(desitnation);
        //위치 모양 설정
        polylineOptions.width(30);
        polylineOptions.color(Color.BLUE);
        map.addPolyline(polylineOptions);
        //중간 지점으로 시점 이동
        map.moveCamera(CameraUpdateFactory.newLatLng(midPoint(start, desitnation)));
        int v;
        double sddistnace=distance(start, desitnation);
        if(sddistnace>250) v=7;
        else if(sddistnace>170) v=8;
        else if(sddistnace>70) v=9;
        else if(sddistnace>30) v=10;
        else v=11;
        map.animateCamera(CameraUpdateFactory.zoomTo(v));
    }

    private LatLng midPoint(LatLng lt1, LatLng lt2){ //위경도 중간지점 반환
        double lat1=lt1.latitude;
        double lat2=lt2.latitude;
        double lon1=lt1.longitude;
        double lon2=lt2.longitude;
        double dLon = Math.toRadians(lon2 - lon1);
        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        double midlatitiude, midlongitude;
        midlatitiude=Math.toDegrees(lat3);
        midlongitude=Math.toDegrees(lon3);

        return (new LatLng(midlatitiude, midlongitude));
    }

    private double distance(LatLng latLng1, LatLng latLng2) { //위도 경로로 거리 계산
        double lat1=latLng1.latitude;
        double lon1=latLng1.longitude;
        double lat2=latLng2.latitude;
        double lon2=latLng2.longitude;
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;

        return (dist);
    }

    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }


}

