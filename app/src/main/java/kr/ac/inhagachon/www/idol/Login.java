package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Login extends AppCompatActivity {
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_login);

        Button login= findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start_login();
            }
        });

        Button register= findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup=new Intent(Login.this, Signup.class);
                startActivity(signup);
                finish();
            }
        });
    }

    public void Start_login() { //로그인 프롬프트를 출력할 메서드
        final EditText input_ID = findViewById(R.id.input_ID);
        final EditText input_password = findViewById(R.id.input_password);
        final CheckBox keep_login = findViewById(R.id.keep_login);


        final String ID = input_ID.getText().toString();
        final String password = input_password.getText().toString();

        if (ID.length() == 0)
            Toast.makeText(getApplicationContext(), "ID를 입력하세요", Toast.LENGTH_SHORT).show();
        else if (password.length() == 0)
            Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else { //인스턴스 배열에서 ID검색
            final DatabaseReference ref1=database.getReference();
            ref1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try { //데이터베이스에서 해당 계정이 존재하는지 검색
                        if (dataSnapshot.child("Account").child(ID).child("password").getValue(String.class).equals(password)) {
                            String name = dataSnapshot.child("Account").child(ID).child("name").getValue(String.class);
                            String birth = dataSnapshot.child("Account").child(ID).child("birth").getValue(String.class);
                            String phone_number = dataSnapshot.child("Account").child(ID).child("phone_number").getValue(String.class);
                            Load.account = new Account(name, birth, phone_number, ID, password);
                            if (Load.account.ID.equals("manager")) {
                                Intent intent = new Intent(Login.this, Manager.class);
                                startActivity(intent);
                                finish();
                            } else {
                                if (keep_login.isChecked()) { //로그인 유지 선택 시
                                try {
                                    //로그인 파일에 ID 저장
                                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + Load.LoginFile));
                                    bw.write(ID);
                                    bw.newLine();
                                    bw.write(password);
                                    bw.flush();
                                    bw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else {//로그인 유지가 체크 해제되면 설정 삭제
                                try { //공백파일로 만들어버림
                                    File logined = new File(getFilesDir() + Load.LoginFile);
                                    BufferedWriter bw = new BufferedWriter(new FileWriter(logined, false));
                                    bw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Intent main_page = new Intent(Login.this, Main.class);
                            startActivity(main_page);
                            finish();
                            //메인 페이지로 이동
                        }
                        }
                    } catch (NullPointerException e) {
                        Toast.makeText(getApplicationContext(), "ID 또는 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Login.this, Main.class);
        startActivity(intent);
        finish();
    }
}
