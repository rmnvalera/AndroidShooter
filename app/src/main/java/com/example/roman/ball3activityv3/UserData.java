package com.example.roman.ball3activityv3;

/**
 * Created by Roman on 20.02.2018.
 */

public class UserData {
    public String userName;
    public String Time;
    //String date = new SimpleDateFormat("dd.MM.yyyy").format(new Date());
    public int Shorts;


    public UserData(){

    }

    public UserData(String userName, String Time, int Shorts){
        this.userName = userName;
        this.Time = Time;
        this.Shorts = Shorts;
    }
}
