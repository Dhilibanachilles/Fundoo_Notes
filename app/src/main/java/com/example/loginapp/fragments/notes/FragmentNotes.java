package com.example.loginapp.fragments.notes;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.loginapp.R;
import com.example.loginapp.adapters.Adapter;
import com.example.loginapp.adapters.MyViewHolder;
import com.example.loginapp.dashboard.HomeActivity;
import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.fragments.AddingNotesFragment;
import com.example.loginapp.util.CallBack;
import com.example.loginapp.util.ViewState;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class FragmentNotes extends Fragment {

    RecyclerView recyclerView;
    FirebaseNoteManager firebaseNoteManager;
    private static final String TAG = "FragmentNotes";
    private Adapter notesAdapter;
    private NotesViewModel notesViewModel;
    private RecyclerView.LayoutManager layoutManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        final StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.recyclerView);
        layoutManager = decideLayoutManager(HomeActivity.IS_LINEAR_LAYOUT);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        firebaseNoteManager = new FirebaseNoteManager();
        notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                try {
                    String noteId = notesAdapter.getItem(position).getId();
                    notesAdapter.removeNote(position);
                    firebaseNoteManager.deleteNote(noteId);
                    Toast.makeText(getContext(), "Note Deleted", Toast.LENGTH_SHORT).show();
                } catch(IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        }; 
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        EditText inputSearch = view.findViewById(R.id.search_notes);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                firebaseNoteManager.getAllNotes(new CallBack<ArrayList<FirebaseNoteModel>>() {
                    @Override
                    public void onSuccess(ArrayList<FirebaseNoteModel> data) {
                        Log.e(TAG, "onNoteReceived: " + data);
                        if (data.size() != 0) {
                            notesAdapter.searchNotes(s.toString());
                        }
                    }

                    @Override
                    public void onFailure(Exception exception) {
                    }

                });
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        notesViewModel.notesMutableLiveData.observe(getViewLifecycleOwner(), arrayListViewState -> {
            if (arrayListViewState instanceof ViewState.Loading) {
                Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
            } else if (arrayListViewState instanceof ViewState.Success) {
                ArrayList<FirebaseNoteModel> notes = ((ViewState.Success<ArrayList<FirebaseNoteModel>>)
                        arrayListViewState).getData();
                Log.e(TAG, "onNoteReceived: " + notes);
                notesAdapter = new Adapter(notes, new MyViewHolder.OnNoteListener() {
                    @Override
                    public void onNoteClick(int position, View viewHolder) {
                        String title = notesAdapter.getItem(position).getTitle();
                        String description = notesAdapter.getItem(position).getDescription();
                        String docID = notesAdapter.getItem(position).getId();
                        EditNotes notes = new EditNotes();
                        Bundle bundle = new Bundle();
                        bundle.putString("Title", title);
                        bundle.putString("Description", description);
                        bundle.putString("DocID", docID);
                        notes.setArguments(bundle);
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction().
                                add(R.id.fragment_container, notes).commit();
                    }
                });
                recyclerView.setAdapter(notesAdapter);
                notesAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Something went Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpOnClickListeners();
    }

    public RecyclerView.LayoutManager decideLayoutManager(boolean isLinear) {
        if (isLinear) {
            layoutManager = new
                    LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL,false);

        } else {
            layoutManager = new StaggeredGridLayoutManager(2,
                                StaggeredGridLayoutManager.VERTICAL);
        }
        return layoutManager;
    }

    private void setUpOnClickListeners() {
        FloatingActionButton onClickingAddNoteButton = Objects.
                requireNonNull(getView()).findViewById(R.id.addNotesFloatingButton);
        onClickingAddNoteButton.setOnClickListener(v -> {
            Fragment fragment = new AddingNotesFragment();
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).
                    getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }
}