package kr.ac.inhagachon.www.idol;

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
import android.util.Log;
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
import java.text.SimpleDateFormat;
import java.util.Date;


public class Main extends AppCompatActivity {
    //출발, 도착지의 위도, 경도
    static double slatitude;
    static double slongitude;
    static double dlatitude;
    static double dlongitude;
    static double distance;

    //정보
    static String message="";
    static int size, weight;
    static boolean is_setSize=false;
    static int final_bill;

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_main);
        //사용자 이름 설정
        TextView username= findViewById(R.id.user_name);
        if(!Load.accounts[Account.current_index].name.equals("비회원")) {
            username.setText("사용자: " + Load.accounts[Account.current_index].name);
            username.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(Main.this, View_log.class);
                    startActivity(intent);
                }
            });
        }
        else { //비회원일 경우 로그인 화면 출력
            username.setText("로그인 하세요");
        }

        //drawerLayout 슬라이드 설정
        ImageView drawerBTN= findViewById(R.id.slide_menu);
        drawerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawerLayout= findViewById(R.id.main_layout);
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //회원가입 페이지 이동 버튼
        ImageView registerBTN= findViewById(R.id.register);
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
                    TextView username= account_info.findViewById(R.id.user_name);
                    TextView ID= account_info.findViewById(R.id.id);
                    TextView phone= account_info.findViewById(R.id.telephone);
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

        TextView SLocation= findViewById(R.id.start_location);
        SLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                move_map();
            }
        });

        LinearLayout egg= findViewById(R.id.egg);
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
                    TextView startloctcation= findViewById(R.id.start_location);
                    startloctcation.setText(start_address);
                    break;
                case 1000:
                    destination_address=data.getStringExtra("address2");
                    TextView destination= findViewById(R.id.destination_location);
                    destination.setText(destination_address);
                    break;
            }
        }
    }

    public void set_round_trip(View v) { //편도/왕복 설정 메서드
        final TextView label= findViewById(R.id.setreturn);
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
        String sloc = ((TextView) findViewById(R.id.start_location)).getText().toString(); //출발주소
        String dloc = ((TextView) findViewById(R.id.destination_location)).getText().toString(); //도착주소
        String s1, s2, total1, total2;

        boolean exist_load=false;
        //주소에 포함된 지명 검색
        if(sloc.contains("서울")) s1="서울";
        else if (sloc.contains("대구")) s1="대구";
        else if(sloc.contains("광주")) s1="광주";
        else if(sloc.contains("대전")) s1="대전";
        else if(sloc.contains("강릉")) s1="강릉";
        else if(sloc.contains("천안")) s1="천안";
        else if(sloc.contains("광주")) s1="광주";
        else if(sloc.contains("부산")) s1="부산";
        else if(sloc.contains("경기")) s1="수원";
        else if(sloc.contains("인천")) s1="인천";
        else s1="";
        if(dloc.contains("서울")) s2="서울";
        else if (dloc.contains("대구")) s2="대구";
        else if(dloc.contains("광주")) s2="광주";
        else if(dloc.contains("대전")) s2="대전";
        else if(dloc.contains("강릉")) s2="강릉";
        else if(dloc.contains("천안")) s2="천안";
        else if(dloc.contains("광주")) s2="광주";
        else if(dloc.contains("부산")) s2="부산";
        else if(dloc.contains("경기")) s2="수원";
        else if(dloc.contains("인천")) s2="인천";
        else s2="";
        total1=s1+"-"+s2;
        total2=s2+"-"+s1;
        int logic_length=0;
        for(int i=0; i<Load.logics.length; i++) { //지원 가능한 경로인지 판단
            if(Load.logics[i].location.equals(total1)||Load.logics[i].location.equals(total2)) {
                exist_load=true;
                logic_length++;
                Log.d("path", "path:"+Load.logics[i].location);
            }
        }
        //출발-도착 위치를 생성

            //출발/도착지를 선택하지 않으면 선택 불가
        if (sloc.equals("출발지 검색") || dloc.equals("도착지 검색")) {
            Toast.makeText(getApplicationContext(), "출발지와 도착지를 선택하세요", Toast.LENGTH_SHORT).show();
            LinearLayout layout= findViewById(R.id.set_st_ed);
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
            final AlertDialog dialog=builder.create();
            Button suggets_way= layout.findViewById(R.id.suggest_way);
            Button low_benefit= layout.findViewById(R.id.low_benefit_btn);
            Button short_way= layout.findViewById(R.id.short_way);
            Button self_select= layout.findViewById(R.id.select_my_self);
            final String finalTotal1 = total1;
            final String finalTotal2 = total2;
            final int finalLogic_length = logic_length;
            final Intent show_way=new Intent(Main.this, Show_way.class);
            TextView s= findViewById(R.id.start_location);
            TextView d= findViewById(R.id.destination_location);
            show_way.putExtra("saddress", s.getText().toString());
            show_way.putExtra("daddress", d.getText().toString());
            show_way.putExtra("location1", finalTotal1);
            show_way.putExtra("location2", finalTotal2);
            show_way.putExtra("count", finalLogic_length);
            suggets_way.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_way.putExtra("title", "추천경로");
                    startActivity(show_way);
                    dialog.cancel();
                }
            });
            low_benefit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_way.putExtra("title", "최소비용");
                    startActivity(show_way);
                    dialog.cancel();
                }
            });
            short_way.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_way.putExtra("title", "최단시간");
                    startActivity(show_way);
                    dialog.cancel();
                }
            });
            self_select.setOnClickListener(new View.OnClickListener() { //직접 선택 메서드
                @Override
                public void onClick(View v) {
                    self_set_trasportation();
                    dialog.cancel();
                }
            });
            if(!exist_load) { //추천경로가 존재하지 않는다면 선택 불가
                View.OnClickListener notable=new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), "지원되지 않는 경로입니다", Toast.LENGTH_SHORT).show();
                    }
                };
                low_benefit.setOnClickListener(notable);
                short_way.setOnClickListener(notable);
                suggets_way.setOnClickListener(notable);
            }
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
        return dist > 100;
    }

    // 주어진 도(degree) 값을 라디언으로 변환
    private double deg2rad(double deg){
        return deg * Math.PI / 180d;
    }

    // 주어진 라디언(radian) 값을 도(degree) 값으로 변환
    private double rad2deg(double rad){
        return rad * 180d / Math.PI;
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

            final TextView transportation= findViewById(R.id.transporation);
            //버튼 생성
            LinearLayout bus= layout.findViewById(R.id.bus);
            LinearLayout ship= layout.findViewById(R.id.ship);
            LinearLayout ktx= layout.findViewById(R.id.ktx);
            LinearLayout quick= layout.findViewById(R.id.quick);
            LinearLayout subway= layout.findViewById(R.id.subway);

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

            final TextView transportation= findViewById(R.id.transporation);
            LinearLayout quick= layout.findViewById(R.id.quick);
            LinearLayout subway= layout.findViewById(R.id.subway);
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
                    transportation.setText("차량종류: 지하철 택배");
                    dialog.cancel();
                }
            });
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
        LinearLayout card= layout.findViewById(R.id.mobile_card);
        LinearLayout depositless= layout.findViewById(R.id.depositless);
        LinearLayout virtual_account= layout.findViewById(R.id.virtual_account);
        final TextView purchase_method= findViewById(R.id.purchase_method);

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

                Button purchase= layout.findViewById(R.id.purchase);
                purchase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText card_num= layout.findViewById(R.id.card_num);
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

        Button cancel= layout.findViewById(R.id.cancel);
        Button input= layout.findViewById(R.id.input);
        final EditText msget= layout.findViewById(R.id.message);

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
                TextView fm= findViewById(R.id.final_message);
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

        final EditText etsize= layout.findViewById(R.id.size);
        etsize.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "가로+세로+높이의 합을 cm단위로 입력해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        final EditText etweight= layout.findViewById(R.id.weight);
        etweight.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "무게를 KG 단위로 입력해주세요", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        Button confirm= layout.findViewById(R.id.confirm);

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
                    TextView slocation= findViewById(R.id.start_location);
                    TextView dlocation= findViewById(R.id.destination_location);
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
        TextView sl= findViewById(R.id.start_location);
        TextView dl= findViewById(R.id.destination_location);
        EditText name1= findViewById(R.id.sendname);
        EditText phone1= findViewById(R.id.sendphone);
        EditText name2= findViewById(R.id.receivename);
        EditText phone2= findViewById(R.id.recievephone);
        TextView setturn= findViewById(R.id.setreturn);
        TextView transporation= findViewById(R.id.transporation);
        TextView purchase= findViewById(R.id.purchase_method);
        if(sl.getText().toString().equals("출발지 검색")) Toast.makeText(getApplicationContext(), "출발지를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(dl.getText().toString().equals("도착지 검색")) Toast.makeText(getApplicationContext(), "도착지를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(name1.getText().toString().length()==0) Toast.makeText(getApplicationContext(), "보낼 사람의 이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(phone1.getText().toString().length()==0) Toast.makeText(getApplicationContext(), "보낼 사람의 전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(name2.getText().toString().length()==0)Toast.makeText(getApplicationContext(), "받을 사람의 이름을 입력하세요", Toast.LENGTH_SHORT).show();
        else if(phone2.getText().toString().length()==0) Toast.makeText(getApplicationContext(), "보낼 사람의 전화번호를 입력하세요", Toast.LENGTH_SHORT).show();
        else if(!is_setSize) Toast.makeText(getApplicationContext(), "화물의 종류를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(setturn.getText().toString().equals("편도/왕복 설정")) Toast.makeText(getApplicationContext(), "편도/왕복을 선택하세요", Toast.LENGTH_SHORT).show();
        else if(!Show_way.isConfirm) Toast.makeText(getApplicationContext(), "차량 종류를 선택하세요", Toast.LENGTH_SHORT).show();
        else if(purchase.getText().toString().equals("결제수단")) Toast.makeText(getApplicationContext(), "결제 수단을 선택하세요", Toast.LENGTH_SHORT).show();
        else { //결제 내역 작성
            if(Account.current_index!=Load.non_member_index) { //회원일 경우만 저장
                String logfile = Load.accounts[Account.current_index].ID + ".dat";
                String send_name = name1.getText().toString();
                String send_address = sl.getText().toString();
                int send_phone = Integer.parseInt(phone1.getText().toString());
                String receive_name = name2.getText().toString();
                String receive_address = dl.getText().toString();
                int receive_phone = Integer.parseInt(phone2.getText().toString());
                String round = setturn.getText().toString();
                //size와 weight는 static
                String path = "";
                for (int i = 0; i < Show_way.flex.length; i++) {
                    if (Show_way.flex[i] != null) {
                        if (i == 0) path += Show_way.flex[i].address;
                        else path += "->" + Show_way.flex[i].address;
                    }
                }
                String purchase_method = (purchase.getText().toString()).split(": ")[1];
                //message도 static
                Date date = new Date();
                SimpleDateFormat sdformat = new SimpleDateFormat("YY년 MM월 dd일 hh시 mm분");
                String time = sdformat.format(date);
                int cost = Show_way.total_cost;
                try {
                    BufferedWriter bw = new BufferedWriter(new FileWriter(getFilesDir() + logfile, true));
                    bw.write(send_name);
                    bw.newLine();
                    bw.write(send_address);
                    bw.newLine();
                    bw.write(Integer.toString(send_phone));
                    bw.newLine();
                    bw.write(receive_name);
                    bw.newLine();
                    bw.write(receive_address);
                    bw.newLine();
                    bw.write(Integer.toString(receive_phone));
                    bw.newLine();
                    bw.write(round);
                    bw.newLine();
                    bw.write(Integer.toString(size));
                    bw.newLine();
                    bw.write(Integer.toString(weight));
                    bw.newLine();
                    bw.write(path);
                    bw.newLine();
                    bw.write(purchase_method);
                    bw.newLine();
                    bw.write(message);
                    bw.newLine();
                    bw.write(time);
                    bw.newLine();
                    bw.write(Integer.toString(cost));
                    bw.newLine();
                    bw.flush();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(getApplicationContext(), "신청되었습니다", Toast.LENGTH_SHORT).show();
        }
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
