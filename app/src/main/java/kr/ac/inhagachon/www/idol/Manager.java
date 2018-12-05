package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Manager extends AppCompatActivity {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    ListView log_list;
    static ArrayList<LOG> logs=new ArrayList<LOG>();
    ArrayList<String> times=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_manager);
        log_list= findViewById(R.id.log_list);

        DatabaseReference ref=database.getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child("Account").getChildren()) {
                    for(DataSnapshot snapshot1: snapshot.child("LOG").getChildren()) {
                        int cost=snapshot1.child("cost").getValue(Integer.class);
                        String message=snapshot1.child("message").getValue(String.class);
                        String path=snapshot1.child("path").getValue(String.class);
                        String purchase_method=snapshot1.child("purchase_method").getValue(String.class);
                        String receive_address=snapshot1.child("receive_address").getValue(String.class);
                        String receive_name=snapshot1.child("receive_name").getValue(String.class);
                        String receive_phone=snapshot1.child("receive_phone").getValue(String.class);
                        String send_address=snapshot1.child("send_address").getValue(String.class);
                        String send_name=snapshot1.child("send_name").getValue(String.class);
                        String send_phone=snapshot1.child("send_phone").getValue(String.class);
                        int size=snapshot1.child("size").getValue(Integer.class);
                        String time=snapshot1.child("time").getValue(String.class);
                        int weight=snapshot1.child("weight").getValue(Integer.class);
                        logs.add(new LOG(send_name, send_address, send_phone, receive_name, receive_address, receive_phone, size, weight, path, purchase_method, message, time, cost));
                        times.add(time);
                    }
                }
                ArrayAdapter<String> adapter=new ArrayAdapter<String>(Manager.this, R.layout.layout, times);
                log_list.setAdapter(adapter);
                View_log.logs=new LOG[logs.size()];
                for(int i=0; i<View_log.logs.length; i++) {
                    View_log.logs[i]=logs.get(i);
                }
                log_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent=new Intent(Manager.this, Inquire_log.class);
                        intent.putExtra("log_index", position);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onBackPressed() {
        Load.account=null;
        super.onBackPressed();
    }
}
