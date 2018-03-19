package com.example.roman.ball3activityv3;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private EditText emailEdit;
    private EditText firstNameEdit;
    private EditText lastNameEdit;

    private EditText passEdit;
    private EditText passConfirmEdit;

    private Button btRegistration;

    SharedPreferences myPreferences;
    SharedPreferences.Editor myEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        emailEdit = (EditText)findViewById(R.id.editLoginReg);
        firstNameEdit = (EditText)findViewById(R.id.editFirstNameReg);
        lastNameEdit = (EditText)findViewById(R.id.editLastNameReg);
        passEdit = (EditText)findViewById(R.id.editPassReg);
        passConfirmEdit = (EditText)findViewById(R.id.editPassConfirmReg);
        btRegistration = (Button)findViewById(R.id.RegisterReg);

        myRef = FirebaseDatabase.getInstance().getReference();

        //toolbar button back
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // back button pressed
//                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

        myPreferences = PreferenceManager.getDefaultSharedPreferences(RegistrationActivity.this);
    }

    public void registration(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String logIn = emailEdit.getText().toString();
                            String passwordIn = passEdit.getText().toString();
                            if (!logIn.contains("@")) {
                                logIn = emailEdit.getText().toString() + "@Ball.com";
                            }
                            Toast.makeText(RegistrationActivity.this, "Создание пользователя успешно!", Toast.LENGTH_SHORT).show();
                            signing(logIn, passwordIn);
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Ошибка аутентификации.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void signing(String email, String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            String login = emailEdit.getText().toString();
                            String FirstName = firstNameEdit.getText().toString();
                            String LastName = lastNameEdit.getText().toString();
                            if (!login.contains("@")) {
                                login = emailEdit.getText().toString() + "@Ball.com";
                            }

                            if(user != null){
                                    myRef.child(user.getUid()).child("/login").setValue(login);
                                    myRef.child(user.getUid()).child("/FirstName").setValue(FirstName);
                                    myRef.child(user.getUid()).child("/LastName").setValue(LastName);
                                    try {
                                        Thread.sleep(500); //Приостанавливает поток на 1 секунду
                                    } catch(Exception e) {
                                    }

                                    myEditor = myPreferences.edit();
                                    myEditor.putLong("TIME", 0);
                                    myEditor.commit();

                                    goToMainMenu();
                            }
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Ошибка аутентификации.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void onClickRegistration(View view){
        String login;
            login = emailEdit.getText().toString();
            if (!login.contains("@")) {
                login = emailEdit.getText().toString() + "@Ball.com";
            }
            if(emailEdit.getText().toString().equals("")){
                Toast.makeText(RegistrationActivity.this, "Заполните ваш логин!", Toast.LENGTH_SHORT).show();
            } else {
                if(firstNameEdit.getText().toString().equals("") || lastNameEdit.getText().toString().equals("")){
                    Toast.makeText(RegistrationActivity.this, "Заполните свое имя и фамилию!", Toast.LENGTH_SHORT).show();
                }else {
                    if (passEdit.getText().length() < 6) {
                        Toast.makeText(RegistrationActivity.this, "Пароль меньше шести символов!", Toast.LENGTH_SHORT).show();
                    }else {
                        if (!passEdit.getText().toString().equals(passConfirmEdit.getText().toString())){
                            Toast.makeText(RegistrationActivity.this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                        }else {
                            registration(login, passEdit.getText().toString());
//                            signing(login, passEdit.getText().toString());

                        }
                    }


                }

            }

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
        try{
            RegistrationActivity.this.finish();
            startActivity(intent);
        }catch (Exception e){
            Log.i("Exception", "" + e);
        }
    }



    private void goToMainMenu(){
        Intent intent = new Intent(RegistrationActivity.this, MenuActivity.class);
        try {
            RegistrationActivity.this.finish();
            startActivity(intent);
        }catch (Exception e){
            Log.i("Exception", "" + e);
        }
    }
}
