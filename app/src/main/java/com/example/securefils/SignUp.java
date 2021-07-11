package com.example.securefils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private EditText name, username, email, password;

    private Button registerUser, signInUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        signInUser = (Button) findViewById(R.id.callsignin);
        signInUser.setOnClickListener(this);

        name = (EditText) findViewById(R.id.name);
        username = (EditText) findViewById(R.id.username);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.registerUser:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String editname = name.getText().toString().trim();
        String editusername = username.getText().toString().trim();
        String editemail = email.getText().toString().trim();
        String editpassword = password.getText().toString().trim();

        if(editname.isEmpty()){
            name.setError("Full Name is required");
            name.requestFocus();
            return;
        }

        if(editusername.isEmpty()){
            username.setError("User Name is required");
            username.requestFocus();
            return;
        }

        if(editemail.isEmpty()){
            email.setError("Email is required");
            email.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(editemail).matches()){
            email.setError("Please provide valid email!");
            email.requestFocus();
            return;
        }


        if(editpassword.isEmpty()){
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if(editpassword.length() < 6){
            password.setError("Min password length should be 6 characters!");
            password.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(editemail, editpassword)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(editname, editusername, editemail);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SignUp.this, "User has been registerd successfully!", Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(SignUp.this, "Faild to register! Try Again", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(SignUp.this, "Faild to register! Try Again", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}