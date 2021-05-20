package com.example.loginapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.R;
import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "NoteAdapter";
    FirebaseNoteManager noteManager;
    private final ArrayList<FirebaseNoteModel> notesList;
    private final MyViewHolder.OnNoteListener onNoteListener;

    public Adapter(ArrayList<FirebaseNoteModel> notesList, MyViewHolder.OnNoteListener onNoteListener) {
        this.notesList = notesList;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.view_note_layout,parent,false);
        Log.e(TAG, "onCreateViewHolder: " );
        return new MyViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        FirebaseNoteModel note = notesList.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteDescription.setText(note.getDescription());
        Log.e(TAG, "onBindViewHolder: "+ position);
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "get Item Count: " + notesList.size());
        return notesList.size();
    }

    public FirebaseNoteModel getItem(int position) {
        return notesList.get(position);
    }

    public void removeNote(int position) {
        notesList.remove(position);
        notifyItemRemoved(position);
    }
}