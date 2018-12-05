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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

//파일 로딩을 위해 로딩 시간동안 표시될 페이지
public class Load extends AppCompatActivity {
    final static int max_account=101;
    static Account account;
    static int non_member_index=100;
    static String Account_File_name="Accounts.dat";
    static String LoginFile="logined.dat";

    static double longitude; //경도
    static double latitude;   //위도
    static double altitude;   //고도
    static float accuracy;    //정확도
    static String provider;   //위치제공자
    static String current_location; //현재 위치
    static Logic[] logics;

    int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION=0;
    int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION=0;

    public static final String WIFE_STATE = "WIFE";
    public static final String MOBILE_STATE = "MOBILE";
    public static final String NONE_STATE = "NONE";
    private boolean newtwork = true;

    ProgressBar pb;

    FirebaseDatabase database=FirebaseDatabase.getInstance();

    String id=null, password=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        pb= findViewById(R.id.progressBar);
        pb.setMax(100);

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
        pb.setProgress(10);

        //권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            read_database();
        } else{
            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},0);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            read_database();
        } else{
            //사용자에게 접근권한 설정을 요구하는 다이얼로그를 띄운다.
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        pb.setProgress(20);


    }

    private void move_main() {
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

    private void read_database() { //firebase의 데이터 접근 메서드

        //로그인 유지 저장사항에 대해 판단
        File logined=new File(getFilesDir()+LoginFile); //로그인 체크를 위한 파일
        try {
            BufferedReader br=new BufferedReader(new FileReader(logined));
            String tmp="";
            while((tmp=br.readLine())!=null) {
                id=tmp;
                tmp=br.readLine();
                password=tmp;
            }
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pb.setProgress(40);

        //로그인 파일 읽기에 성공시 firebase에서 데이터를 찾음
        if(id!=null&&password!=null) {
            DatabaseReference ref3=database.getReference();
            ref3.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("Account").child(id).child("password").getValue(String.class).equals(password)) {
                        String name = dataSnapshot.child("Account").child(id).child("name").getValue(String.class);
                        String birth = dataSnapshot.child("Account").child(id).child("birth").getValue(String.class);
                        String phone_number=dataSnapshot.child("Account").child(id).child("phone_number").getValue(String.class);
                        //객체 생성
                        account=new Account(name, birth, phone_number, id, password);
                        pb.setProgress(60);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else pb.setProgress(60);

        //firebase에 저장된 만큼의 경로 인스턴스 생성
        DatabaseReference ref1=database.getReference();
        ref1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int Links_size=(int)(dataSnapshot.child("Links").getChildrenCount());
                logics=new Logic[Links_size];
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        pb.setProgress(70);
        //데이터베이스에서 경로 및 요금 읽어오기
        DatabaseReference ref2=database.getReference();
        ref2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int index=0;
                for(DataSnapshot snapshot: dataSnapshot.child("Links").getChildren()) {
                    String location=snapshot.child("location").getValue(String.class);
                    int priority=snapshot.child("priority").getValue(Integer.class);
                    int id=snapshot.child("id").getValue(Integer.class);
                    int next=snapshot.child("next").getValue(Integer.class);
                    String address=snapshot.child("address").getValue(String.class);
                    String transportation=snapshot.child("transportation").getValue(String.class);
                    double distance=snapshot.child("distance").getValue(Double.class);
                    int min=snapshot.child("min").getValue(Integer.class);
                    int cost=snapshot.child("cost").getValue(Integer.class);
                    double speed=snapshot.child("speed").getValue(Double.class);
                    double rate=snapshot.child("rate").getValue(Double.class);
                    logics[index++]=new Logic(location, priority, id, next, address, transportation, distance, min, cost, speed, rate);
                }
                pb.setProgress(90);
                move_main();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResult){
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        //위 예시에서 requestPermission 메서드를 썼을시 , 마지막 매개변수에 0을 넣어 줬으므로, 매칭
        if(requestCode == 0){
            // requestPermission의 두번째 매개변수는 배열이므로 아이템이 여러개 있을 수 있기 때문에 결과를 배열로 받는다.
            // 해당 예시는 요청 퍼미션이 한개 이므로 i=0 만 호출한다.
            if(grantResult[0] == 0){
                read_database();
                //해당 권한이 승낙된 경우.
            }else{
                //해당 권한이 거절된 경우.
                Toast.makeText(getApplicationContext(), "애플리케이션을 사용하려면 위치 권한이 있어야 합니다\n설정에서 위치 권한을 설정해 주십시오", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
