package com.example.securefils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity implements View.OnClickListener{

    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    EditText editusername, editpassword;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        //Hooks
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logoImage);
        logoText = findViewById(R.id.logoName);
        sloganText = findViewById(R.id.form_name);
        editusername = findViewById(R.id.username);
        editpassword = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);

        callSignUp.setOnClickListener(this);
        login_btn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.signup_screen:
                Intent intent =  new Intent(Login.this, SignUp.class);

                Pair[] pairs = new Pair[7];

                pairs[0] = new Pair<View, String>(image, "logoImage");
                pairs[1] = new Pair<View, String>(logoText, "logoName");
                pairs[2] = new Pair<View, String>(sloganText, "logo_desc");
                pairs[3] = new Pair<View, String>(editusername, "username_tran");
                pairs[4] = new Pair<View, String>(editpassword, "password_tran");
                pairs[5] = new Pair<View, String>(login_btn, "button_tran");
                pairs[6] = new Pair<View, String>(callSignUp, "signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent, options.toBundle());
                break;

            case R.id.login_btn:
                userLogin();
                break;
        }
    }

    private void userLogin() {
        String email = editusername.getText().toString().trim();
        String password = editpassword.getText().toString().trim();

        if(email.isEmpty()){
            editusername.setError("Email is required!");
            editusername.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editusername.setError("Enter valid email");
            editusername.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editpassword.setError("Password is required!");
            editpassword.requestFocus();
            return;
        }

        if(password.length() < 6 ){
            editpassword.setError("Min password length is 6 characters!");
            editpassword.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    startActivity(new Intent(Login.this, Dashboard.class));
                }else{
                    Toast.makeText(Login.this, "Faild to sign in! Please check your credentials", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}