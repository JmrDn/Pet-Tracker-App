package com.example.pettrackerfersoncapstone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    private TextView  noAccountYet;
    private AppCompatButton loginBtn;
    private AppCompatEditText email, password;
   private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private boolean passwordVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initWidgets();
        passwordHideMethod();



        noAccountYet.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), Signup.class));
        });

        loginBtn.setOnClickListener(v->{
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);

            String EMAIL = email.getText().toString();
            String PASSWORD = password.getText().toString();

            if(EMAIL.isEmpty()){
                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);

                email.setError("Enter email");
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(EMAIL).matches()){
                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);

                email.setError("Enter valid email");
            }
            else if(PASSWORD.isEmpty()){
                progressBar.setVisibility(View.GONE);
                loginBtn.setVisibility(View.VISIBLE);

                password.setError("Enter password");
            }
            else{

                progressBar.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.GONE);
                loginUser(EMAIL, PASSWORD);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void passwordHideMethod() {
        password.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                final int Right = 2;

                if (motionEvent.getAction()== MotionEvent.ACTION_UP){
                    if (motionEvent.getRawX()>= password.getRight()-password.getCompoundDrawables()[Right].getBounds().width()){
                        int selection = password.getSelectionEnd();
                        if (passwordVisible){
                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_off_24, 0);
                            // for hide password
                            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible = false;
                        }
                        else {

                            //set drawable image here
                            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0, R.drawable.baseline_visibility_24, 0);
                            // for show password
                            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible = true;

                        }
                        password.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            //Successfully log in
                            Toast.makeText(getApplicationContext(), "Successfully log in", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), HomePage.class));

                        }
                        else{
                            progressBar.setVisibility(View.GONE);
                            loginBtn.setVisibility(View.VISIBLE);

                            Toast.makeText(getApplicationContext(), "Sign in failed", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void initWidgets() {
        noAccountYet = findViewById(R.id.noAccount_Textview);
        email = findViewById(R.id.email_Edittext);
        password = findViewById(R.id.password_Edittext);
        loginBtn = findViewById(R.id.login_Button);
        progressBar = findViewById(R.id.login_ProgressBar);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), HomePage.class));

        }
        super.onStart();
    }
}