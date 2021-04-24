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

public class MainActivity extends AppCompatActivity {
    EditText emailId, password, name, phone;
    Button signUp;
    TextView signIn;
    FirebaseAuth firebaseAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuthenticator = FirebaseAuth.getInstance();
        name = findViewById(R.id.editTextName);
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        signUp = findViewById(R.id.button);
        signIn = findViewById(R.id.textView);
        phone = findViewById(R.id.editTextPhone);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pasword = password.getText().toString();
                String userName = name.getText().toString();
                String phoneNumber = phone.getText().toString();

                if (email.isEmpty()) {
                    emailId.setError("Please enter a email id");
                    emailId.requestFocus();
                } else if (pasword.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                } else if (userName.isEmpty()) {
                    name.setError("Please enter your name");
                    name.requestFocus();
                } else if (phoneNumber.isEmpty()) {
                    phone.setError("Please enter phone number");
                    phone.requestFocus();
                } else if (email.isEmpty() && pasword.isEmpty() && userName.isEmpty() && phoneNumber.isEmpty()) {
                    Toast.makeText(MainActivity.this, "All fields are empty", Toast.LENGTH_SHORT).show();
                } else if (!(email.isEmpty() && pasword.isEmpty() && userName.isEmpty() && phoneNumber.isEmpty())) {
                    firebaseAuthenticator.createUserWithEmailAndPassword(email, pasword).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "SignUp unsuccessful, Please try again", Toast.LENGTH_SHORT).show();
                            } else {
                                startActivity(new Intent(MainActivity.this, HomeActivity.class));
                            }
                        }
                    });
                } else {
                    Toast.makeText(MainActivity.this, "SignUp error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(a);
            }
        });
    }
}
