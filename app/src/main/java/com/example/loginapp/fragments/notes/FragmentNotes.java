package com.example.loginapp.fragments.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.loginapp.R;
import com.example.loginapp.adapters.NoteAdapter;
import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.fragments.AddingNotesFragment;
import com.example.loginapp.util.ViewState;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentNotes extends Fragment {

    RecyclerView recyclerView;
    FirebaseNoteManager firebaseNoteManager;
    private static final String TAG = "FragmentNotes";
    private final ArrayList<FirebaseNoteModel> firebaseNoteModels = new ArrayList<FirebaseNoteModel>();
    private NoteAdapter notesAdapter;
    private NotesViewModel notesViewModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        final StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
              StaggeredGridLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        recyclerView.setHasFixedSize(true);
        firebaseNoteManager = new FirebaseNoteManager();
        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notesViewModel.notesMutableLiveData.observe(getViewLifecycleOwner(), new Observer<ViewState<ArrayList<FirebaseNoteModel>>>() {
            @Override
            public void onChanged(ViewState<ArrayList<FirebaseNoteModel>> arrayListViewState) {
                if(arrayListViewState instanceof ViewState.Loading) {
                    Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
                } else if (arrayListViewState instanceof ViewState.Success) {
                    ArrayList<FirebaseNoteModel> notes = ((ViewState.Success<ArrayList<FirebaseNoteModel>>) arrayListViewState).getData();
                    Log.e(TAG, "onNoteReceived: " + notes);
                    notesAdapter = new NoteAdapter(notes);
                    recyclerView.setAdapter(notesAdapter);
                    notesAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
//        firebaseNoteManager.getAllNotes(new CallBack<ArrayList<FirebaseNoteModel>>() {
//            @Override
//            public void onSuccess(ArrayList<FirebaseNoteModel> data) {
//                Log.e(TAG, "onNoteReceived: " + data);
//                notesAdapter = new NoteAdapter(data);
//                recyclerView.setAdapter(notesAdapter);
//                notesAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onFailure(Exception exception) {
//                Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
//            }
//        });
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