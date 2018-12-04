package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Each_board extends AppCompatActivity {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
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
        Intent intent=getIntent();
        String content=intent.getStringExtra("content");
        String title=intent.getStringExtra("title");
        final int index=intent.getIntExtra("index", 0);
        final String kind=intent.getStringExtra("kind");

        TextView contentTV= findViewById(R.id.content);
        TextView titleTV= findViewById(R.id.content_title);
        if(content!=null) contentTV.setText(content);
        if(title!=null) titleTV.setText(title);

        final LinearLayout cmt_board= findViewById(R.id.commnet_board);

        DatabaseReference ref1=database.getReference();
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.child(kind).child(Integer.toString(index)).child("reply").getChildren()) {
                    reply reply=new reply(snapshot.child("name").getValue(String.class), snapshot.child("content").getValue(String.class));
                    LayoutInflater inflater=getLayoutInflater();
                    View sub=inflater.inflate(R.layout.sub_commnet, null);
                    TextView sub_cmt= sub.findViewById(R.id.comment);
                    TextView user= sub.findViewById(R.id.user);
                    sub_cmt.setText(reply.content);
                    user.setText(reply.name);
                    cmt_board.addView(sub);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //댓글 달기
        final EditText comment= findViewById(R.id.comments);
        Button input_comment= findViewById(R.id.input_comment);

        input_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String cmt=comment.getText().toString();
                //입력한 내용 화면에 추가
                //데이터베이스에 추가
                DatabaseReference ref=database.getReference();
                ref.child(kind).child(Integer.toString(index)).child("reply").push().setValue(new reply(Load.account.name, cmt));
                //텍스트 없애고 키보드 내리기
                comment.setText("");
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(comment.getWindowToken(), 0);
            }
        });
    }
}
