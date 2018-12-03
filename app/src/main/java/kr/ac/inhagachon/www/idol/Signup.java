package kr.ac.inhagachon.www.idol;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class Signup extends AppCompatActivity {
    Boolean did_overlap_check=false; //중복검사를 실시했는지 확인할 변수
    FirebaseDatabase database=FirebaseDatabase.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        EditText phone=(EditText)findViewById(R.id.input_phone_number);
        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());

    }

    boolean overlap;
    public void check_ID_overlap(View v) { //ID 중복 검사 메서드
        final EditText input_ID=(EditText)findViewById(R.id.input_ID); //아이디 입력란
        final String ID=input_ID.getText().toString();
        overlap=false;
        DatabaseReference ref=database.getReference();


        //ID 미입력시 토스트 출력
        if(ID.length()==0) Toast.makeText(Signup.this, "ID를 입력하세요", Toast.LENGTH_SHORT).show();
        //ID는 8자 이상 16자 미만으로 제한
        else if(ID.length()<8||ID.length()>16) Toast.makeText(Signup.this, "ID는 8자 이상 16자 이하여야 됩니다", Toast.LENGTH_SHORT).show();
        else {
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        if(dataSnapshot.child("Account").child(ID).child("id").getValue()!=null) overlap=true;
                    }catch (NullPointerException e) {
                    }
                    if (!overlap) { //해당 ID를 사용 가능할 시
                        AlertDialog.Builder builder = new AlertDialog.Builder(Signup.this);
                        builder.setTitle("ID 사용가능")
                                .setMessage("해당 ID를 사용할 수 있습니다")
                                .setCancelable(false)
                                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) { //입력 비활성화
                                        input_ID.setEnabled(false);
                                        input_ID.setFocusable(false);
                                        input_ID.setClickable(false);
                                        input_ID.setTextColor(Color.GRAY);
                                        did_overlap_check = true; //중복검사 실시 확인
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog=builder.create();
                        dialog.show();
                    }
                    else Toast.makeText(Signup.this, "동일한 ID가 존재합니다", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    public void Sign_up(View v) { //회원가입 버튼을 눌렀을 떄
        //EditText를 통해 입력받을 정보들
        EditText input_ID=(EditText)findViewById(R.id.input_ID);
        EditText input_name=(EditText)findViewById(R.id.input_name);
        EditText input_password=(EditText)findViewById(R.id.input_passwd);
        EditText input_check_password=(EditText)findViewById(R.id.check_passwd);
        EditText input_birth=(EditText)findViewById(R.id.input_birth);
        EditText input_phone=(EditText)findViewById(R.id.input_phone_number);
        //문자열로 변환
        final String ID=input_ID.getText().toString();
        final String name=input_name.getText().toString();
        final String password=input_password.getText().toString();
        String check_password=input_check_password.getText().toString();
        final String birth=input_birth.getText().toString();
        final String phone_number=input_phone.getText().toString();

        //입력 조건 확인
        if(!did_overlap_check) Toast.makeText(Signup.this, "ID 중복확인 버튼을 눌러주세요", Toast.LENGTH_SHORT).show();
        else if(name.length()==0) Toast.makeText(Signup.this, "이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(birth.length()==0) Toast.makeText(Signup.this,"생년월일을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(name.length()<2||name.length()>20) Toast.makeText(Signup.this, "이름은 2자에서 20자 이내여야 합니다", Toast.LENGTH_SHORT).show();
        else if(phone_number.length()==0) Toast.makeText(this, "전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(phone_number.length()<10||phone_number.length()>20) Toast.makeText(this, "전화번호는 10자 이상 20자 이내여야 합니다", Toast.LENGTH_SHORT).show();
        else if(password.length()==0) Toast.makeText(Signup.this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(password.length()<8||password.length()>20) Toast.makeText(Signup.this, "비밀번호는 8자 이상 20자 미만이여야 합니다", Toast.LENGTH_SHORT).show();
        else if(!password.equals(check_password)) Toast.makeText(Signup.this,"비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
        else { //모든 조건이 충족되었을 경우
            AlertDialog.Builder builder=new AlertDialog.Builder(Signup.this);
            builder.setTitle("가입 확인")
                    .setCancelable(false)
                    .setMessage("이름: "+name+"\n생년월일: "+birth+"\n전화번호: "+phone_number+"\nID: "+ID+"\n가입하시겠습니까?")
                    .setPositiveButton("확인",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseReference ref=database.getReference();
                            Account ac=new Account(name, birth, phone_number, ID, password);
                            ref.child("Account").child(ID).child("id").setValue(ac.ID);
                            ref.child("Account").child(ID).child("birth").setValue(ac.birth);
                            ref.child("Account").child(ID).child("name").setValue(ac.name);
                            ref.child("Account").child(ID).child("password").setValue(ac.password);
                            ref.child("Account").child(ID).child("phone_number").setValue(ac.phone_number);


                            dialog.cancel();
                            Intent login=new Intent(Signup.this, Login.class);
                            startActivity(login);
                            finish();
                        }
                    })
                    .setNegativeButton("취소",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Toast.makeText(Signup.this, "취소합니다", Toast.LENGTH_SHORT).show();
                        }
                    });
            AlertDialog dialog=builder.create();
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(Signup.this, Login.class);
        startActivity(intent);
        finish();
    }

}
