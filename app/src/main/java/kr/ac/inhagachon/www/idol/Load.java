package kr.ac.inhagachon.www.idol;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

//파일 로딩을 위해 로딩 시간동안 표시될 페이지
public class Load extends AppCompatActivity {
    final static int max_account=101;
    static Account[] accounts=new Account[max_account]; //계정 인스턴스
    static int non_member_index=100;
    static String Account_File_name="Accounts.dat";
    static String LoginFile="logined.dat";

    static double longitude; //경도
    static double latitude;   //위도
    static double altitude;   //고도
    static float accuracy;    //정확도
    static String provider;   //위치제공자
    static String current_location; //현재 위치

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //계졍 파일 접근 및 인스턴스 생성

        File Account_File=new File(getFilesDir()+Account_File_name);

        try {
            BufferedReader br=new BufferedReader(new FileReader(Account_File));
            String ID="", name="", passwd="", birth="";
            String tmp="";
            int i=0;
            while((tmp=br.readLine())!=null) { //파일을 읽어가며 인스턴스화, 내용이 없으면 중지
                name=tmp;
                tmp=br.readLine();
                birth=tmp;
                tmp=br.readLine();
                ID=tmp;
                tmp=br.readLine();
                passwd=tmp;
                accounts[i]=new Account(name, birth, ID, passwd);
                i++;
                }
              Account.count=i;
                br.close();
        } catch (FileNotFoundException e) {
            try {
                Account_File.createNewFile();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        accounts[non_member_index]=new Account("비회원", "000000", "non_member", "pw");

        //로그인 유지 저장사항에 대해 판단

        File logined=new File(getFilesDir()+LoginFile); //로그인 체크를 위한 파일

        try{
            BufferedReader br=new BufferedReader(new FileReader(logined));
            boolean keep_login=Boolean.parseBoolean(br.readLine());
            if(keep_login) { //로그인 유지가 되어 있을 경우
                String current_ID=br.readLine();
                for(int i=0; i<Account.count; i++) {
                    if(accounts[i].ID.equals(current_ID)) Account.current_index=i;
                }
                //저장된 ID로 로그인
                Intent main_page=new Intent(Load.this, Main.class);
                startActivity(main_page);
                //메인 페이지로 이동
                finish();
            }
            else { //로그인 유지가 되어있지 않을 경우 로그인 화면으로 이동
                move_login();
            }
            br.close();

        } catch (FileNotFoundException e) {
            try { //파일이 없을 경우 생성
                logined.createNewFile();
                move_login();
            } catch (IOException e1) {
                move_login();
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();

    }

    void move_login() { //로그인 페이지 이동 메서드
        Intent login_page=new Intent(Load.this, Login.class);
        startActivity(login_page);
    }
}
