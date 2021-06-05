package com.example.loginapp.data_manager;

import android.util.Log;

import com.example.loginapp.data_manager.model.FirebaseUserModel;
import com.example.loginapp.util.CallBack;
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
                .addOnSuccessListener(documentSnapshots -> {
                    String userEmail = (String) documentSnapshots.getString("Email");
                    String userName = (String) documentSnapshots.getString("Name");
                    Log.e(TAG, "getUserDetails: " + userEmail + userName);

                    FirebaseUserModel firebaseUserModel = new FirebaseUserModel(userEmail, userName);
                    listener.onSuccess(firebaseUserModel);
                }).addOnFailureListener(listener::onFailure);
    }
}