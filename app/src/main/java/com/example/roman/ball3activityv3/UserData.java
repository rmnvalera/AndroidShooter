package com.example.roman.ball3activityv3;

class UserData {
    long Time;
    int Shorts;
    protected String date;
    protected String userName;

    protected String firstName;
    protected String lastName;



    UserData(String UserName, String date, long Time, int Shorts){
        this.userName = UserName;
        this.date = date;
        this.Time = Time;
        this.Shorts = Shorts;
    }

    UserData(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
