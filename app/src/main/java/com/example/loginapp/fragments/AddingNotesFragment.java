package com.example.loginapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.loginapp.R;
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

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addnotes, container, false);
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FloatingActionButton saveNoteButton = Objects.requireNonNull(getView()).findViewById(R.id.saveNote);
        createNoteDescription = getView().findViewById(R.id.noteDescription);
        createNoteTitle = getView().findViewById(R.id.noteTitle);
        createNoteProgressBar = getView().findViewById(R.id.saveNoteProgressBar);
        firebaseAuthenticator = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        saveNoteButton.setOnClickListener(this::onClick);
    }

    private void onClick(View v) {
        String title = createNoteTitle.getText().toString();
        String description = createNoteDescription.getText().toString();
        String email = firebaseUser.getEmail();
        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Atleast one field must be filled", Toast.LENGTH_SHORT).show();
        } else {
            String currentUID = firebaseUser.getUid();
            DocumentReference exist = firebaseFirestore.collection("Users").document(firebaseUser.getUid());
            if (currentUID.equals(exist.toString())) {
                createNoteProgressBar.setVisibility(View.VISIBLE);
                DocumentReference documentReference = firebaseFirestore.collection("Users")
                        .document(firebaseUser.getUid()).collection("User Notes").document();
                Map<String, Object> note = new HashMap<>();
                note.put("1.Title", title);
                note.put("2.Description", description);

                createNoteProgressBar.setVisibility(View.VISIBLE);
                documentReference.set(note).addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                        "Note Created and Saved Successfully", Toast.LENGTH_SHORT).show()).
                        addOnFailureListener(e -> Toast.makeText(getContext(), "Note creation failed", Toast.LENGTH_SHORT).show());
                createNoteProgressBar.setVisibility(View.INVISIBLE);
            } else {
                createNoteProgressBar.setVisibility(View.VISIBLE);
                Map<String, Object> noteGettingUserDetails = new HashMap<>();
                noteGettingUserDetails.put("Email", email);
                DocumentReference documentReference;
                documentReference = firebaseFirestore.collection("Users")
                        .document(firebaseUser.getUid()).collection("User Notes").document();
                Map<String, Object> note = new HashMap<>();
                note.put("1.Title", title);
                note.put("2.Description", description);
                firebaseFirestore.collection("Users").document(firebaseUser.getUid()).set(noteGettingUserDetails);
                createNoteProgressBar.setVisibility(View.VISIBLE);
                documentReference.set(note).addOnSuccessListener(aVoid -> Toast.makeText(getContext(),
                        "Note Created and Saved Successfully", Toast.LENGTH_SHORT).show()).
                        addOnFailureListener(e -> Toast.makeText(getContext(), "Note creation failed", Toast.LENGTH_SHORT).show());
                createNoteProgressBar.setVisibility(View.INVISIBLE);
            }
        }
    }
}