package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.ColorRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.Projection;

public class Desing_way extends AppCompatActivity {
    Logic[] logics;

    int count;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_design_way);
        Intent intent=getIntent();
        count=intent.getIntExtra("count",0);

        String startL=intent.getStringExtra("saddress");
        String destinationL=intent.getStringExtra("daddress");
        TextView start= findViewById(R.id.slocation);
        TextView destination= findViewById(R.id.dlocation);
        start.setText("출발: "+startL);
        destination.setText("도착: "+destinationL);
        getLogics();
        Show_brand();

    }

    private void getLogics() { //경로 받아오기
        boolean reverse=false; //db와 역순인지 확인
        Intent intent=getIntent();
        //지역 위치 받아옴 ex)서울-대전
        String location1=intent.getStringExtra("location1");
        String location2=intent.getStringExtra("location2");


        //함수 내에 새 로직 생성
        logics=new Logic[count];
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
    }
    private void Show_brand() {
        LayoutInflater inflater=getLayoutInflater();
        View layout[]=new View[count];
        TextView price[]=new TextView[count];
        TextView name[]=new TextView[count];
        TextView time[]=new TextView[count];
        TextView path[]=new TextView[count];
        LinearLayout brands= findViewById(R.id.brands);
        TextView path1= findViewById(R.id.path1);
        path1.setText(logics[0].address);
        TextView path2= findViewById(R.id.path2);
        TextView path3= findViewById(R.id.path3);

        for(int i=0; i<count; i++) {
            layout[i]=inflater.inflate(R.layout.show_brand, null);
            price[i]=layout[i].findViewById(R.id.price);
            name[i]=layout[i].findViewById(R.id.name);
            time[i]=layout[i].findViewById(R.id.time);
            path[i]=layout[i].findViewById(R.id.path);
            price[i].setText(logics[i].cost+"원");
            name[i].setText(logics[i].transportation);
            time[i].setText(logics[i].min+"분");
            path[i].setText(logics[i].address);
            brands.addView(layout[i]);
            if(i!=count-1&&logics[i+1]!=null) {
                if(logics[i+1].priority!=logics[i].priority) {
                    TextView pathTV=new TextView(Desing_way.this);
                    pathTV.setText(logics[i+1].address);
                    pathTV.setTextColor(Color.BLACK);
                    pathTV.setBackgroundColor(Color.parseColor("#FFCC00"));
                    pathTV.setTextSize(20);
                    brands.addView(pathTV);
                }
            }
        }
    }
}
