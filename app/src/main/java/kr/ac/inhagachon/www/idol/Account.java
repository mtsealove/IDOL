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
    protected LOG[] logs;

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
    String company;
    String date;
    String loaction;
}

