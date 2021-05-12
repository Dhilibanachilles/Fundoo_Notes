package com.example.loginapp;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FirebaseNoteManager {

    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    private static final String TAG = "FirebaseNoteManager";

    public void  getAllNotes(NoteListener listener) {
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
                    listener.onNoteReceived(noteslist);
                });
    }
}