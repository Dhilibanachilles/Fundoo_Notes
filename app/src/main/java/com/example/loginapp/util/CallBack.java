package com.example.loginapp.util;

public interface CallBack<T> {
    void onSuccess(T data);
    void onFailure(Exception exception);
}