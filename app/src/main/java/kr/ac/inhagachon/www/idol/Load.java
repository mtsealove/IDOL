package kr.ac.inhagachon.www.idol;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
    static Logic[] logics=new Logic[200];

    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=0;
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION=0;

    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    private boolean newtwork = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);

        //요금 db연동
        String location;
        int priority;
        int id;
        int next;
        String address;
        String transportation;
        double distance;
        int min;
        int cost;
        double speed;
        double rate;
        try {
            BufferedReader br=new BufferedReader(new InputStreamReader(getAssets().open("way.dat")));
            String tmp="";
            int i=0;
            while((tmp=br.readLine())!=null) {
                location=tmp;
                tmp=br.readLine();
                priority=Integer.parseInt(tmp);
                tmp=br.readLine();
                id=Integer.parseInt(tmp);
                tmp=br.readLine();
                next=Integer.parseInt(tmp);
                tmp=br.readLine();
                address=tmp;
                tmp=br.readLine();
                transportation=tmp;
                tmp=br.readLine();
                distance=Double.parseDouble(tmp);
                tmp=br.readLine();
                min=Integer.parseInt(tmp);
                tmp=br.readLine();
                cost=Integer.parseInt(tmp);
                tmp=br.readLine();
                speed=Double.parseDouble(tmp);
                tmp=br.readLine();
                rate=Double.parseDouble(tmp);
                logics[i++]=new Logic(location, priority, id, next, address, transportation, distance,min, cost, speed, rate);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("waydb", "db입력 실패");
        }

        //계졍 파일 접근 및 인스턴스 생성

        File Account_File=new File(getFilesDir()+Account_File_name);

        try {
            BufferedReader br=new BufferedReader(new FileReader(Account_File));
            String ID="", name="", passwd="", birth="", phone_number="";
            String tmp="";
            int i=0;
            while((tmp=br.readLine())!=null) { //파일을 읽어가며 인스턴스화, 내용이 없으면 중지
                name=tmp;
                tmp=br.readLine();
                birth=tmp;
                tmp=br.readLine();
                phone_number=tmp;
                tmp=br.readLine();
                ID=tmp;
                tmp=br.readLine();
                passwd=tmp;
                accounts[i]=new Account(name, birth,Integer.parseInt(phone_number), ID, passwd);
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
        //비회원 인스턴스 생성
        accounts[non_member_index]=new Account("비회원", "000000", 0,"non_member", "pw");

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
            }
            else { //로그인 유지가 되어있지 않을 경우 로그인 화면으로 이동
                Account.current_index=100;
            }
            br.close();

        } catch (FileNotFoundException e) {
            try { //파일이 없을 경우 생성
                logined.createNewFile();
                Account.current_index=100;
            } catch (IOException e1) {
                Account.current_index=100;
                e1.printStackTrace();
            }
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //인터넷 연결 여부 확인
        String getNetwork =  getWhatKindOfNetwork(getApplication());
        if(getNetwork.equals("NONE")){
            newtwork = false;
        }
        //인터넷에 연결되어 있지 않으면 0.5초 후 종료
        if(!newtwork) {
            Toast.makeText(getApplicationContext(), "인터넷 연결상태를 확인해 주세요\n프로그램을 종료합니다", Toast.LENGTH_SHORT).show();
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000);
        }

        //권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            //0.5초 뒤에 메인으로 이동
            Handler handler=new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    move_main();
                }
            }, 500);
        } else{
            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }

    }

    public void move_main() {
        Intent main_page=new Intent(Load.this, Main.class);
        startActivity(main_page);
        finish();
    }

    public static String getWhatKindOfNetwork(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                return WIFE_STATE;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return MOBILE_STATE;
            }
        }
        return NONE_STATE;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        //위 예시에서 requestPermission 메서드를 썼을시 , 마지막 매개변수에 0을 넣어 줬으므로, 매칭
        if(requestCode == 0){
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            if(grantResult[0] == 0){
                move_main();
                //해당 권한이 승낙된 경우.
            }else{
                //해당 권한이 거절된 경우.
            }
        }
    }
}
