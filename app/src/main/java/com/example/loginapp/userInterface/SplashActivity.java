package com.example.loginapp.userInterface;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loginapp.R;
import com.example.loginapp.SharedPreference;
import com.example.loginapp.authentication.HomeActivity;
import com.example.loginapp.authentication.LoginActivity;


public class SplashActivity extends AppCompatActivity {

    private boolean isLoggedIn;
    SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreference = new SharedPreference(this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams
                .FLAG_FULLSCREEN);

        new Handler().postDelayed(() -> {

            isLoggedIn = sharedPreference.getLoggedIn();
            Intent SplashAct;
            if(isLoggedIn) {
                SplashAct = new Intent(SplashActivity.this, HomeActivity.class);
            } else{
                SplashAct = new Intent(SplashActivity.this, LoginActivity.class);
            }
            startActivity(SplashAct);
            finish();
        },4000);
    }
}
