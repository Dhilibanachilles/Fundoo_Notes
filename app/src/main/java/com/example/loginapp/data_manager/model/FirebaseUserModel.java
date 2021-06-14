package com.example.loginapp.data_manager.model;

public class FirebaseUserModel {
    private String userEmail;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userName;

    public FirebaseUserModel(String userEmail, String userName) {
        this.userEmail = userEmail;
        this.userName = userName;
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