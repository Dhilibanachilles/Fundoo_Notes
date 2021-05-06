package com.example.loginapp.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private EditText emailId, password, name, phone;
    private Button signUp;
    private TextView signIn;
    private FirebaseAuth firebaseAuthenticator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       findViews();
       setUpClickListeners();
    }

    public void findViews() {
        firebaseAuthenticator = FirebaseAuth.getInstance();
        name = findViewById(R.id.editTextName);
        emailId = findViewById(R.id.editTextTextEmailAddress);
        password = findViewById(R.id.editTextTextPassword);
        signUp = findViewById(R.id.button);
        signIn = findViewById(R.id.textView);
        phone = findViewById(R.id.editTextPhone);
    }

    private void setUpClickListeners() {
        signUp.setOnClickListener(v -> userRegistration());

        signIn.setOnClickListener(v -> {
            Intent toLogInPage = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(toLogInPage);
            finish();
        });
    }

    private boolean isValidName(String userName) {
        if(userName.isEmpty()) {
            name.setError("Please enter your name");
            name.requestFocus();
            return false;
        } else if(userName.matches("[0-9*$%#&^()@!_+{}';]*")) {
            name.setError("Please enter your first and last name");
            name.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidEmail(String email) {
        if(email.isEmpty()) {
            emailId.setError("Please enter your email address");
            emailId.requestFocus();
            return false;
        } else if(!email.matches("^[a-zA-Z]+([._+-]{0,1}[a-zA-Z0-9]+)*@[a-zA-Z0-9]+.[a-zA-Z]{2,4}+(?:\\.[a-z]{2,}){0,1}$")) {
            emailId.setError("Please enter valid email address");
            emailId.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidPassword(String pasword) {
        if(pasword.isEmpty()) {
            password.setError("Please enter the password");
            password.requestFocus();
            return false;
        } else if(!pasword.matches("(^(?=.*[A-Z]))(?=.*[0-9])(?=.*[a-z])(?=.*[@*&^%#-*+!]{1}).{8,}$")) {
            password.setError("Please enter atleast 1 uppercase, specialcase, number");
            password.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if(phoneNumber.isEmpty()) {
            phone.setError("Please enter phone number");
            phone.requestFocus();
            return false;
        } else if(!phoneNumber.matches("(([1-9]{2})?)[ ][0-9]{10}")) {
            phone.setError("Please enter valid phone number");
            phone.requestFocus();
            return false;
        } else {
            return true;
        }
    }

        public void userRegistration() {
            String userName = name.getText().toString();
            String email = emailId.getText().toString();
            String pasword = password.getText().toString();
            String phoneNumber = phone.getText().toString();

            if(!isValidName(userName)) {
                return;
            } else if(!isValidEmail(email)) {
                return;
            }else if(!isValidPassword(pasword)) {
                return;
            } else if(!isValidPhoneNumber(phoneNumber)) {
                return;
            } else if (!(email.isEmpty() && pasword.isEmpty() && userName.isEmpty() && phoneNumber.isEmpty())) {
                firebaseAuthenticator.createUserWithEmailAndPassword(email, pasword).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "SignUp unsuccessful, Please try again", Toast.LENGTH_SHORT).show();
                        } else {
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        }
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "SignUp error", Toast.LENGTH_SHORT).show();
            }
    }
}

