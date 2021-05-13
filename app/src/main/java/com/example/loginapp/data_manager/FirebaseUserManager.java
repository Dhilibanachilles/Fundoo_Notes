package com.example.loginapp.data_manager;

import android.util.Log;

import com.example.loginapp.data_manager.model.FirebaseUserModel;
import com.example.loginapp.util.CallBack;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseUserManager {

    private static final String TAG = "FirebaseUserManager";
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

    public void getUserDetails(CallBack<FirebaseUserModel> listener){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        Task<DocumentSnapshot> documentSnapshotTask = firebaseFirestore.collection("Users")
                .document(firebaseUser.getUid()).get()
                .addOnSuccessListener((OnSuccessListener<DocumentSnapshot>) documentSnapshots -> {
                    String userEmail = (String) documentSnapshots.getString("Email");
                    Log.e(TAG, "getUserDetails: " + userEmail);

                    FirebaseUserModel firebaseUserModel = new FirebaseUserModel(userEmail);
                    listener.onSuccess(firebaseUserModel);
                })
                .addOnFailureListener((OnFailureListener) listener::onFailure);
    }
}