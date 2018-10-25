package kr.ac.inhagachon.www.idol;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Show_bill extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bill);

        //소스에서 글자를 따옴
        Resources res=getResources();
        String[] sizes=res.getStringArray(R.array.size);
        String[] weights=res.getStringArray(R.array.weight);
        String[] bill_longs=res.getStringArray(R.array.bill_long);
        String[] bill_shorts=res.getStringArray(R.array.bill_short);

        LinearLayout detail_bill= findViewById(R.id.detail_bill);
        LayoutInflater inflater=getLayoutInflater();
        View[] sub_layout=new View[sizes.length];
        for(int i=0; i<sizes.length; i++) {
            sub_layout[i]=inflater.inflate(R.layout.sub_bill, null);
            TextView size= sub_layout[i].findViewById(R.id.size);
            TextView weight= sub_layout[i].findViewById(R.id.weight);
            TextView shortt= sub_layout[i].findViewById(R.id.shortt);
            TextView longt= sub_layout[i].findViewById(R.id.longt);

            size.setText(sizes[i]);
            weight.setText(weights[i]);
            shortt.setText(bill_shorts[i]);
            longt.setText(bill_longs[i]);
            detail_bill.addView(sub_layout[i]);
        }

    }
}
