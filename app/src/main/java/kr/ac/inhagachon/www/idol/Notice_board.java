package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class Notice_board extends AppCompatActivity {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    String[] title;
    String[] content;
    ListView listView;
    @Override
    protected void onCreate(final Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_board_list);
        Button back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView name= findViewById(R.id.name);
        name.setText("공지사항");

        listView= findViewById(R.id.list);
        //개수 생성
        DatabaseReference ref1=database.getReference();
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int size= (int) dataSnapshot.child("Notice").getChildrenCount();
                title=new String[size];
                content=new String[size];

                int i=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Notice").getChildren()) {
                    title[i]=snapshot.child("title").getValue(String.class);
                    content[i++]=snapshot.child("content").getValue(String.class);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.layout, title);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent each_board=new Intent(Notice_board.this, Each_board.class);
                        each_board.putExtra("content", content[position]);
                        each_board.putExtra("title", title[position]);
                        each_board.putExtra("index", position);
                        each_board.putExtra("kind", "Notice");
                        startActivity(each_board);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
