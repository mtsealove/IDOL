package kr.ac.inhagachon.www.idol;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class Faq extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_board);

        TextView name=(TextView)findViewById(R.id.name);
        name.setText("FAQ");

        Resources res=getResources();
        String[] lists=res.getStringArray(R.array.faq);
        ListView listView=(ListView)findViewById(R.id.list);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.layout, lists);
        listView.setAdapter(adapter);
    }
}
