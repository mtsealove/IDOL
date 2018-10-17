package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;
//로그 목록 조회 페이지
public class View_log extends AppCompatActivity {
    static int count=0;
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_view_log);

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
