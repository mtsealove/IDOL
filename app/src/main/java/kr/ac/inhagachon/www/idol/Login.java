package kr.ac.inhagachon.www.idol;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstancetate) {
        super.onCreate(savedInstancetate);
        setContentView(R.layout.activity_login);

        final TextView Signup=(TextView)findViewById(R.id.join);
        Signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //회원가입 페이지 이동
                Intent intent=new Intent(Login.this, Signup.class);
                startActivity(intent);
            }
        });

    }

    public void Show_login_Dialog(View v) { //로그인 프롬프트를 출력할 메서드
        AlertDialog.Builder builder=new AlertDialog.Builder(Login.this);
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.dialog_login, null);
        final EditText input_ID=(EditText)view.findViewById(R.id.input_ID);
        final EditText input_password=(EditText)view.findViewById(R.id.input_password);
        final CheckBox keep_login=(CheckBox)view.findViewById(R.id.keep_login);
        Button loginBtn=(Button)view.findViewById(R.id.login);
        builder.setView(view);


        loginBtn.setOnClickListener(new View.OnClickListener() { //로그인 버튼 클릭
            @Override
            public void onClick(View v) {
                String ID=input_ID.getText().toString();
                String password=input_password.getText().toString();
                boolean login_fail=true;
                if(ID.length()==0) Toast.makeText(getApplicationContext(), "ID를 입력하세요", Toast.LENGTH_SHORT).show();
                else if(password.length()==0) Toast.makeText(getApplicationContext(), "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
                else { //인스턴스 배열에서 ID검색
                    for(int i=0; i<Account.count; i++) {
                        if(ID.equals(Load.accounts[i].ID)&&password.equals(Load.accounts[i].password)) {//저장된 계정과 입력값이 일치하는 경우
                            login_fail=false;
                            Account.current_index=i; //로그인한 계정 저장
                            if(keep_login.isChecked()) { //로그인 유지 선택 시
                                try {
                                    //로그인 파일에 ID 저장
                                    BufferedWriter bw=new BufferedWriter(new FileWriter(getFilesDir()+Load.LoginFile));
                                    bw.write("true");
                                    bw.newLine();
                                    bw.write(ID);
                                    bw.flush();
                                    bw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {//로그인 유지가 체크 해제되면 설정 삭제
                                try { //공백파일로 만들어버림
                                    File logined=new File(getFilesDir()+Load.LoginFile);
                                    BufferedWriter bw=new BufferedWriter(new FileWriter(logined, false));
                                    bw.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Intent main_page=new Intent(Login.this, Main.class);
                            startActivity(main_page);
                            finish();
                            //메인 페이지로 이동
                        }
                    }
                    //로그인 실패 메세지 출력
                    if(login_fail) Toast.makeText(getApplicationContext(), "ID 또는 비밀번호를 확인하세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog=builder.create();
        dialog.show();
    }

    public void non_member_login(View v) { //비회원 이용으로 메인 페이지 이동
        Account.current_index=100;
        Intent main_page=new Intent(Login.this, Main.class);
        startActivity(main_page);
        finish();
    }


    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() { //뒤로가기 2번 눌러 종료
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {

            System.exit(0);
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한번 더 누르면 종료합니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
