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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.loginapp.R;
import com.example.loginapp.adapters.Adapter;
import com.example.loginapp.adapters.PaginationListener;
import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.fragments.AddingNotesFragment;
import com.example.loginapp.util.CallBack;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.loginapp.adapters.PaginationListener.LIMIT;

public class NotesFragment extends Fragment {
    RecyclerView recyclerView;
    FirebaseNoteManager firebaseNoteManager;
    private static final String TAG = "FragmentNotes";
    private Adapter notesAdapter;
    private final ArrayList<FirebaseNoteModel> firebaseNoteModels = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int itemCount = 0;
    private static int TOTAL_NOTES_COUNT = 0;
    private static int CURRENT_NOTES_COUNT = 0;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    public EditNotesFragment editNotes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        final StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.VERTICAL);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                recyclerView.post(() -> notesAdapter.addLoading());

                fetchNotes(notesAdapter.getItem(notesAdapter.
                        getItemCount() - 1).getCreationTime());
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        firebaseNoteManager = new FirebaseNoteManager();

        NotesViewModel notesViewModel = new ViewModelProvider(this).get(NotesViewModel.class);

        fetchNotes(0);
        deleteNote();


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
                        } catch(IndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<FirebaseNoteModel> notes = new ArrayList<>();
        notesAdapter = new Adapter(notes, (position, viewHolder) -> {

            String title = notesAdapter.getItem(position).getTitle();
            String description = notesAdapter.getItem(position).getDescription();
            String docID = notesAdapter.getItem(position).getId();

            editNotes = new EditNotesFragment();
            Bundle args1 = new Bundle();
            args1.putString("Title", title);
            args1.putString("Description", description);
            args1.putString("docID", docID);
            editNotes.setArguments(args1);
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().add(R.id.fragment_container, editNotes).commit();
        });
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
            assert getFragmentManager() != null;
            getFragmentManager().popBackStack();
            fragmentTransaction.commit();
        });
    }

    public void searchText(String newText) {
        notesAdapter.getFilter().filter(newText);
    }

    private void fetchNotes(long timestamp) {
        fetchAllNotesSize(new CallBack<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                TOTAL_NOTES_COUNT = data;
                Log.e(TAG, "onSuccess: total notes count " +  data );
                ArrayList<FirebaseNoteModel> noteslist = new ArrayList<FirebaseNoteModel>();
                firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
                firebaseFirestore= FirebaseFirestore.getInstance();

                Query query = firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                        .collection("User Notes")
                        .orderBy("Creation Date", Query.Direction.DESCENDING);
                if(timestamp != 0){
                    query = query.startAfter(timestamp);
                }

                query.limit(LIMIT)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            int i;
                            for (i=0;i<queryDocumentSnapshots.size();i++) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);

                                String title = documentSnapshot.getString("Title");
                                String description = documentSnapshot.getString("Description");
                                String docID = documentSnapshot.getId();
                                long timestamp = documentSnapshot.getLong("Creation Date");

                                FirebaseNoteModel note = new FirebaseNoteModel(title, description, docID);
                                note.setCreationTime(timestamp);
                                noteslist.add(note);
                            }

                            if (CURRENT_NOTES_COUNT != 0) {
                                notesAdapter.removeLoading();
                                isLoading = false;
                            }

                            CURRENT_NOTES_COUNT += queryDocumentSnapshots.size() ;
                            notesAdapter.addItems(noteslist);

                            if (CURRENT_NOTES_COUNT < TOTAL_NOTES_COUNT ) {
                                Log.e(TAG, "onSuccess: Current & Total "+ CURRENT_NOTES_COUNT + " : " + TOTAL_NOTES_COUNT );
                            } else {
                                Log.e(TAG, "onSuccess: is last page true " + CURRENT_NOTES_COUNT + " : " + TOTAL_NOTES_COUNT );

                                isLastPage = true;
                            }
                        });

                recyclerView.setAdapter(notesAdapter);
                notesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {
            }
        });
    }

    private void fetchAllNotesSize(CallBack<Integer> countCallBack){
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                .collection("User Notes")
                .get().addOnSuccessListener(queryDocumentSnapshots -> countCallBack.
                onSuccess(queryDocumentSnapshots.size())).
                addOnFailureListener(countCallBack::onFailure);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUpOnClickListeners();
    }

//    public void setLayoutManager(boolean isLinear) {
//        if (isLinear) {
//            layoutManager = new
//                    LinearLayoutManager(getContext(),
//                    LinearLayoutManager.VERTICAL,false);
//        } else {
//            layoutManager = new StaggeredGridLayoutManager(2,
//                                StaggeredGridLayoutManager.VERTICAL);
//        }
//        recyclerView.setLayoutManager(layoutManager);
//    }

    private void deleteNote() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
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
                }catch(IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void addNote(FirebaseNoteModel notes) {
        notesAdapter.addNote(notes);
    }
}