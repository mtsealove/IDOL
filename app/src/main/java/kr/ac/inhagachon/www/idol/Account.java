package kr.ac.inhagachon.www.idol;

//계정에 저장될 정보
public class Account {
    protected String name;
    protected String birth;
    protected int phone_number;
    protected String ID;
    protected String password;
    protected static int current_index;
    static int count=0; //계정의 개수
    protected LOG[] logs=new LOG[100];

    Account(String name, String birth,int phone_number, String ID, String password) {
        this.name=name;
        this.birth=birth;
        this.phone_number=phone_number;
        this.ID=ID;
        this.password=password;
    }
}
//계정에 저장될 정보
class LOG {
    String send_name;
    String send_address;
    int send_phone;
    String receive_name;
    String receive_address;
    int receive_phone;
    String round;
    int size;
    int weight;
    String path;
    String purchase_method;
    String message;
    String time;
    int cost;

    LOG(String send_name, String send_address, int send_phone, String receive_name, String receive_address, int receive_phone, String round, int size, int weight, String path, String purchase_method, String message, String time, int cost) {
        this.send_name=send_name;
        this.send_address=send_address;
        this.send_phone=send_phone;
        this.receive_name=receive_name;
        this.receive_address=receive_address;
        this.receive_phone=receive_phone;
        this.round=round;
        this.size=size;
        this.weight=weight;
        this.path=path;
        this.purchase_method=purchase_method;
        this.message=message;
        this.time=time;
        this.cost=cost;
    }

}

