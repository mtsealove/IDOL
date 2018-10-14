package kr.ac.inhagachon.www.idol;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Size;
import android.view.Gravity;
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
        List<String> All=new ArrayList<String>();
        All.add("크기(cm)");
        All.add("무게(kg)");
        All.add("단거리");
        All.add("장거리");
        for(int i=0; i<sizes.length; i++) {
            All.add(sizes[i]);
            All.add(weights[i]);
            All.add(bill_shorts[i]);
            All.add(bill_longs[i]);
        }

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(), R.layout.gridview_layout, All);

        //그리드 뷰 생성
        GridView gridView=(GridView)findViewById(R.id.gv);
        gridView.setAdapter(adapter);


    }
}
