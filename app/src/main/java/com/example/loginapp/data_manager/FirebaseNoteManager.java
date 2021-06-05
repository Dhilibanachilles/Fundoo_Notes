package com.example.loginapp.data_manager;

import android.util.Log;
import android.widget.Adapter;

import com.example.loginapp.data_manager.model.FirebaseLabelModel;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.util.CallBack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirebaseNoteManager implements NoteManager {

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    Adapter adapter;
    String newNoteID;
    String newLabelID;
    private static final String TAG = "FirebaseNoteManager";

    public void  getAllNotes(CallBack<ArrayList<FirebaseNoteModel>> listener) {
        ArrayList<FirebaseNoteModel> noteslist = new ArrayList<FirebaseNoteModel>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                .collection("User Notes").get().addOnSuccessListener(queryDocumentSnapshots -> {
                    int i;
                    for (i=0;i<queryDocumentSnapshots.size();i++){
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                        Log.e(TAG, "onSuccess: " + documentSnapshot);
                        String title = documentSnapshot.getString("Title");
                        String description = documentSnapshot.getString("Description");
                        String docID = documentSnapshot.getId();
                        long timestamp = documentSnapshot.getLong("Creation Date");
                        FirebaseNoteModel firebaseNoteModel = new FirebaseNoteModel(title, description, docID);
                        firebaseNoteModel.setCreationTime(timestamp);
                        noteslist.add(firebaseNoteModel);
                    }
                    listener.onSuccess(noteslist);
                })
                .addOnFailureListener(listener::onFailure);
    }

    @Override
    public void addNote(String title, String description, CallBack<String> addListener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        assert firebaseUser != null;
        DocumentReference documentReference = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("User Notes").document();
        Map<String, Object> note = new HashMap<>();
        note.put("Title", title);
        note.put("Description", description);
        note.put("Creation Date", System.currentTimeMillis());
        documentReference.set(note)
                .addOnSuccessListener(aVoid -> {
                    newNoteID = documentReference.getId();
                    addListener.onSuccess(newNoteID);
                    Log.e(TAG, "newNoteID "+ newNoteID );
                })
                .addOnFailureListener(addListener::onFailure
                );
        Log.e(TAG, "addNote: " + newNoteID );
    }

    public void  getAllLabels(CallBack listener) {
        ArrayList<FirebaseLabelModel> noteslist = new ArrayList<FirebaseLabelModel>();
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Users").document(firebaseUser.getUid())
                .collection("Labels").get().addOnSuccessListener(queryDocumentSnapshots -> {
            int i;
            for (i=0;i<queryDocumentSnapshots.size();i++){
                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(i);
                Log.e(TAG, "onSuccess: "+ documentSnapshot);
                String label = documentSnapshot.getString("Label");
                String docID = documentSnapshot.getId();
                FirebaseLabelModel firebaseLabelModel = new FirebaseLabelModel(label, docID);
                noteslist.add(firebaseLabelModel);
            }
            listener.onSuccess(noteslist);
        })
                .addOnFailureListener(listener::onFailure);
    }

    public void addLabel(String label, CallBack<String> listener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        assert firebaseUser != null;
        DocumentReference documentReference = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Labels").document();
        Map<String, Object> note = new HashMap<>();
        note.put("Label", label);
        note.put("Creation Date", System.currentTimeMillis());

        documentReference.set(note)
                .addOnSuccessListener(aVoid -> {
                    newLabelID = documentReference.getId();
                    listener.onSuccess(newLabelID);
                    Log.e(TAG, "newNoteID "+ newNoteID );
                }).addOnFailureListener(listener::onFailure);
        Log.e(TAG, "addNote: " + newNoteID );
    }

    public void deleteNote(String docID) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        assert firebaseUser != null;
        DocumentReference documentReference = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("User Notes").document(docID);
        documentReference.delete().addOnSuccessListener(aVoid -> Log.e(TAG, "onSuccess: Deleted "+ docID ))
                .addOnFailureListener(e -> Log.e(TAG, "onFailure:Error Deleted "+ docID ));
    }

    public void deleteLabel(String labelID){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();
        assert firebaseUser != null;
        DocumentReference documentReference = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("Labels").document(labelID);
        documentReference.delete().addOnSuccessListener(aVoid ->
                Log.e(TAG, "onSuccess: Deleted "+ labelID )).
                addOnFailureListener(e -> Log.e(TAG, "onFailure:Error Deleted "+ labelID ));
    }
}