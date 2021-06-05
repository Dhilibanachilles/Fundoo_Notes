package com.example.loginapp.data_manager;

import com.example.loginapp.data_manager.model.FirebaseLabelModel;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.util.CallBack;

import java.util.ArrayList;

public interface NoteManager {
    void getAllNotes(CallBack<ArrayList<FirebaseNoteModel>> listener);
    void addNote(String title, String description, CallBack<String> addListener);
    void addLabel(String label, CallBack<String> listener);
    void getAllLabels(CallBack<ArrayList<FirebaseLabelModel>> listCallBack);
}