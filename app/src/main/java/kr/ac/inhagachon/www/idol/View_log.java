package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//로그 목록 조회 페이지
public class View_log extends AppCompatActivity {
    static int count=0;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_view_log);
        Button back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //액티비티가 시작될 때 파일에서 로그 읽기
        if(Account.current_index!=100) {
            //accounts[Account.current_index].logs
            File log=new File(getFilesDir()+Load.accounts[Account.current_index].ID+".dat");
            String send_name;
            String send_address;
            String send_phone;
            String receive_name;
            String receive_address;
            String receive_phone;
            int size;
            int weight;
            String path;
            String purchase_method;
            String message;
            String time;
            int total_cost;
            try {
                String tmp="";
                BufferedReader br=new BufferedReader(new FileReader(log));
                int i=0;
                while((tmp=br.readLine())!=null) {
                    send_name=tmp;
                    tmp=br.readLine();
                    send_address=tmp;
                    tmp=br.readLine();
                    send_phone=tmp;
                    tmp=br.readLine();
                    receive_name=tmp;
                    tmp=br.readLine();
                    receive_address=tmp;
                    tmp=br.readLine();
                    receive_phone=tmp;
                    tmp=br.readLine();
                    size=Integer.parseInt(tmp);
                    tmp=br.readLine();
                    weight=Integer.parseInt(tmp);
                    tmp=br.readLine();
                    path=tmp;
                    tmp=br.readLine();
                    purchase_method=tmp;
                    tmp=br.readLine();
                    message=tmp;
                    tmp=br.readLine();
                    time=tmp;
                    tmp=br.readLine();
                    total_cost=Integer.parseInt(tmp);
                    Load.accounts[Account.current_index].logs[i++]=new LOG(send_name, send_address, send_phone, receive_name, receive_address, receive_phone, size, weight, path, purchase_method, message, time, total_cost);
                }
                count=i;

            } catch (FileNotFoundException e) {
                Log.d("file", "로그 없음");
                ScrollView scrollView= findViewById(R.id.list_scroll);
                RelativeLayout layout= findViewById(R.id.nothing);
                scrollView.setVisibility(View.GONE);
                layout.setVisibility(View.VISIBLE);
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String[] list=new String[count];
        //리스트에 이름을 추가
        for(int i=0; i<count; i++) {
            list[i]=(Load.accounts[Account.current_index].logs[i].time);
        }
        ListView listView= findViewById(R.id.list);
        ArrayAdapter<String> nameadapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.layout, list);
        listView.setAdapter(nameadapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent inquire=new Intent(View_log.this, Inquire_log.class);
                inquire.putExtra("log_index", position);
                startActivity(inquire);
            }
        });
    }
}
