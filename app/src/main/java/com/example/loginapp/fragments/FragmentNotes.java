package com.example.loginapp.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.FirebaseNoteManager;
import com.example.loginapp.Note;
import com.example.loginapp.NoteAdapter;
import com.example.loginapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentNotes extends Fragment {

    RecyclerView recyclerView;
    FirebaseNoteManager firebaseNoteManager;

    private final ArrayList<Note> notes = new ArrayList<Note>();
    private NoteAdapter notesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        firebaseNoteManager = new FirebaseNoteManager();

        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseNoteManager.getAllNotes(notesList -> {
            Log.e("Dhiliban", "onNoteReceived: " + notesList);
            notesAdapter = new NoteAdapter(notesList,this.getContext());
            recyclerView.setAdapter(notesAdapter);
            notesAdapter.notifyDataSetChanged();
        });
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpOnClickListeners();
    }

    private void setUpOnClickListeners() {
        FloatingActionButton onClickingAddNoteButton = Objects.requireNonNull(getView()).findViewById(R.id.addNotesFloatingButton);
        onClickingAddNoteButton.setOnClickListener(v -> {
            Fragment fragment = new AddingNotesFragment();
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }
}