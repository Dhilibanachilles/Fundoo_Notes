package com.example.loginapp.data_manager;

import android.util.Log;
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

public class FirebaseNoteManager {

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
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
                        Log.e(TAG, "onSuccess: "+ documentSnapshot);
                        String title = documentSnapshot.getString("Title");
                        String description = documentSnapshot.getString("Description");
                        String docID = documentSnapshot.getId();
                        FirebaseNoteModel firebaseNoteModel = new FirebaseNoteModel(title, description, docID);
                        noteslist.add(firebaseNoteModel);
                    }
                    listener.onSuccess(noteslist);
                })
                .addOnFailureListener(listener::onFailure);
    }

    public void addNote(String title, String description, CallBack<Boolean> addListener) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        assert firebaseUser != null;
        DocumentReference documentReference = firebaseFirestore
                .collection("Users")
                .document(firebaseUser.getUid())
                .collection("User Notes").document();
        Map<String, Object> note = new HashMap<>();
        note.put("Title", title);
        note.put("Description", description);
        documentReference.set(note).addOnSuccessListener(aVoid -> addListener.onSuccess(true))
                .addOnFailureListener(addListener::onFailure);
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
}