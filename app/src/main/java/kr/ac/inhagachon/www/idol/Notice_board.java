package kr.ac.inhagachon.www.idol;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class Notice_board extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle si) {
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

        Resources res=getResources();
        String[] lists=res.getStringArray(R.array.notice);
        ListView listView= findViewById(R.id.list);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.layout, lists);
        listView.setAdapter(adapter);

    }
}
