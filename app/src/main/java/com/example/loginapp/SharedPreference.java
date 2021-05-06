package com.example.loginapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreference  {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String isLoggedIn = "Logged_In";
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    public SharedPreference(Context context) {
        sharedPreference = context.getSharedPreferences(SHARED_PREFS,Context.MODE_PRIVATE);
    }

    public void setLoggedIn(boolean value){
        editor  = sharedPreference.edit();
        editor.putBoolean(isLoggedIn,value);
        editor.apply();
    }

    public boolean getLoggedIn() {
        return sharedPreference.getBoolean(isLoggedIn,false);
    }
}