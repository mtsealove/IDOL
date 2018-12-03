package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Inquire_log extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_inquire_log);
        //표시할 로그 결정
        Intent intent=getIntent();
        int index=intent.getIntExtra("log_index", 0);
        LOG log=View_log.logs[index];

        Button back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //뷰 매칭
        TextView sname= findViewById(R.id.send_name);
        TextView sphone= findViewById(R.id.send_phone);
        TextView saddress= findViewById(R.id.send_address);
        TextView rname= findViewById(R.id.receive_name);
        TextView rphone= findViewById(R.id.receive_phone);
        TextView raddress= findViewById(R.id.receive_address);
        TextView size= findViewById(R.id.size);
        TextView weight= findViewById(R.id.weight);
        TextView path= findViewById(R.id.path);
        TextView cost= findViewById(R.id.cost);
        TextView pay= findViewById(R.id.payment);
        TextView time= findViewById(R.id.time);

        String[] paths=log.path.split(",");
        String path4display="";
        for(int i=0; i<paths.length; i++) {
            path4display+=paths[i];
            if(i!=paths.length-1) path4display+="\n";
        }


        //매칭에 맞게 설정
        sname.setText("이름: "+log.send_name);
        sphone.setText("전화번호: "+log.send_phone);
        saddress.setText("주소: "+log.send_address);
        rname.setText("이름: "+log.receive_name);
        rphone.setText("전화번호: "+log.receive_phone);
        raddress.setText("주소: "+log.receive_address);
        size.setText("크기: "+log.size+"cm");
        weight.setText("무게: "+log.weight+"kg");
        path.setText(path4display);
        cost.setText("결제 금액: "+log.cost);
        pay.setText("결제 수단: "+log.purchase_method);
        time.setText("구매시간: "+log.time);

    }
}
