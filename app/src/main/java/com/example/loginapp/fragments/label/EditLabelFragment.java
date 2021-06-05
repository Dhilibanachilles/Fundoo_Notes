package com.example.loginapp.fragments.label;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

public class EditLabelFragment extends Fragment {
        private static final String TAG = "edit label";
        EditText editLabelTitle;
        FloatingActionButton saveEditedLabel;
        FirebaseFirestore firebaseFirestore;
        FirebaseUser firebaseUser;

        @Override
        public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.edit_label, container, false);

                String label = getArguments().getString("label");
                String docID = getArguments().getString("docID");
                Log.e(TAG, "onCreate: " + label);

                editLabelTitle = view.findViewById(R.id.editLabelFragment);
                saveEditedLabel =  view.findViewById(R.id.editLabelButton);
                editLabelTitle.setText(label);

                firebaseFirestore=FirebaseFirestore.getInstance();
                firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

                saveEditedLabel.setOnClickListener(v -> {
                        String newLabel= editLabelTitle.getText().toString();
                        if(newLabel.isEmpty()) {
                             Toast.makeText(getContext(),"Something is empty",Toast.LENGTH_SHORT).show();
                        } else {
                             firebaseFirestore=FirebaseFirestore.getInstance();
                             DocumentReference documentReference = firebaseFirestore
                                     .collection("Users")
                                     .document(firebaseUser.getUid())
                                     .collection("Labels").document(docID);
                             Map<String,Object> note=new HashMap<>();
                             note.put("Label",newLabel);
                             documentReference.set(note).addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Label updated",Toast.LENGTH_SHORT).
                                                show();
                                     InputMethodManager keyBoard = (InputMethodManager) Objects.requireNonNull(getActivity())
                                             .getSystemService(Context.INPUT_METHOD_SERVICE);
                                     keyBoard.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                    Fragment fragment = new LabelFragment();
                                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).
                                                getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }).addOnFailureListener(e -> Toast.makeText(getContext(),"Failed To update",
                                        Toast.LENGTH_SHORT).show());
                        }
                });
                return view;
        }
}
