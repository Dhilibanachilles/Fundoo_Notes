package com.example.loginapp;

import java.util.ArrayList;

public interface NoteListener{
    void onNoteReceived(ArrayList<Note> noteslist);
}