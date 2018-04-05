package com.example.roman.ball3activityv3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


class HasConnection{

    static boolean resultWifi = true;


    static boolean isOnline(Context context)
    {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting())
        {
            return true;
        }
        return false;
    }

    static void OnlineWifi(){
       new Thread(new Runnable() {
            public void run() {
                try {
                    int timeoutMs = 1500;
                    Socket sock = new Socket();
                    SocketAddress sockaddr = new InetSocketAddress("8.8.8.8", 53);
                    sock.connect(sockaddr, timeoutMs);
                    sock.close();
                    resultWifi = true;
                } catch (IOException e) {
                    resultWifi = false;
                }
            }
        }).start();
    }



}
