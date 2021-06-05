package com.example.loginapp.fragments.notes;

import com.example.loginapp.data_manager.model.FirebaseNoteModel;

public interface AddNoteListener {
    void onNoteAdded(FirebaseNoteModel note);
}
