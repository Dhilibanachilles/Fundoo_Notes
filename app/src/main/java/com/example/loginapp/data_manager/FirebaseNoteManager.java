package com.example.loginapp.data_manager;

import android.util.Log;

import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.util.CallBack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseNoteManager {

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private static final String TAG = "FirebaseNoteManager";

    public void  getAllNotes(CallBack listener) {
        ArrayList<FirebaseNoteModel> noteslist = new ArrayList<FirebaseNoteModel>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                .collection("User Notes").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    int i;
                    for (i=0;i<queryDocumentSnapshots.size();i++){
                        Log.e(TAG, "onSuccess: "+queryDocumentSnapshots.getDocuments().get(i));
                        String title = queryDocumentSnapshots.getDocuments().get(i).getString("Title");
                        String description = queryDocumentSnapshots.getDocuments().get(i).getString("Description");
                        FirebaseNoteModel firebaseNoteModel = new FirebaseNoteModel(title, description);
                        noteslist.add(firebaseNoteModel);
                    }
                    listener.onSuccess(noteslist);
                })
                .addOnFailureListener(e -> listener.onFailure(e));
    }

    public void addNote(String title, String description, CallBack<Boolean> addListener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DocumentReference documentReference = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Users Notes").document();
        Map<String, Object> note = new HashMap<>();
        note.put("Title", title);
        note.put("Description", description);
        note.put("Creation Date", System.currentTimeMillis());

        documentReference.set(note)
                .addOnSuccessListener(aVoid -> addListener.onSuccess(true))
                .addOnFailureListener(addListener::onFailure
                );
    }
}