package kr.ac.inhagachon.www.idol;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;

public class Show_way extends AppCompatActivity implements OnMapReadyCallback {
    String start_address, destination_address;
    static int total_cost;
    static Logic[] flex; //경유지 리스트
    static boolean isConfirm=false;
    boolean reverse;

    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_show_way);

        ImageView back= findViewById(R.id.goback);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //최소비용, 최단거리 표시
        Intent intent=getIntent();
        String title=intent.getStringExtra("title");
        TextView titlet= findViewById(R.id.title);
        titlet.setText(title);


        //지도 이용 설정
        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment) fragmentManager
                .findFragmentById(R.id.map_for_show);
        mapFragment.getMapAsync(this);

        Button toggle_detail= findViewById(R.id.detail);
        toggle_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout= findViewById(R.id.detail_layout);
                LinearLayout simple= findViewById(R.id.btml);
                layout.setVisibility(View.VISIBLE);
                simple.setVisibility(View.GONE);
            }
        });
        Button toggle_simple= findViewById(R.id.simple);
        toggle_simple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout layout= findViewById(R.id.detail_layout);
                LinearLayout simple= findViewById(R.id.btml);
                layout.setVisibility(View.GONE);
                simple.setVisibility(View.VISIBLE);
            }
        });

        View.OnClickListener confirm=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isConfirm=true;
                Toast.makeText(getApplicationContext(), "경로가 설정되었습니다", Toast.LENGTH_SHORT).show();
                finish();
            }
        };

        Button pay= findViewById(R.id.pay);
        Button pay2= findViewById(R.id.pay2);
        pay.setOnClickListener(confirm);
        pay2.setOnClickListener(confirm);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        Geocoder geocoder=new Geocoder(Show_way.this);
        //화면상에 표시될 주소
        TextView slocation= findViewById(R.id.slocation);
        TextView dlocation= findViewById(R.id.dlocation);
        //메인 페이지에서 표시될 주소를 가져옴
        Intent intent=getIntent();
        start_address=intent.getStringExtra("saddress");
        destination_address=intent.getStringExtra("daddress");
        slocation.setText("출발: "+start_address);
        dlocation.setText("도착: "+destination_address);
        Log.d("start address:", start_address);
        Log.d("destination addredss: ", destination_address);

        List<Address> startl=null;
        List<Address> destnl=null;
        try {
            startl=geocoder.getFromLocationName(start_address, 10);
            destnl=geocoder.getFromLocationName(destination_address, 10);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final LatLng start=new LatLng(startl.get(0).getLatitude(), startl.get(0).getLongitude());
        final LatLng desitnation=new LatLng(destnl.get(0).getLatitude(), destnl.get(0).getLongitude());
        //위치 객체 생성

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
        if(!reverse) polylineOptions.add(start);
        else polylineOptions.add(desitnation);


        String title=intent.getStringExtra("title");
        if(title.equals("최소비용")) flex=find_low_cost();
        else if(title.equals("최단시간")) flex=find_short_way();
        else if(title.equals("추천경로")) flex=find_suggest_way();
        //경로를 화면에 출력
        LinearLayout detail= findViewById(R.id.detail_list);
        LayoutInflater inflater=getLayoutInflater();

        int ttime=0, tcost=0;
        for(int i=0; i<flex.length; i++) {
            if(flex[i]!=null) {
                View list=inflater.inflate(R.layout.show_brand, null);
                TextView name= list.findViewById(R.id.name);
                TextView price= list.findViewById(R.id.price);
                TextView time= list.findViewById(R.id.time);
                TextView path=list.findViewById(R.id.path);
                name.setText(flex[i].transportation);
                price.setText(flex[i].cost+"원");
                time.setText(flex[i].min/60+"시간 "+flex[i].min%60+"분");
                path.setText(flex[i].address);
                tcost+=flex[i].cost;
                ttime+=flex[i].min;
                detail.addView(list);
            }
        }
        TextView total_time= findViewById(R.id.total_time);
        Button pay2= findViewById(R.id.pay2);
        pay2.setText(tcost+"원");
        total_time.setText(ttime/60+"시간 "+ttime%60+"분");
        total_cost=tcost;

        //경유지 통과
        LatLng[] flexLatlng=new LatLng[flex.length];

        int flexcount=0;
        List<Address>[] list=new List[flex.length];
        try {
            for(int i=0; i<flex.length; i++) {
                list[i] = geocoder.getFromLocationName(flex[i].address, 10);
                flexcount++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {}
        for(int i=0; i<flexcount; i++) {
            if(list[i]!=null) {
                if(list[i].size()!=0) flexLatlng[i]=new LatLng(list[i].get(0).getLatitude(), list[i].get(0).getLongitude());
            }
        }
        for(int i=0; i<flexcount; i++)
            if(flexLatlng[i]!=null) polylineOptions.add(flexLatlng[i]);
        if(!reverse) polylineOptions.add(desitnation);
        else polylineOptions.add(start);
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
        Main.pb.setVisibility(View.GONE);
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

    public Logic[] find_short_way() { //최단경로 찾기
        Intent intent=getIntent();
        boolean reverse=false; //db와 역순인지 확인

        //지역 위치 받아옴 ex)서울-대전
        String location1=intent.getStringExtra("location1");
        String location2=intent.getStringExtra("location2");
        int count=intent.getIntExtra("count",0);
        //함수 내에 새 로직 생성
        Logic[] logics=new Logic[count];
        int index=0;

        //지역 위치가 같으면 추가
        for(int i=0; i<Load.logics.length; i++) {
            if(location1.equals(Load.logics[i].location)) { //위치 검색하기
                logics[index++]=Load.logics[i];
                reverse=false;
                if(index==count) break;
            }
            else if(location2.equals(Load.logics[i].location)) {
                logics[index++]=Load.logics[i];
                reverse=true;
                if(index==count) break;
            }
        }
        this.reverse=reverse;
        int ll=0;
        Logic[] best=new Logic[3];
        Logic[] best2=new Logic[3];

        //시간이 가장 빠른 경우 계산
        //방법 1 계산
        for(int i=0; i<logics.length; i++) {
            switch (logics[i].priority) {
                case 1: //best에 대한 케이스
                    if(best[0]==null&&logics[i].next==1) best[0]=logics[i];
                    else if(best[0]!=null){
                        if(logics[i].min<best[0].min) best[0]=logics[i];
                    }
                    break;
                case 2:
                    //이전 best에서 1을 택한 경우
                    if(best[0]==null) break;
                    if(best[1]==null&&logics[i].id==best[0].next) best[1]=logics[i];
                    else if(best[1]!=null) {
                        if(logics[i].min<best[1].min) best[1]=logics[i];
                    }
                    break;
                case 3:
                    if(best[1].next==0) break;
                    if(best[2]==null&&logics[i].id==best[1].next) best[2]=logics[i];
                    else if(best[2]!=null){
                        if(logics[i].min<best[2].min) best[2]=logics[i];
                    }
                    break;
            }
        }

        //방법 2 계산
        for(int i=0; i<logics.length; i++) {
            switch (logics[i].priority) {
                case 1: //best에 대한 케이스
                    if(best2[0]==null&&logics[i].next==2) best2[0]=logics[i];
                    else if(best2[0]!=null){
                        if(logics[i].min<best2[0].min) best2[0]=logics[i];
                    }
                    break;
                case 2:
                    //이전 best에서 1을 택한 경우
                    if(best2[0]==null) break;
                    if(best2[1]==null&&logics[i].id==best2[0].next) best2[1]=logics[i];
                    else if(best2[1]!=null){
                        if(logics[i].min<best2[1].min) best2[1]=logics[i];
                    }
                    break;
                case 3:
                    if(best2[1].next==0) break;
                    if(best2[2]==null&&logics[i].id==best2[1].next) best2[2]=logics[i];
                    else if(best2[2]!=null){
                        if(logics[i].min<best2[2].min) best2[2]=logics[i];
                    }
                    break;
            }
        }
        //각각 시간을 계산
        int count1=0, count2=0;
        int min1=0, min2=0, cost1=0, cost2=0;
        for(int i=0; i<3; i++) {
            try {
                min1+=best[i].min;
                cost1+=best[i].cost;
                count1++;
            } catch (NullPointerException e) {
                break;
            }
        }
        for(int i=0; i<3; i++) {
            try {
                min2+=best2[i].min;
                cost2+=best2[i].cost;
                count2++;
            } catch (NullPointerException e) {
                break;
            }
        }

        //가장 좋은 3*2개의 수단을 뽑아 내었음
        int min=0, cost=0; //최종 시간, 비용 계산
        int i=0;
        String result=""; //결과를 표출할 텍스트

        //최단 시간에 따른 최종 결과 반환
        if(min1<min2&&count1==count2) {
            min=min1;
            cost=cost1;
        }
        else if(min1>min2&&count1==count2){
            min=min2;
            cost=cost2;
        }
        else if(count1>=count2) {
            min=min1;
            cost=cost1;
        } else {
            min=min2;
            cost=cost2;
        }

        //결과를 화면에 표시
        Button pay= findViewById(R.id.pay);
        pay.setText(cost+"원");
        TextView path= findViewById(R.id.path);
        result+="시간: "+ min/60 +"시간 "+min%60+"분";
        path.setText(result);
        if(min1<min2) return best;
        else return best2;
    }

    public Logic[] find_low_cost() { //최소비용 계산 메서드
        Intent intent=getIntent();
        boolean reverse=false; //db와 역순인지 확인

        //지역 위치 받아옴 ex)서울-대전
        String location1=intent.getStringExtra("location1");
        String location2=intent.getStringExtra("location2");
        int count=intent.getIntExtra("count",0);

        //함수 내에 새 로직 생성
        Logic[] logics=new Logic[count];
        int index=0;

        //지역 위치가 같으면 추가
        for(int i=0; i<Load.logics.length; i++) {
            if(location1.equals(Load.logics[i].location)) { //위치 검색하기
                logics[index++]=Load.logics[i];
                reverse=false;
                if(index==count) break;
            }
            else if(location2.equals(Load.logics[i].location)) {
                logics[index++]=Load.logics[i];
                reverse=true;
                if(index==count) break;
            }
        }
        this.reverse=reverse;
        int ll=0;
        Logic[] best=new Logic[3];
        Logic[] best2=new Logic[3];

        //시간이 가장 빠른 경우 계산
        //방법 1 계산
        for(int i=0; i<logics.length; i++) {
            switch (logics[i].priority) {
                case 1: //best에 대한 케이스
                    if(best[0]==null&&logics[i].next==1) best[0]=logics[i];
                    else if(best[0]!=null){
                        if(logics[i].cost<=best[0].cost) best[0]=logics[i];
                    }
                    break;
                case 2:
                    //이전 best에서 1을 택한 경우
                    if(best[0]==null) break;
                    if(best[1]==null&&logics[i].id==best[0].next) best[1]=logics[i];
                    else if(best[1]!=null) {
                        if(logics[i].cost<=best[1].cost) best[1]=logics[i];
                    }
                    break;
                case 3:
                    if(best[1]==null) break;
                    if(best[1].next==0) break;
                    if(best[2]==null&&logics[i].id==best[1].next) best[2]=logics[i];
                    else if(best[2]!=null){
                        if(logics[i].cost <=best[2].cost) best[2]=logics[i];
                    }
                    break;
            }
        }

        //방법 2 계산
        for(int i=0; i<logics.length; i++) {
            switch (logics[i].priority) {
                case 1: //best에 대한 케이스
                    if(best2[0]==null&&logics[i].next==2) best2[0]=logics[i];
                    else if(best2[0]!=null){
                        if(logics[i].cost<=best2[0].cost) best2[0]=logics[i];
                    }
                    break;
                case 2:
                    //이전 best에서 1을 택한 경우
                    if(best2[0]==null) break;
                    if(best2[1]==null&&logics[i].id==best2[0].next) best2[1]=logics[i];
                    else if(best2[1]!=null){
                        if(logics[i].cost<=best2[1].cost) best2[1]=logics[i];
                    }
                    break;
                case 3:
                    if(best2[1]==null) break;
                    if(best2[1].next==0) break;
                    if(best2[2]==null&&logics[i].id==best2[1].next) best2[2]=logics[i];
                    else if(best2[2]!=null){
                        if(logics[i].cost<=best2[2].cost) best2[2]=logics[i];
                    }
                    break;
            }
        }
        //각각 시간을 계산
        int count1=0, count2=0;
        int min1=0, min2=0, cost1=0, cost2=0;
        for(int i=0; i<3; i++) {
            try {
                min1+=best[i].min;
                cost1+=best[i].cost;
                count1++;
            } catch (NullPointerException e) {
                break;
            }
        }
        for(int i=0; i<3; i++) {
            try {
                min2+=best2[i].min;
                cost2+=best2[i].cost;
                count2++;
            } catch (NullPointerException e) {
                break;
            }
        }

        //가장 좋은 3*2개의 수단을 뽑아 내었음

        int min=0, cost=0; //최종 시간, 비용 계산
        int i=0;
        String result=""; //결과를 표출할 텍스트

        //최단 시간에 따른 최종 결과 반환
        if(cost1<cost2&&count1==count2) {
            min=min1;
            cost=cost1;
             }
        else if(cost1>cost2&&count1==count2){
            min=min2;
            cost=cost2;
        } else if(count1>count2) {
            min=min1;
            cost=cost1;
        } else {
            min=min2;
            cost=cost2;
        }

        //결과를 화면에 표시
        Button pay= findViewById(R.id.pay);
        pay.setText(cost+"원");
        TextView path= findViewById(R.id.path);
        result+="시간: "+ min/60 +"시간 "+min%60+"분";
        path.setText(result);
        if(cost1<cost2) return best;
        else return best2;
    }

    public Logic[] find_suggest_way() { //추천경로 찾기
        Intent intent=getIntent();
        boolean reverse=false; //db와 역순인지 확인

        //지역 위치 받아옴 ex)서울-대전
        String location1=intent.getStringExtra("location1");
        String location2=intent.getStringExtra("location2");
        int count=intent.getIntExtra("count",0);
        //함수 내에 새 로직 생성
        Logic[] logics=new Logic[count];
        int index=0;

        //지역 위치가 같으면 추가
        for(int i=0; i<Load.logics.length; i++) {
            if(location1.equals(Load.logics[i].location)) { //위치 검색하기
                logics[index++]=Load.logics[i];
                reverse=false;
                if(index==count) break;
            }
            else if(location2.equals(Load.logics[i].location)) {
                logics[index++]=Load.logics[i];
                reverse=true;
                if(index==count) break;
            }
        }
        this.reverse=reverse;
        int ll=0;
        Logic[] best=new Logic[3];
        Logic[] best2=new Logic[3];

        //가성비가 가장 좋은 경로 계산
        //방법 1 계산
        for(int i=0; i<logics.length; i++) {
            switch (logics[i].priority) {
                case 1: //best에 대한 케이스
                    if(best[0]==null&&logics[i].next==1) best[0]=logics[i];
                    else if(best[0]!=null){
                        if(logics[i].rate>best[0].rate) best[0]=logics[i];
                    }
                    break;
                case 2:
                    //이전 best에서 1을 택한 경우
                    if(best[0]==null) break;
                    if(best[1]==null&&logics[i].id==best[0].next) best[1]=logics[i];
                    else if(best[1]!=null) {
                        if(logics[i].rate>best[1].rate) best[1]=logics[i];
                    }
                    break;
                case 3:
                    if(best[1].next==0) break;
                    if(best[2]==null&&logics[i].id==best[1].next) best[2]=logics[i];
                    else if(best[2]!=null){
                        if(logics[i].rate>best[2].rate) best[2]=logics[i];
                    }
                    break;
            }
        }

        //방법 2 계산
        for(int i=0; i<logics.length; i++) {
            switch (logics[i].priority) {
                case 1: //best에 대한 케이스
                    if(best2[0]==null&&logics[i].next==2) best2[0]=logics[i];
                    else if(best2[0]!=null){
                        if(logics[i].rate>best2[0].rate) best2[0]=logics[i];
                    }
                    break;
                case 2:
                    //이전 best에서 1을 택한 경우
                    if(best2[0]==null) break;
                    if(best2[1]==null&&logics[i].id==best2[0].next) best2[1]=logics[i];
                    else if(best2[1]!=null){
                        if(logics[i].rate>best2[1].rate) best2[1]=logics[i];
                    }
                    break;
                case 3:
                    if(best2[1].next==0) break;
                    if(best2[2]==null&&logics[i].id==best2[1].next) best2[2]=logics[i];
                    else if(best2[2]!=null){
                        if(logics[i].rate>best2[2].rate) best2[2]=logics[i];
                    }
                    break;
            }
        }
        //각각 시간을 계산
        int count1=0, count2=0;
        int min1=0, min2=0, cost1=0, cost2=0;
        double rate1=0, rate2=0;
        for(int i=0; i<3; i++) {
            try {
                min1+=best[i].min;
                cost1+=best[i].cost;
                rate1+=best[i].rate;
                count1++;
            } catch (NullPointerException e) {
                break;
            }
        }
        for(int i=0; i<3; i++) {
            try {
                min2+=best2[i].min;
                cost2+=best2[i].cost;
                rate2+=best2[i].rate;
                count2++;
            } catch (NullPointerException e) {
                break;
            }
        }

        //가장 좋은 3*2개의 수단을 뽑아 내었음
        int min=0, cost=0; //최종 시간, 비용 계산
        int i=0;
        String result=""; //결과를 표출할 텍스트

        //최단 시간에 따른 최종 결과 반환
        if(rate1>rate2&&count1==count2) {
            min=min1;
            cost=cost1;
        }
        else if(rate1<rate2&&count1==count2){
            min=min2;
            cost=cost2;
        }
        else if(count1>=count2) {
            min=min1;
            cost=cost1;
        } else {
            min=min2;
            cost=cost2;
        }

        //결과를 화면에 표시
        Button pay= findViewById(R.id.pay);
        pay.setText(cost+"원");
        TextView path= findViewById(R.id.path);
        result+="시간: "+ min/60 +"시간 "+min%60+"분";
        path.setText(result);
        if(rate1>rate2) return best;
        else return best2;
    }

}

