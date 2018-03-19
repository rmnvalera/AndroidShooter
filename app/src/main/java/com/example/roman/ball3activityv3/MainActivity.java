package com.example.roman.ball3activityv3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.Manifest;
import android.support.design.widget.Snackbar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText ETemail;
    private EditText ETpassword;

    private static final int PERMISSION_REQUEST_CODE = 123;

    private DatabaseReference myRef;

    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if(user != null){

                }else{
                }
            }
        };

        ETemail = (EditText)findViewById(R.id.editLogin);
        ETpassword = (EditText)findViewById(R.id.editPass);

        findViewById(R.id.btSignIn).setOnClickListener(this);
        findViewById(R.id.btSignUp).setOnClickListener(this);

        myRef = FirebaseDatabase.getInstance().getReference();
        myPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            goToMainMenu();
        }
    }


    @Override
    public void onClick(View view) {
        String login;
        if(view.getId() == R.id.btSignUp){
            goToBallRegistration();
            //registration(login, ETpassword.getText().toString());
        }else {
            if(!ETemail.getText().toString().equals("") && !ETpassword.getText().toString().equals("")){
                login = ETemail.getText().toString();
                if (!login.contains("@")) {
                    login = ETemail.getText().toString() + "@Ball.com";
                }

                if (view.getId() == R.id.btSignIn) {
                    signing(login, ETpassword.getText().toString());
                    //keyboard clouse
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                goToMainMenu();

                } else {
                    Toast.makeText(MainActivity.this, "Логин или Пароль не заполнены!", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }

    public void signing(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(MainActivity.this, "Авторизация прошла успешно!", Toast.LENGTH_SHORT).show();
                            if(user != null){
                                if (hasPermissions()){
//                                    Intent intent = new Intent(MainActivity.this, Ball3Activity.class);
//                                    startActivity(intent);
                                    goToMainMenu();
                                }
                                else {
                                    //our app doesn't have permissions, So i m requesting permissions.
                                    requestPermissionWithRationale();
                                }
                            }
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Ошибка аутентификации.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void registration(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "createUserWithEmail:success");
//                            FirebaseUser user = mAuth.getCurrentUser();

                            Toast.makeText(MainActivity.this, "Create Users successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
                    // if user granted all permissions.
                    allowed = allowed && (res == PackageManager.PERMISSION_GRANTED);
                }

                break;
            default:
                // if user not granted permissions.
                allowed = false;
                break;
        }

        if (allowed){
            //user granted all permissions we can perform our task.
            goToMainMenu();
        }
        else {
            // we will give warning to user that they haven't granted permissions.
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
        Snackbar.make(MainActivity.this.findViewById(R.id.activity_view), "Storage permission isn't granted" , Snackbar.LENGTH_LONG)
                .setAction("НАСТРОЙКИ", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApplicationSettings();

                        Toast.makeText(getApplicationContext(),
                                "Разрешение на камеру не предоставляется",
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
            goToMainMenu();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void requestPermissionWithRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            final String message = "Требуется разрешение на Камеру!";
            Snackbar.make(MainActivity.this.findViewById(R.id.activity_view), message, Snackbar.LENGTH_LONG)
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

    private void goToBallRegistration(){
        Intent intent = new Intent(MainActivity.this, RegistrationActivity.class);
        try {
            MainActivity.this.finish();
            startActivity(intent);
        }catch (Exception e){
            Log.i("Exception", "" + e);
        }
    }

    private void goToMainMenu(){
        getTimeUser();
        Intent intent = new Intent(MainActivity.this, MenuActivity.class);
        try {
            MainActivity.this.finish();
            startActivity(intent);
        }catch (Exception e){
            Log.i("Exception", "" + e);
        }
    }

    private void getTimeUser(){
        FirebaseUser user = mAuth.getInstance().getCurrentUser();
        if(user != null) {
            myRef.child(user.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int timeDB = dataSnapshot.child("Time").getValue(Integer.class);
                    myEditor = myPreferences.edit();
                    myEditor.putLong("TIME", timeDB);
                    myEditor.commit();
                    Log.i("UserTime:", "" + timeDB);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }else{
            Log.i("UserTime:", "User = null");
        }
    }


}
