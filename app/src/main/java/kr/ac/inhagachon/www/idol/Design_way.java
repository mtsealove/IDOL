package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Design_way extends AppCompatActivity {
    Logic[] logics;

    int count;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_design_way);
        Intent intent=getIntent();
        count=intent.getIntExtra("count",0);

        getLogics();
        Show_brand();
        Button pay= findViewById(R.id.pay);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm();
            }
        });
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
    View layout[];
    LinearLayout background[];
    int subcat=0;

    private void Show_brand() {
        LayoutInflater inflater=getLayoutInflater();
        layout=new View[count];
        background=new LinearLayout[count];
        TextView price[]=new TextView[count];
        TextView name[]=new TextView[count];
        TextView time[]=new TextView[count];
        TextView path[]=new TextView[count];
        LinearLayout brands= findViewById(R.id.brands);
        TextView path1= findViewById(R.id.path1);
        path1.setText("1. "+logics[0].address);

        for(int i=0; i<count; i++) {
            layout[i]=inflater.inflate(R.layout.show_brand, null);
            price[i]=layout[i].findViewById(R.id.price);
            name[i]=layout[i].findViewById(R.id.name);
            time[i]=layout[i].findViewById(R.id.time);
            path[i]=layout[i].findViewById(R.id.path);
            background[i]=layout[i].findViewById(R.id.show_brand);

            final int finalI = i;
            layout[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select_item(finalI);
                }
            });
            price[i].setText(logics[i].cost+"원");
            name[i].setText(logics[i].transportation);
            int hour=logics[i].min/60, minute=logics[i].min-hour*60;
            if(hour==0) time[i].setText(minute+"분");
            else if(minute==0) time[i].setText(hour+"시간");
            else time[i].setText(hour+"시간 "+minute+"분");
            path[i].setText(logics[i].address);

            brands.addView(layout[i]);

            if(i!=count-1&&logics[i+1]!=null) {
                if(!logics[i+1].address.equals(logics[i].address)) {
                    View cat=inflater.inflate(R.layout.sub_category, null);
                    TextView catTV=cat.findViewById(R.id.path);
                    catTV.setText(logics[i+1].priority+". "+logics[i+1].address);
                    brands.addView(cat);
                    subcat++;
                }
            }
        }
    }


    Logic select[]=new Logic[3];
    private void select_item(int position) { //선택한 리스트 설정
        //선택 배경을 변경
        background[position].setBackgroundResource(R.drawable.list_background_pressed);

        switch (logics[position].priority) {
            case 1: select[0]=logics[position];
            break;
            case 2: select[1]=logics[position];
            break;
            case 3: select[2]=logics[position];
            break;
        }


        //다른 리스트 활성화 및 비활성화
        if(logics[position].priority==1) { //1번 경유지일 경우
            for(int i=0; i<count; i++) {
                if(position!=i) {
                    if (logics[i].priority == 1) { //다른 1번 경유지에 대해
                        activate(i); //모두 활성화
                    } else if (logics[i].priority == 2) { //2번 경유지에 대해
                        if (logics[position].next == logics[i].id) activate(i); //같은 계열이면 활성화
                        else disable(i); //다른 계열이면 비활성화
                    } else if(logics[i].priority==3) {
                        if(logics[position].next==logics[i].id) activate(i);
                        else disable(i);
                    }
                }
            }
        } else if(logics[position].priority==2) { //2번 경유지에 대해
            for(int i=0; i<count; i++) {
                if(position!=i) {
                    if(logics[i].priority==2&&logics[i].next==logics[position].next) activate(i);
                    else if(logics[i].priority==3&&logics[position].next==logics[i].id) activate(i);
                    else if(logics[i].priority==3&&logics[position].next!=logics[i].id) disable(i);
                }
            }
        } else if(logics[position].priority==3) {
            for(int i=0; i<count; i++) {
                if(position!=i) {
                    if(logics[i].priority==3&&logics[i].id==logics[position].id) activate(i);
                }
            }
        }
        calculate();
    }

    private void disable(int position) {
        background[position].setBackgroundResource(R.drawable.list_background_notable);
        background[position].setClickable(false);
        background[position].setVisibility(View.GONE);
        try {
        for(int i=0; i<3; i++) {
            if(select[i].equals(logics[position])) select[i]=null;
        } } catch (NullPointerException e) {

        }
    }
    private void activate(int position) {
        background[position].setClickable(true);
        background[position].setBackgroundResource(R.drawable.list_background);
        background[position].setVisibility(View.VISIBLE);
    }
    private void calculate() { //시간과 비용의 합을 계산하여 출력
        TextView timeTV= findViewById(R.id.show_time);
        Button payTV= findViewById(R.id.pay);
        int total_min=0;
        int total_cost=0;

        try { //각 합을 모두 더함
            for(int i=0; i<3; i++) {
                total_min+=select[i].min;
                total_cost+=select[i].cost;
            }
        } catch (NullPointerException e) {

        }finally {
            int hour=total_min/60;
            int min=total_min-(hour*60);
            String show_time;
            if(hour==0) show_time=min+"분";
            else if(min==0) show_time=hour+"시간";
            else show_time=hour+"시간 "+min+"분";
            timeTV.setText(show_time);
            payTV.setText(total_cost+"원");
        }
    }
    private int check_max_count() { //경로의 최대치를 반환
        int max=2;
        for(int i=0; i<count; i++) {
            if(logics[i].priority==3) max=3;
        }
        return max;
    }
    private void confirm() { //최종 확인
        boolean done=true;

        for(int i=0; i<check_max_count(); i++) {
            if(select[i]==null) done=false;
        }

        if(!done) Toast.makeText(Design_way.this, "모든 경로를 설정하지 않았습니다.", Toast.LENGTH_SHORT).show();
        else {
            Show_way.flex=new Logic[check_max_count()];
            Show_way.isConfirm=true;
            for(int i=0; i<check_max_count(); i++) {
                Show_way.flex[i] = select[i];
                Show_way.total_cost+=select[i].cost;
            }
            Toast.makeText(Design_way.this, "경로가 설정되었습니다", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

}
