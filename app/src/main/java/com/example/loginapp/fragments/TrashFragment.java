package com.example.loginapp.fragments;

import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.loginapp.R;
import com.example.loginapp.adapters.Adapter;
import com.example.loginapp.adapters.PaginationListener;
import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.fragments.notes.EditNotesFragment;
import com.example.loginapp.util.CallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.example.loginapp.adapters.PaginationListener.LIMIT;

public class TrashFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.fragment_trash, container, false);
        final StaggeredGridLayoutManager layoutManager = new
                StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);

        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.trashRecyclerView);
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
        fetchNotes(0);

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
                        switch (direction) {
                            case ItemTouchHelper.LEFT:
                                try {
                                    FirebaseNoteModel firebaseNoteModel = notesAdapter.getItem(position);
                                    firebaseNoteManager.deleteNote(firebaseNoteModel.getId());
                                    notesAdapter.removeNote(position);
                                    Toast.makeText(getContext(), "Note Deleted Permanently", Toast.LENGTH_SHORT).show();
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                                break;

                            case ItemTouchHelper.RIGHT:
                                try {
                                    FirebaseNoteModel firebaseNoteModel = notesAdapter.getItem(position);
                                    firebaseNoteModel.setDeleted(false);
                                    firebaseNoteManager.updateNote(firebaseNoteModel, new CallBack<Boolean>() {
                                        @Override
                                        public void onSuccess(Boolean data) {
                                            notesAdapter.removeNote(position);
                                            Toast.makeText(getContext(), "Note Restored", Toast.LENGTH_SHORT).show();
                                        }

                                        @Override
                                        public void onFailure(Exception exception) {
                                            Toast.makeText(getContext(), "Something went wrong while archiving",
                                                    Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                } catch (IndexOutOfBoundsException e) {
                                    e.printStackTrace();
                                }
                                break;
                        }
                    }
                    @Override
                    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                            @NonNull RecyclerView.ViewHolder viewHolder,
                                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
                        new RecyclerViewSwipeDecorator.Builder(getContext(), c,
                                recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                                .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.black))
                                .addSwipeLeftActionIcon(R.drawable.ic_delete)
                                .addSwipeRightBackgroundColor(ContextCompat.getColor(getContext(), R.color.design_default_color_error))
                                .addSwipeRightActionIcon(R.drawable.ic_note)
                                .setActionIconTint(ContextCompat.getColor(recyclerView.getContext(), android.R.color.white))
                                .create()
                                .decorate();
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
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
        });
    }

    private void fetchNotes(long timestamp) {
        fetchAllNotesSize(new CallBack<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                TOTAL_NOTES_COUNT = data;
                Log.e(TAG, "onSuccess: total notes count " + data);
                ArrayList<FirebaseNoteModel> noteslist = new ArrayList<FirebaseNoteModel>();
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseFirestore = FirebaseFirestore.getInstance();

                Query query = firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                        .collection("User Notes")
                        .orderBy("creationTime", Query.Direction.DESCENDING);
                if (timestamp != 0) {
                    query = query.startAfter(timestamp);
                }

                query.limit(LIMIT)
                        .whereEqualTo("deleted", true)
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            int i;
                            for (i = 0; i < queryDocumentSnapshots.size(); i++) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                                FirebaseNoteModel note = documentSnapshot.toObject(FirebaseNoteModel.class);
                                noteslist.add(note);
                            }

                            if (CURRENT_NOTES_COUNT != 0) {
                                notesAdapter.removeLoading();
                                isLoading = false;
                            }

                            CURRENT_NOTES_COUNT += queryDocumentSnapshots.size();
                            Log.e(TAG, "onSuccess: note List " + noteslist);
                            notesAdapter.addItems(noteslist);

                            if (CURRENT_NOTES_COUNT < TOTAL_NOTES_COUNT) {
                                Log.e(TAG, "onSuccess: Current & Total " +
                                        CURRENT_NOTES_COUNT + " : " + TOTAL_NOTES_COUNT);
                            } else {
                                Log.e(TAG, "onSuccess: is last page true " +
                                        CURRENT_NOTES_COUNT + " : " + TOTAL_NOTES_COUNT);
                                isLastPage = true;
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
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

    private void fetchAllNotesSize(CallBack<Integer> countCallBack) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                .collection("User Notes")
                .get().addOnSuccessListener(queryDocumentSnapshots -> countCallBack.
                onSuccess(queryDocumentSnapshots.size())).
                addOnFailureListener(countCallBack::onFailure);
    }
}