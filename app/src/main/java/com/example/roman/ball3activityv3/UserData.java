package com.example.roman.ball3activityv3;

/**
 * Created by Roman on 20.02.2018.
 */

class UserData {
    long Time;
    int Shorts;
    protected String date;
    protected String userName;



    UserData(String UserName, String date, long Time, int Shorts){
        this.userName = UserName;
        this.date = date;
        this.Time = Time;
        this.Shorts = Shorts;
    }
}
