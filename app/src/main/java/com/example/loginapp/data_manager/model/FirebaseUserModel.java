package com.example.loginapp.data_manager.model;

public class FirebaseUserModel {

    private String userEmail;

    public FirebaseUserModel(String userEmail) {
        this.userEmail = userEmail;
    }

    public FirebaseUserModel() {
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}