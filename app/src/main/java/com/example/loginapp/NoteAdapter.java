package com.example.loginapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {

    private final ArrayList<Note> notesList;
    private final Context context;

    public NoteAdapter(ArrayList<Note> notesList, Context context){
        this.notesList = notesList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_note_layout,parent,false);
        Log.e("Dhiliban", "onCreateViewHolder: " );
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Note note = notesList.get(position);
        holder.noteTitle.setText(note.getTitle());
        holder.noteContent.setText(note.getContent());
        Log.e("Dhiliban", "onBindViewHolder: "+position );
    }

    @Override
    public int getItemCount() {
        Log.e("Dhiliban", "get Item Count: " + notesList.size());
        return notesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        CardView mCardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.title);
            noteContent = itemView.findViewById(R.id.content);
            mCardView = itemView.findViewById(R.id.notecard);
            view = itemView;
        }
    }
}