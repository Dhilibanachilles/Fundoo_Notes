package com.example.loginapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, password;
    Button signIn;
    TextView signUp;
    FirebaseAuth firebaseAuthenticator;
    private FirebaseAuth.AuthStateListener fireBaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuthenticator = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        signIn = findViewById(R.id.button);
        signUp = findViewById(R.id.textView);

        fireBaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser logInFireBaseUser = firebaseAuthenticator.getCurrentUser();
                if(logInFireBaseUser != null) {
                    Toast.makeText(LoginActivity.this, "You are logged in", Toast.LENGTH_SHORT).show();
                    Intent a = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(a);
                } else {
                    Toast.makeText(LoginActivity.this, "Please Login", Toast.LENGTH_SHORT).show();
                }
            }
        };

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pasword = password.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter a email id");
                    emailId.requestFocus();
                } else if (pasword.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (email.isEmpty() && pasword.isEmpty()) {
                        Toast.makeText(LoginActivity.this, "All fields are empty", Toast.LENGTH_SHORT).show();
                    } else if (!(email.isEmpty() && pasword.isEmpty())) {
                        firebaseAuthenticator.signInWithEmailAndPassword(email, pasword).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Login error, try again", Toast.LENGTH_SHORT).show();
                                } else {
                                    Intent toHome = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(toHome);
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Error occurred", Toast.LENGTH_SHORT).show();
                    }

            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toSignUp = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(toSignUp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuthenticator.addAuthStateListener(fireBaseAuthStateListener);
    }
}