package com.example.roman.ball3activityv3;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class MenuActivity extends AppCompatActivity {

    ListView    listViewMenu;
    Toolbar     toolbar;
    TextView    TextOfflineResult;

    private FirebaseAuth        mAuth;
    private DatabaseReference   myRef;

    FirebaseUser        user = mAuth.getInstance().getCurrentUser();
    SharedPreferences   myPreferences;
    SharedPreferences.Editor myEditor;


    private static final int PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TextOfflineResult = (TextView)findViewById(R.id.TOfllineResult);

        //DB
        myPreferences = PreferenceManager.getDefaultSharedPreferences(MenuActivity.this);
        myRef = FirebaseDatabase.getInstance().getReference();

        mAuth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.toolbarMenu);
        if(user != null){
            toolbar.setSubtitle("Ваш логин: " + user.getEmail());
        }

        String[] listMenu = getResources().getStringArray(R.array.ListMainMenu);
        listViewMenu = (ListView)findViewById(R.id.listViewMainMenu);


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, listMenu);

        listViewMenu.setAdapter(adapter);


        listViewMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                TextView textView = (TextView) itemClicked;
                String stTrext = textView.getText().toString();

                if(stTrext.equals("Старт")){
                    onItemStart();
                }
                if(stTrext.equals("Статистика")){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ball3activity.firebaseapp.com/"));
                    startActivity(browserIntent);
                }
                if(stTrext.equals("Мой результат")){
                    long AtimeDB = myPreferences.getLong("TIME", 0);
                    if(AtimeDB != 0){
                        TextOfflineResult.setText("Ваш результат: " + AtimeDB + "сек.");
                    }else{
                        Toast.makeText(getApplicationContext(),
                                "Его еще нет", Toast.LENGTH_SHORT)
                                .show();
                    }
                }
                if(stTrext.equals("Выйти из аккаунта")){

                    myEditor = myPreferences.edit();
                    myEditor.putLong("TIME", 0);
                    myEditor.commit();

                    mAuth.signOut();
                    Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                    try {
                        MenuActivity.this.finish();
                        startActivity(intent);
                    }catch (Exception e){
                        Log.i("Exception","" + e);
                    }
                }
            }
        });

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        long BtimeDB = myPreferences.getLong("TIME", 0);
//
//        if(BtimeDB == 0){
//            TextOfflineResult.setText("");
//        }else{
//            TextOfflineResult.setText("Ваш результат: " + BtimeDB + "сек.");
//        }
//    }

    private void onItemStart(){
        if (hasPermissions()){
            goToBall3Activity();
        }
        else {
            requestPermissionWithRationale();
        }
    }

    ///
    private boolean hasPermissions(){
        int res;
        //string array of permissions,
        String[] permissions = new String[]{Manifest.permission.CAMERA};

        for (String perms : permissions){
            res = checkCallingOrSelfPermission(perms);
            if (!(res == PackageManager.PERMISSION_GRANTED)){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean allowed = true;

        switch (requestCode){
            case PERMISSION_REQUEST_CODE:

                for (int res : grantResults){
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                allowed = false;
                break;
        }

        if (allowed){
            goToBall3Activity();
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)){
                    Toast.makeText(this, "Разрешения на камеру запрещены.", Toast.LENGTH_SHORT).show();

                } else {
                    showNoStoragePermissionSnackbar();
                }
            }
        }

    }

    public void showNoStoragePermissionSnackbar() {
        Snackbar.make(MenuActivity.this.findViewById(R.id.activityMenu_view), "Разрешение на камеру не предоставляется" , Snackbar.LENGTH_LONG)
                .setAction("НАСТРОЙКИ", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                "Открыть разрешение и предоставить разрешение на камеру",
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                })
                .show();
    }

    public void openApplicationSettings() {
        Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(appSettingsIntent, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            goToBall3Activity();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            final String message = "Требуется разрешение на камеру, чтобы играть";
            Snackbar.make(MenuActivity.this.findViewById(R.id.activityMenu_view), message, Snackbar.LENGTH_LONG)
                    .setAction("ПРЕДОСТАВИТЬ", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            requestPerms();
                        }
                    })
                    .show();
        } else {
            requestPerms();
        }
    }

    private void requestPerms(){
        String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            requestPermissions(permissions,PERMISSION_REQUEST_CODE);
        }
    }


    private void goToBall3Activity(){
        if(!HasConnection.isOnline(MenuActivity.this)){
            HasConnection.resultWifi = true;
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MenuActivity.this);
            dlgAlert.setMessage("У вас нет интернет соиденения! Перейдите в offline-режим или подключите интернет!!");
            dlgAlert.setTitle("Ball3Activity: Warning!");
            dlgAlert.setPositiveButton("ок", null);
//            dlgAlert.setPositiveButton("Да", new DialogInterface.OnClickListener() {
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent(MenuActivity.this, Ball3Activity.class);
//                    try {
//                        MenuActivity.this.finish();
//                        intent.putExtra("isOffline", true);
//                        startActivity(intent);
//                    }catch (Exception e){}
//                }
//            });
//            dlgAlert.setNegativeButton("Нет", null);
//                dlgAlert.setCancelable(false);
            dlgAlert.create().show();
        }else {
            Intent intent = new Intent(MenuActivity.this, Ball3Activity.class);
            try {
                this.finish();
                intent.putExtra("online", true);
                startActivity(intent);
            }catch (Exception e){}
        }

    }


}
