package com.example.loginapp.fragments.notes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.loginapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditNotes extends Fragment {

    EditText editTitleInNote, editDescriptionInNote;
    FloatingActionButton editNoteSaveButton;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ProgressBar viewEditProgressBar;
    private static final String TAG = "EditNotes";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_note, container, false);
        assert getArguments() != null;
        String title = getArguments().getString("Title");
        String description = getArguments().getString("Description");
        String docID = getArguments().getString("DocID");
        Log.e(TAG, "onCreate: " + title);
        Log.e(TAG, "onCreate: " + description);

        editTitleInNote = view.findViewById(R.id.edit_note_title);
        editDescriptionInNote = view.findViewById(R.id.edit_note_description);
        editNoteSaveButton =  view.findViewById(R.id.update_button);
        viewEditProgressBar = view.findViewById(R.id.edit_note_progressbar);
        editDescriptionInNote.setText(description);
        editTitleInNote.setText(title);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        editNoteSaveButton.setOnClickListener(v -> {
            String newTitle= editTitleInNote.getText().toString();
            String newDescription= editDescriptionInNote.getText().toString();

            if(newTitle.isEmpty()||newDescription.isEmpty()) {
                Toast.makeText(getContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
            }
            else {
                firebaseFirestore=FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore
                        .collection("Users")
                        .document(firebaseUser.getUid())
                        .collection("User Notes").document(docID);
                Map<String,Object> note=new HashMap<>();
                note.put("Title", newTitle);
                note.put("Description", newDescription);
                viewEditProgressBar.setVisibility(View.VISIBLE);
                documentReference.set(note).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),"Note Updated",Toast.LENGTH_SHORT).show();
                    Fragment fragment = new FragmentNotes();
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStackImmediate();
                    fragmentTransaction.commit();

                }).
                        addOnFailureListener(e -> Toast.makeText(getContext(),
                                "Note Updation Failed",Toast.LENGTH_SHORT).show());
            }
        });
        return view;
    }
}