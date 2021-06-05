package com.example.loginapp.data_manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String isLoggedIn = "Logged_In";
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;
    Context context;
    private static final String TITLE = "Title";
    private static final String DESCRIPTION = "Description";

    public SharedPreferenceHelper(Context context) {
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

    public void setNoteTitle(String noteTitle ){
        editor = sharedPreference.edit();
        editor.putString(TITLE, noteTitle);
        editor.apply();
    }

    public void setNoteDescription(String noteDescription ){
        editor = sharedPreference.edit();
        editor.putString(DESCRIPTION, noteDescription);
        editor.apply();
    }

    public String getNoteTitle(){
        return sharedPreference.getString(TITLE,null);
    }

    public String getNoteDescription(){
        return sharedPreference.getString(DESCRIPTION,null);
    }
}