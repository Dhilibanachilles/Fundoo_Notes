package com.example.loginapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.loginapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Objects;

public class FragmentNotes extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        return inflater.inflate(R.layout.fragment_notes, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        FloatingActionButton addnote = Objects.requireNonNull(getView()).findViewById(R.id.addNotesFloatingButton);
        addnote.setOnClickListener(v -> {
            Fragment fragment = new AddingNotesFragment();
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }
}