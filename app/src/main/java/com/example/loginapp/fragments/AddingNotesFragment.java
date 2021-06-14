package com.example.loginapp.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.loginapp.R;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.fragments.notes.AddNoteListener;
import com.example.loginapp.fragments.notes.NotesFragment;
import com.example.loginapp.work_manager.MyWorker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddingNotesFragment extends Fragment {

    private EditText createNoteTitle, createNoteDescription;
    FirebaseAuth firebaseAuthenticator;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    ProgressBar createNoteProgressBar;
    private static final String TAG = "AddingNotesFragment";
    AddNoteListener addNoteListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addNoteListener = (AddNoteListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addnotes, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton saveNoteButton = Objects.requireNonNull(getView()).findViewById(R.id.update_button);
        createNoteDescription = getView().findViewById(R.id.edit_note_description);
        createNoteTitle = getView().findViewById(R.id.edit_note_title);
        createNoteProgressBar = getView().findViewById(R.id.edit_note_progressbar);
        ImageView backButton = getView().findViewById(R.id.addNoteBackButton);
        firebaseAuthenticator = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        saveNoteButton.setOnClickListener(this::onClick);

        backButton.setOnClickListener(v -> {
            Fragment fragment = new NotesFragment();
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).
                    getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            InputMethodManager keyBoard = (InputMethodManager) getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            keyBoard.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void onClick(View v) {
        String title = createNoteTitle.getText().toString();
        String description = createNoteDescription.getText().toString();
        String email = firebaseUser.getEmail();

        long timeID = System.currentTimeMillis();
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "All fields must be filled", Toast.LENGTH_SHORT).show();
        } else {
            String currentUID = firebaseUser.getUid();
            DocumentReference exist = firebaseFirestore.collection("Users").
                    document(firebaseUser.getUid());
            createNoteProgressBar.setVisibility(View.VISIBLE);

            Map<String, Object> noteGettingUserDetails = new HashMap<>();
            noteGettingUserDetails.put("Email", email);
            DocumentReference documentReference;
            documentReference = firebaseFirestore.collection("Users")
                    .document(firebaseUser.getUid()).collection("User Notes").document();
            FirebaseNoteModel firebaseNoteModel = new FirebaseNoteModel(title, description, documentReference.getId(), false, false, System.currentTimeMillis());
            firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                    .set(noteGettingUserDetails);
            createNoteProgressBar.setVisibility(View.VISIBLE);
            documentReference.set(firebaseNoteModel).addOnSuccessListener(aVoid -> {
                Toast.makeText(getContext(),
                        "Note Created", Toast.LENGTH_SHORT).show();

                final OneTimeWorkRequest.Builder workRequest =
                        new OneTimeWorkRequest.Builder(MyWorker.class);
                Data.Builder data = new Data.Builder();
                data.putString(MyWorker.NOTIFICATION_TITLE, "Title: " + title);
                data.putString(MyWorker.NOTIFICATION_MESSAGE, "Description: " + description);
                workRequest.setInputData(data.build());
                WorkManager.getInstance().enqueue(workRequest.build());

                InputMethodManager imm = (InputMethodManager) Objects.
                        requireNonNull(getActivity()).getSystemService(Context.
                        INPUT_METHOD_SERVICE);
                InputMethodManager keyBoard = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                keyBoard.hideSoftInputFromWindow(v.getWindowToken(), 0);
                Fragment fragment = new NotesFragment();
                FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                assert getFragmentManager() != null;
                getFragmentManager().popBackStack();
                fragmentTransaction.commit();
            }).
                    addOnFailureListener(e -> Toast.makeText(getContext(),
                            "Note creation failed", Toast.LENGTH_SHORT).show());
        }
        createNoteProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(((AppCompatActivity) Objects.
                requireNonNull(getActivity())).getSupportActionBar()).hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(((AppCompatActivity) Objects.
                requireNonNull(getActivity())).getSupportActionBar()).show();
    }
}