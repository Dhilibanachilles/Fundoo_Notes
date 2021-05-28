package com.example.loginapp.data_manager;

import com.example.loginapp.data_manager.model.FirebaseLabelModel;
import com.example.loginapp.util.CallBack;

import java.util.ArrayList;

public interface LabelManager {
    void addLabel(String label, CallBack<String> listener);
    void getAllLabels(CallBack<ArrayList<FirebaseLabelModel>> listCallBack);
}
