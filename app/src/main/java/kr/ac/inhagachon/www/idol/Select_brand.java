package kr.ac.inhagachon.www.idol;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;


public class Select_brand extends AppCompatActivity implements View.OnClickListener { //업체 표시 다이얼로그
    static String final_price, final_name, final_time;

    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.dialog_select_brand);

        TabHost tabHost1 = (TabHost) findViewById(R.id.tabHost1) ;
        tabHost1.setup() ; // 첫 번째 Tab. (탭 표시 텍스트:"TAB 1"), (페이지 뷰:"content1")
        TabHost.TabSpec ts1 = tabHost1.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.suggest) ;
        ts1.setIndicator("추천 순") ;
        tabHost1.addTab(ts1) ;
        // 두 번째 Tab. (탭 표시 텍스트:"TAB 2"), (페이지 뷰:"content2")
        TabHost.TabSpec ts2 = tabHost1.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.short_way) ;
        ts2.setIndicator("최단 시간") ;
        tabHost1.addTab(ts2) ;
        // 세 번째 Tab. (탭 표시 텍스트:"TAB 3"), (페이지 뷰:"content3")
        TabHost.TabSpec ts3 = tabHost1.newTabSpec("Tap Spec 3") ;
        ts3.setContent(R.id.low_benefit) ;
        ts3.setIndicator("최소 비용") ;
        tabHost1.addTab(ts3) ;

        //브랜드별 가격, 시간에 대한 리스트 생성
        Resources res=getResources();
        double distance=Main.distance;
        String[] name=res.getStringArray(R.array.brand_name); //업체명
        String [] km=res.getStringArray(R.array.brand_km); //속도
        int speed[]=new int[km.length];
        int[] time=new int[speed.length]; //업체별 시간
        String[] stime=new String[speed.length]; //사람에게 보여질 시간
        String [] srate=res.getStringArray(R.array.custic_rain); //가성비
        int[] rate=new int[srate.length];
        int [] price=new int[speed.length]; //업체별 가격

        //거리, 속도를 바탕으로 시간과 가격 설정
        for(int i=0; i<time.length; i++) {
            rate[i]=Integer.parseInt(srate[i]);
            speed[i]=Integer.parseInt(km[i]);
            time[i] = (int) ((double)(distance / speed[i])*60);
            price[i]=(int)(rate[i]*distance*speed[i])/100-((int)(rate[i]*distance*speed[i])/100)%100;
            stime[i]= Integer.toString((int)(time[i]/60))+"시간 "+Integer.toString(time[i]%60)+"분";
        }


        LinearLayout suggest_list=(LinearLayout)findViewById(R.id.sugget_list);
        LayoutInflater inflater=getLayoutInflater();

        //추천순 정렬
        for(int i=0; i<rate.length; i++) {
            for(int j=0; j<rate.length; j++) {
                    if (rate[i] < rate[j]) {
                        change(name, i, j);
                        change(speed, i, j);
                        change(time, i, j);
                        change(stime, i, j);
                        change(rate,i, j);
                        change(srate, i, j);
                        change(km, i, j);
                        change(price, i, j);
                    }
            }
        }
        //추천 레이아웃에 추가
        View[] list1=new View[speed.length];
        for(int i=0; i<speed.length; i++) {
            list1[i] = inflater.inflate(R.layout.show_brand, null);
            TextView n = (TextView) list1[i].findViewById(R.id.name);
            TextView p = (TextView) list1[i].findViewById(R.id.price);
            TextView t = (TextView) list1[i].findViewById(R.id.time);
            n.setText(name[i]);
            p.setText(price[i] + "원");
            t.setText(stime[i]);
            list1[i].setOnClickListener(this);
            suggest_list.addView(list1[i]);
        }

        //시간순 정렬
        for(int i=0; i<rate.length; i++) {
            for(int j=0; j<rate.length; j++) {
                if (time[i] < time[j]) {
                    change(name, i, j);
                    change(speed, i, j);
                    change(time, i, j);
                    change(stime, i, j);
                    change(rate,i, j);
                    change(srate, i, j);
                    change(km, i, j);
                    change(price, i, j);
                }
            }
        }
        //시간 레이아웃에 추가
        LinearLayout short_way=(LinearLayout)findViewById(R.id.short_list);
        View[] list2=new View[speed.length];
        for(int i=0; i<speed.length; i++) {
            list2[i] = inflater.inflate(R.layout.show_brand, null);
            TextView n = (TextView) list2[i].findViewById(R.id.name);
            TextView p = (TextView) list2[i].findViewById(R.id.price);
            TextView t = (TextView) list2[i].findViewById(R.id.time);
            n.setText(name[i]);
            p.setText(price[i] + "원");
            t.setText(stime[i]);
            list2[i].setOnClickListener(this);
            short_way.addView(list2[i]);
        }

        //비용순 정렬
        for(int i=0; i<rate.length; i++) {
            for(int j=0; j<rate.length; j++) {
                if (price[i] < price[j]) {
                    change(name, i, j);
                    change(speed, i, j);
                    change(time, i, j);
                    change(stime, i, j);
                    change(rate,i, j);
                    change(srate, i, j);
                    change(km, i, j);
                    change(price, i, j);
                }
            }
        }
        //비용 레이아웃에 추가
        LinearLayout low_benefit=(LinearLayout)findViewById(R.id.low_list);
        View[] list3=new View[speed.length];
        for(int i=0; i<speed.length; i++) {
            list3[i] = inflater.inflate(R.layout.show_brand, null);
            TextView n = (TextView) list3[i].findViewById(R.id.name);
            TextView p = (TextView) list3[i].findViewById(R.id.price);
            TextView t = (TextView) list3[i].findViewById(R.id.time);
            n.setText(name[i]);
            p.setText(price[i] + "원");
            t.setText(stime[i]);
            list3[i].setOnClickListener(this);
            low_benefit.addView(list3[i]);
        }



    }

    private void change(int[] array, int i1, int i2) { //정수형 변경
        int tmp=array[i1];
        array[i1]=array[i2];
        array[i2]=tmp;
    }

    private void change(String[] array, int i1, int i2) {
        String tmp=array[i1];
        array[i1]=array[i2];
        array[i2]=tmp;
    }

    @Override
    public void onClick(View v) { //선택한 업체 저장
        TextView name=(TextView)v.findViewById(R.id.name);
        TextView price=(TextView)v.findViewById(R.id.price);
        TextView time=(TextView)v.findViewById(R.id.time);
        final_name=name.getText().toString();
        final_price=price.getText().toString();
        final_time=time.getText().toString();
        //인텐트를 통해 메인으로 전달
        Intent main=new Intent(Select_brand.this, Main.class);
        main.putExtra("final_name", final_name);
        main.putExtra("final_price", final_price);
        main.putExtra("final_time", final_time);
        finish();
    }
}
