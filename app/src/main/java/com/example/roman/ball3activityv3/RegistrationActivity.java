package com.example.roman.ball3activityv3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;

    private EditText emailEdit;
    private EditText firstNameEdit;
    private EditText lastNameEdit;

    private EditText passEdit;
    private EditText passConfirmEdit;

    private Button btRegistration;

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
                            String login = emailEdit.getText().toString();
                            if (!login.contains("@")) {
                                login = emailEdit.getText().toString() + "@Ball.com";
                            }

                            mAuth.signInWithEmailAndPassword(login, passEdit.getText().toString());
                            FirebaseUser user = mAuth.getInstance().getCurrentUser();
                            myRef.child(user.getUid()).child("/FirstName").setValue(firstNameEdit.getText().toString());
                            myRef.child(user.getUid()).child("/LastName").setValue(lastNameEdit.getText().toString());
                            mAuth.signOut();

                            Toast.makeText(RegistrationActivity.this, "Create Users successful!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrationActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//    public void signing(String email, String password){
//        mAuth.signInWithEmailAndPassword(email, password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser user = mAuth.getCurrentUser();
////                            Toast.makeText(MainActivity.this, "Authorization successful!", Toast.LENGTH_SHORT).show();
//                            if(user != null){
////                                myRef.child(user.getUid()).child("/").setValue(userName);
//                            }
//                        } else {
//                            // If sign in fails, display a message to the user.
//                        }
//                    }
//                });
//    }

    public void onClickRegistration(View view){
        String login;
            login = emailEdit.getText().toString();
            if (!login.contains("@")) {
                login = emailEdit.getText().toString() + "@Ball.com";
            }
            if (Objects.equals(emailEdit.getText().toString(), "")) {
                Toast.makeText(RegistrationActivity.this, "Email failed", Toast.LENGTH_SHORT).show();
            } else {
                if (Objects.equals(firstNameEdit.getText().toString(), "") || Objects.equals(lastNameEdit.getText().toString(), "")) {
                    Toast.makeText(RegistrationActivity.this, "fill in the name and surname", Toast.LENGTH_SHORT).show();
                }else {
                    if (passEdit.getText().length() < 6) {
                        Toast.makeText(RegistrationActivity.this, "Password is less than six characters", Toast.LENGTH_SHORT).show();
                    }else {
                        if (!Objects.equals(passEdit.getText().toString(), passConfirmEdit.getText().toString())) {
                            Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                        }else {
                            registration(login, passEdit.getText().toString());
                        }
                    }


                }

            }

    }
}
