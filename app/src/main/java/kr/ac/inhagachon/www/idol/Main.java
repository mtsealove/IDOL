package kr.ac.inhagachon.www.idol;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Main extends AppCompatActivity {
    //출발, 도착지의 위도, 경도
    static double slatitude;
    static double slongitude;
    static double dlatitude;
    static double dlongitude;
    static double distance;

    //정보
    static String message;
    static int size, weight;
    static boolean is_setSize=false;
    static int final_bill;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        //사용자 이름 설정
        TextView username=(TextView)findViewById(R.id.user_name);
        if(!Load.accounts[Account.current_index].name.equals("비회원"))
            username.setText("사용자: "+Load.accounts[Account.current_index].name);
        else { //비회원일 경우 로그인 화면 출력
            username.setText("로그인 하세요");
        }

        //drawerLayout 슬라이드 설정
        ImageView drawerBTN=(ImageView) findViewById(R.id.slide_menu);
        drawerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout=(DrawerLayout)findViewById(R.id.main_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //회원가입 페이지 이동 버튼
        ImageView registerBTN=(ImageView)findViewById(R.id.register);
        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //비회원일 경우 로그인 페이지로 이동
                if(Load.accounts[Account.current_index].name=="비회원") {
                    Intent login_page = new Intent(Main.this,Login.class);
                    startActivity(login_page);
                    finish();
                }
                else { //회원일 경우 회원 정보 및 로그아웃 버튼 생성
                    LayoutInflater inflater=getLayoutInflater();
                    View account_info=inflater.inflate(R.layout.dialog_account_info, null);
                    TextView username=(TextView)account_info.findViewById(R.id.user_name);
                    TextView ID=(TextView)account_info.findViewById(R.id.id);
                    TextView phone=(TextView)account_info.findViewById(R.id.telephone);
                    username.setText("이름: "+Load.accounts[Account.current_index].name);
                    ID.setText("ID: "+Load.accounts[Account.current_index].ID);
                    phone.setText("전화번호: 0"+Load.accounts[Account.current_index].phone_number);
                    AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
                    builder.setTitle("계정 정보")
                            .setCancelable(true)
                            .setView(account_info);
                    AlertDialog dialog=builder.create();
                    dialog.show();
                }
            }
        });

        TextView SLocation=(TextView)findViewById(R.id.start_location);
        SLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move_map();
            }
        });

        LinearLayout egg=(LinearLayout)findViewById(R.id.egg);
        egg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                egg();
                return true;
            }
        });

    }

    public void LogOut(View v) { //로그아웃 메서드
        try { //로그인 유지 해제
            File logind=new File(getFilesDir()+Load.LoginFile);
            BufferedWriter bw=new BufferedWriter(new FileWriter(logind, false));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Account.current_index=100;
        finish();
        Intent main_page=new Intent(Main.this, Main.class);
        startActivity(main_page);
        Toast.makeText(Main.this, "로그아웃 되었습니다", Toast.LENGTH_SHORT).show();
        //페이지 리로드

    }

    //지도 화면으로 이동
    public void move_map() {
        Intent map=new Intent(Main.this, SelectMap.class);
        map.putExtra("isStart", true);
        startActivityForResult(map, 900);
    }
    public void move_mapd(View v) {
        Intent map=new Intent(Main.this, SelectMap.class);
        map.putExtra("isStart", false);
        startActivityForResult(map, 1000);
    }

    //결과값에 따라 값을 변경할 메서드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String start_address, destination_address;
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case 900:
                    start_address=data.getStringExtra("address");
                    TextView startloctcation=(TextView)findViewById(R.id.start_location);
                    startloctcation.setText(start_address);
                    break;
                case 1000:
                    destination_address=data.getStringExtra("address2");
                    TextView destination=(TextView)findViewById(R.id.destination_location);
                    destination.setText(destination_address);
                    break;
            }
        }
    }

    public void set_round_trip(View v) { //편도/왕복 설정 메서드
        final TextView label=(TextView)findViewById(R.id.setreturn);
        String[] list={"편도", "왕복"};
        ListView listView=new ListView(this);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(Main.this, R.layout.layout, list);
        listView.setAdapter(adapter);

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("편도/왕복 설정")
                .setView(listView)
        .setCancelable(false);
        final AlertDialog dialog=builder.create();
        dialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                label.setText(parent.getItemAtPosition(position).toString()); //화면상의 주소지를 설정
                dialog.cancel();
            }
        });
    }

    public void set_transportation(View v) { //운송 방법 선택 메서드
        String sloc = ((TextView) findViewById(R.id.start_location)).getText().toString();
        String dloc = ((TextView) findViewById(R.id.destination_location)).getText().toString();
        if (sloc.equals("출발지 검색") || dloc.equals("도착지 검색")) {//출발/도착지를 선택하지 않으면 선택 불가
            Toast.makeText(getApplicationContext(), "출발지와 도착지를 선택하세요", Toast.LENGTH_SHORT).show();
            LinearLayout layout=(LinearLayout)findViewById(R.id.set_st_ed);
            layout.setFocusableInTouchMode(true);
            layout.requestFocus();
            } //화물 종류를 선택하지 않으면 실행 불가
        else if(!is_setSize) Toast.makeText(getApplicationContext(), "화물 종류를 설정하세요", Toast.LENGTH_SHORT).show();
        else { //모두 선택시 방법 표시
            //3개의 선택사항 출력
            LayoutInflater inflater=getLayoutInflater();
            View layout=inflater.inflate(R.layout.dialog_set_transportation_method, null);
            AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
            builder.setView(layout);
            AlertDialog dialog=builder.create();
            Button low_benefit=(Button)layout.findViewById(R.id.low_benefit);
            Button short_way=(Button)layout.findViewById(R.id.short_way);
            Button self_select=(Button)layout.findViewById(R.id.select_my_self);
            low_benefit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView s=(TextView)findViewById(R.id.start_location);
                    TextView d=(TextView)findViewById(R.id.destination_location);
                    Intent move_low_benefit=new Intent(Main.this, LowBenefit.class);
                    move_low_benefit.putExtra("saddress", s.getText().toString());
                    move_low_benefit.putExtra("daddress", d.getText().toString());
                    startActivity(move_low_benefit);
                }
            });
            self_select.setOnClickListener(new View.OnClickListener() { //직접 선택 메서드
                @Override
                public void onClick(View v) {
                    self_set_trasportation();
                }
            });
            dialog.show();
        }
    }

    //위도 경로를 이용해 거리를 구하고 100km가 넘으면 true값 반환
    public boolean isLongDistance(double lat1, double lon1, double lat2, double lon2){
        double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;// 단위 mile 에서 km 변환
        distance=dist;
        if(dist>100) return true;
        else return false;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return (double)(deg * Math.PI / (double)180d);
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return (double)(rad * (double)180d / Math.PI);
    }

    public void self_set_trasportation() { //거리를 계산하여 각 거리에 맞는 운송수단 표시, 직접 선택
        if(isLongDistance(slatitude, slongitude, dlatitude, dlongitude)) {
            long_transportation();
        }
        else {
            short_transportation();
        }
    }

    protected void long_transportation() { //장거리 운송수단 출력
            //Alertdialog 생성
            AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
            LayoutInflater inflater=getLayoutInflater();
            View layout=inflater.inflate(R.layout.dialog_long_transportation, null);
            builder.setView(layout);
            final AlertDialog dialog=builder.create();
            dialog.show();

            final TextView transportation=(TextView)findViewById(R.id.transporation);
            //버튼 생성
            LinearLayout bus=(LinearLayout)layout.findViewById(R.id.bus);
            LinearLayout ship=(LinearLayout)layout.findViewById(R.id.ship);
            LinearLayout ktx=(LinearLayout)layout.findViewById(R.id.ktx);
            LinearLayout quick=(LinearLayout)layout.findViewById(R.id.quick);
            LinearLayout subway=(LinearLayout)layout.findViewById(R.id.subway);

            bus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportation.setText("차량종류: 고속/시외버스");
                    dialog.cancel();
                }
            });
            ship.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportation.setText("차량종류: 선박");
                    dialog.cancel();
                }
            });
            ktx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportation.setText("차량종류: KTX 특송");
                    dialog.cancel();
                }
            });
            quick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent select_brand=new Intent(Main.this, Select_brand.class);
                    startActivity(select_brand);
                    transportation.setText("차량종류: 퀵");
                    dialog.cancel();
                }
            });
            subway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportation.setText("차량종류: 지하철택배");
                    dialog.cancel();
                }
            });
    }

    protected void short_transportation() { //단거리 차량종류
            AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
            LayoutInflater inflater=getLayoutInflater();
            View layout=inflater.inflate(R.layout.dialog_short_transportation, null);
            builder.setView(layout);
            final AlertDialog dialog=builder.create();
            dialog.show();

            final TextView transportation=(TextView)findViewById(R.id.transporation);
            LinearLayout quick=(LinearLayout)layout.findViewById(R.id.quick);
            LinearLayout subway=(LinearLayout)layout.findViewById(R.id.subway);
            quick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent select_brand=new Intent(Main.this, Select_brand.class);
                    startActivity(select_brand);
                    transportation.setText("차량종류: 퀵");
                    show_price();//화면에 결제 금액 출력
                    dialog.cancel();
                }
            });
            subway.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    transportation.setText("차량종류: 지하철 택배");
                    dialog.cancel();
                }
            });
    }

    protected void show_price() { //결제 금액을 화면에 출력시키는 메소드
        //String-array에서 값 읽어오기
        Resources res=getResources();
        String[] Ssizes=res.getStringArray(R.array.size);
        String[] Sweights=res.getStringArray(R.array.weight);
        String[] Slong_bills=res.getStringArray(R.array.bill_long);
        String[] Sshort_bills=res.getStringArray(R.array.bill_short);
        String result="";

        //int값으로 변환
        int[] sizes=new int[Ssizes.length];
        int[] weights=new int[Sweights.length];
        for(int i=0; i<Ssizes.length; i++) {
            sizes[i]=Integer.parseInt(Ssizes[i]);
            weights[i]=Integer.parseInt(Sweights[i]);
        }
        int index=sizes.length-1;
        for(int i=0; i<sizes.length; i++) { //크기와 무게를 비교
            if(size<sizes[i]&&weight<weights[i]) {
                index=i;
                break;
            }
        }
        //장거리 단거리를 파악해 금액 설정
        if(isLongDistance(slatitude, slongitude, dlatitude, dlongitude)) {
            result=Slong_bills[index];
        }
        else {
            result=Sshort_bills[index];
        }
        final_bill=Integer.parseInt(result); //메모리에 값 저장
        //textview형태로 화면에 출력
        TextView billT=(TextView)findViewById(R.id.final_bill);
        billT.setText("결제 금액: "+result+"원");
        RelativeLayout layout=(RelativeLayout)findViewById(R.id.final_bill_layout);
        layout.setVisibility(View.VISIBLE);
    }

    public void set_purchase_method(View v){ //결제수단 설정 메소드
        //dialog 출력
        final LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_purchase_method, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
        builder.setView(layout);
        final AlertDialog dialog0=builder.create();
        dialog0.show();

        //버튼 생성
        LinearLayout card=(LinearLayout)layout.findViewById(R.id.mobile_card);
        LinearLayout depositless=(LinearLayout)layout.findViewById(R.id.depositless);
        LinearLayout virtual_account=(LinearLayout)layout.findViewById(R.id.virtual_account);
        final TextView purchase_method=(TextView)findViewById(R.id.purchase_method);

        //카드 결제
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
                LayoutInflater inflater1=getLayoutInflater();
                final View layout=inflater1.inflate(R.layout.sub_dialog_purchase_card, null);
                builder.setView(layout)
                        .setTitle("모바일 카드 결제");
                final AlertDialog dialog1=builder.create();
                dialog1.show();

                Button purchase=(Button)layout.findViewById(R.id.purchase);
                purchase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText card_num=(EditText)layout.findViewById(R.id.card_num);
                        if(card_num.getText().toString().length()!=16) Toast.makeText(getApplicationContext(), "카드번호를 제대로 입력하세요", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(getApplicationContext(), "결제수단이 등록되었습니다", Toast.LENGTH_SHORT).show();
                            purchase_method.setText("결제수단: 모바일 카드");
                            dialog1.cancel();
                            dialog0.cancel();
                        }
                    }
                });

            }
        });
        //무통장 입금
        depositless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Main.this);

                final EditText account = new EditText(getApplicationContext());
                account.setHint("계좌번호 입력");
                account.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder1.setTitle("무통장 입금")
                        .setMessage("무통장 입금 계좌번호를 입력하세요")
                        .setView(account)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (account.getText().toString().length() != 0) {
                                    Toast.makeText(getApplicationContext(), "결제수단이 등록되었습니다", Toast.LENGTH_SHORT).show();
                                    purchase_method.setText("결제수단: 무통장 입금");
                                    dialog.cancel();
                                    dialog0.cancel();
                                }
                                else Toast.makeText(getApplicationContext(), "계좌번호를 입력하세요", Toast.LENGTH_SHORT).show();

                            }
                        });
                AlertDialog dialog1=builder1.create();
                dialog1.show();
            }
        });

        //가상계좌
        virtual_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(Main.this);

                final EditText account = new EditText(getApplicationContext());
                account.setHint("가상계좌 번호 입력");
                account.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder1.setTitle("가상계좌")
                        .setMessage("가상계좌 입금 계좌번호를 입력하세요")
                        .setView(account)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (account.getText().toString().length() != 0) {
                                    Toast.makeText(getApplicationContext(), "결제수단이 등록되었습니다", Toast.LENGTH_SHORT).show();
                                    purchase_method.setText("결제수단: 가상계좌");
                                    dialog.cancel();
                                    dialog0.cancel();
                                }
                                else Toast.makeText(getApplicationContext(), "계좌번호를 입력하세요", Toast.LENGTH_SHORT).show();

                            }
                        });
                AlertDialog dialog1=builder1.create();
                dialog1.show();
            }
        });
    }

    public void notice_board(View v) { //공지사항
        Intent intent=new Intent(Main.this, Notice_board.class);
        startActivity(intent);
    }

    public void faq(View v) { //faq
        Intent intent=new Intent(Main.this, Faq.class);
        startActivity(intent);
    }

    public void set_message(View v) { //메세지 입력
        AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_input_message, null);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();

        Button cancel=(Button)layout.findViewById(R.id.cancel);
        Button input=(Button)layout.findViewById(R.id.input);
        final EditText msget=(EditText)layout.findViewById(R.id.message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message=msget.getText().toString();
                TextView fm=(TextView)findViewById(R.id.final_message);
                fm.setText(message);
                fm.setVisibility(View.VISIBLE);
                if(message.length()==0) fm.setText(View.GONE);
                dialog.cancel();
                Toast.makeText(getApplicationContext(), "메세지가 저장되었습니다", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    public void set_size(View v) { //크기및 무게 출력 dialog
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.dialog_set_size, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
        builder.setView(layout);
        final AlertDialog dialog=builder.create();
        dialog.show();

        final EditText etsize=(EditText)layout.findViewById(R.id.size);
        etsize.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "가로+세로+높이의 합을 cm단위로 입력해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        final EditText etweight=(EditText)layout.findViewById(R.id.weight);
        etweight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "무게를 KG 단위로 입력해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        Button confirm=(Button)layout.findViewById(R.id.confirm);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { //미입력 방지
                if(etsize.getText().toString().length()==0) Toast.makeText(Main.this, "크기를 입력하세요", Toast.LENGTH_SHORT).show();
                else if(Integer.parseInt(etsize.getText().toString())==0) Toast.makeText(Main.this, "크기는 0이 될 수 없습니다", Toast.LENGTH_SHORT).show();
                else if(etweight.getText().toString().length()==0) Toast.makeText(Main.this, "무게를 입력하세요", Toast.LENGTH_SHORT).show();
                else if(Integer.parseInt(etsize.getText().toString())==0) Toast.makeText(Main.this, "무게는 0이 될 수 없습니다", Toast.LENGTH_SHORT).show();
                else { //전역 변수에 크기와 무게 설정
                    size = Integer.parseInt(etsize.getText().toString());
                    weight = Integer.parseInt(etweight.getText().toString());
                    is_setSize=true;
                    TextView slocation=(TextView)findViewById(R.id.start_location);
                    TextView dlocation=(TextView)findViewById(R.id.destination_location);
                    if((!slocation.getText().toString().equals("출발지 검색"))&&!dlocation.getText().toString().equals("도착지 검색")) show_price();
                    dialog.cancel();
                }
            }
        });

    }

    public void show_bill_list(View v) {
        Intent intent=new Intent(Main.this, Show_bill.class);
        startActivity(intent);
    }

    public void request(View v) { //모든 입력사항을 입력했는지 확인후 결제
        TextView sl=(TextView)findViewById(R.id.start_location);
        TextView dl=(TextView)findViewById(R.id.destination_location);
        EditText name1=(EditText)findViewById(R.id.sendname);
        EditText phone1=(EditText)findViewById(R.id.sendphone);
        EditText name2=(EditText)findViewById(R.id.receivename);
        EditText phone2=(EditText)findViewById(R.id.recievephone);
        TextView setturn=(TextView)findViewById(R.id.setreturn);
        TextView transporation=(TextView)findViewById(R.id.transporation);
        TextView purchase=(TextView)findViewById(R.id.purchase_method);
        if(sl.getText().toString().equals("출발지 검색")) Toast.makeText(getApplicationContext(), "출발지를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(dl.getText().toString().equals("도착지 검색")) Toast.makeText(getApplicationContext(), "도착지를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(name1.getText().toString().length()==0) Toast.makeText(getApplicationContext(), "보낼 사람의 이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(phone1.getText().toString().length()==0) Toast.makeText(getApplicationContext(), "보낼 사람의 전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(name2.getText().toString().length()==0)Toast.makeText(getApplicationContext(), "받을 사람의 이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(phone2.getText().toString().length()==0) Toast.makeText(getApplicationContext(), "보낼 사람의 전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(!is_setSize) Toast.makeText(getApplicationContext(), "화물의 종류를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(setturn.getText().toString().equals("편도/왕복 설정")) Toast.makeText(getApplicationContext(), "편도/왕복을 선택하세요", Toast.LENGTH_SHORT).show();
        else if(transporation.getText().toString().equals("차량 종류")) Toast.makeText(getApplicationContext(), "차량 종류를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(purchase.getText().toString().equals("결제수단")) Toast.makeText(getApplicationContext(), "결제 수단을 선택하세요", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(), "신청되었습니다", Toast.LENGTH_SHORT).show();
    }


    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    @Override
    public void onBackPressed() { //뒤로가기 2번 눌러 종료
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            ActivityCompat.finishAffinity(this);
            System.runFinalization();
            System.exit(0);
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기 버튼을 한 번 더 누르면 종료합니다", Toast.LENGTH_SHORT).show();
        }
    }

    public void egg() {
        LayoutInflater inflater=getLayoutInflater();
        View layout=inflater.inflate(R.layout.info, null);
        AlertDialog.Builder builder=new AlertDialog.Builder(Main.this);
        builder.setView(layout);

        AlertDialog dialog=builder.create();
        dialog.show();
    }


}
