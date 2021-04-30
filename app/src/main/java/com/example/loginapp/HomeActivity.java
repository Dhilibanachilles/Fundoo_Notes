package com.example.loginapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import static com.example.loginapp.LoginActivity.isLoggedIn;
import static com.example.loginapp.LoginActivity.SHARED_PREFS;

public class HomeActivity extends AppCompatActivity {
    Button logOutButton;
    FirebaseAuth fireBaseAuthenticator;
    private FirebaseAuth.AuthStateListener fireBaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        logOutButton = findViewById(R.id.button2);

        logOutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            SharedPreferences mySharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            SharedPreferences.Editor editor = mySharedPref.edit();
            editor.putBoolean(LoginActivity.isLoggedIn,false);
            editor.apply();
            Intent toLogInPage = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(toLogInPage);
            finish();
        });
    }
}