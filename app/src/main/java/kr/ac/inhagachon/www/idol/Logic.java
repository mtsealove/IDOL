package kr.ac.inhagachon.www.idol;

import android.util.Log;

//운송 수단을 정의하기 위한 클래스
public class Logic {
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

    Logic(String location, int priority, int id, int next, String address, String transportation, double distance, int min, int cost, double speed, double rate) {
        this.location=location;
        this.priority=priority;
        this.id=id;
        this.next=next;
        this.address=address;
        this.transportation=transportation;
        this.distance=distance;
        this.min=min;
        this.cost=cost;
        this.speed=speed;
        this.rate=rate;
    }

    void show() {
        Log.d("logic", location);
        Log.d("logic", String.valueOf(priority));
        Log.d("logic", transportation);
        Log.d("logic", String.valueOf(distance));
        Log.d("logic", String.valueOf(min));
        Log.d("logic", String.valueOf(cost));
        Log.d("logic", String.valueOf(speed));
        Log.d("logic", String.valueOf(rate));
    }
}
