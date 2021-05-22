package com.example.loginapp.adapters;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.R;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Adapter extends RecyclerView.Adapter<MyViewHolder> {
    private static final String TAG = "NoteAdapter";
    private ArrayList<FirebaseNoteModel> notesList;
    private final MyViewHolder.OnNoteListener onNoteListener;
    private final ArrayList<FirebaseNoteModel> noteSource;
    private Timer timer;

    public Adapter(ArrayList<FirebaseNoteModel> notesList,
                   MyViewHolder.OnNoteListener onNoteListener) {
        this.notesList = notesList;
        this.onNoteListener = onNoteListener;
        noteSource = notesList;
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

    public void searchNotes(final String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()){
                    notesList = noteSource;
                } else {
                    ArrayList<FirebaseNoteModel> temp = new ArrayList<>();
                    for (FirebaseNoteModel note : noteSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase())
                                || note.getDescription().toLowerCase().
                                contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    notesList = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        },       500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
}