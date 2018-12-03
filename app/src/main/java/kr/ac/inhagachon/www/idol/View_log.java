package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

//로그 목록 조회 페이지
public class View_log extends AppCompatActivity {
    static int count=0;
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    DatabaseReference ref=database.getReference();
    static LOG[] logs;
    @Override
    protected void onCreate(final Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_view_log);
        Button back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size=(int)(dataSnapshot.child("Account").child(Load.account.ID).child("LOG").getChildrenCount());
                logs=new LOG[size];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //액티비티가 시작될 때 파일에서 로그 읽기
        if(Load.account!=null) {
            DatabaseReference ref1=database.getReference();
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i=0;
                    for(DataSnapshot snapshot:dataSnapshot.child("Account").child(Load.account.ID).child("LOG").getChildren()) {
                        int cost=snapshot.child("cost").getValue(Integer.class);
                        String message=snapshot.child("message").getValue(String.class);
                        String path=snapshot.child("path").getValue(String.class);
                        String purchase_method=snapshot.child("purchase_method").getValue(String.class);
                        String receive_address=snapshot.child("receive_address").getValue(String.class);
                        String receive_name=snapshot.child("receive_name").getValue(String.class);
                        String receive_phone=snapshot.child("receive_phone").getValue(String.class);
                        String send_address=snapshot.child("send_address").getValue(String.class);
                        String send_name=snapshot.child("send_name").getValue(String.class);
                        String send_phone=snapshot.child("send_phone").getValue(String.class);
                        int size=snapshot.child("size").getValue(Integer.class);
                        String time=snapshot.child("time").getValue(String.class);
                        int weight=snapshot.child("weight").getValue(Integer.class);
                        logs[i++]=new LOG(send_name, send_address, send_phone, receive_name, receive_address, receive_phone, size, weight, path, purchase_method, message, time, cost);
                    }
                    count=i;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        String[] list=new String[count];
        //리스트에 이름을 추가
        for(int i=0; i<count; i++) {
            list[i]=(logs[i].time);
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
