package kr.ac.inhagachon.www.idol;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Each_board extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle si) {
        super.onCreate(si);
        setContentView(R.layout.activity_board);

        Button back= findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //댓글 달기
        final EditText comment= findViewById(R.id.comments);
        Button input_comment= findViewById(R.id.input_comment);
        final LinearLayout cmt_board= findViewById(R.id.commnet_board);
        input_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //입력한 내용 화면에 추가
                String cmt=comment.getText().toString();
                LayoutInflater inflater=getLayoutInflater();
                View sub=inflater.inflate(R.layout.sub_commnet, null);
                TextView sub_cmt= sub.findViewById(R.id.comment);
                TextView user= sub.findViewById(R.id.user);
                sub_cmt.setText(cmt);
                user.setText(Load.account.name);
                cmt_board.addView(sub);
                //텍스트 없애고 키보드 내리기
                comment.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
            }
        });
    }
}
