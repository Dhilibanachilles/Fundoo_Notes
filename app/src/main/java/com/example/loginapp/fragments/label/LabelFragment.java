package com.example.loginapp.fragments.label;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.R;
import com.example.loginapp.adapters.LabelAdapter;
import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.model.FirebaseLabelModel;
import com.example.loginapp.data_manager.model.LabelViewModel;
import com.example.loginapp.fragments.notes.NotesFragment;
import com.example.loginapp.sqlitedatabase.DatabaseHelper;
import com.example.loginapp.util.CallBack;
import com.example.loginapp.util.ViewState;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

public class LabelFragment extends Fragment {

    private EditText mCreateLabel;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    FirebaseNoteManager fireBaseNoteManager;
    private static final String TAG = "LabelFragment";
    DatabaseHelper mDatabaseHelper;
    String docID;
    RecyclerView recyclerView;
    LabelViewModel labelViewModel;
    private LabelAdapter labelAdapter;
    AddLabelListener addLabelListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        addLabelListener = (AddLabelListener) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.label_fragment, container, false);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView = view.findViewById(R.id.recyclerviewLabel);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fireBaseNoteManager = new FirebaseNoteManager();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.
                SimpleCallback(0, ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getBindingAdapterPosition();
                try {
                    String labelId = labelAdapter.getItem(position).getLabelId();
                    labelAdapter.removeNote(position);
                    fireBaseNoteManager.deleteLabel(labelId);
                    Toast.makeText(getContext(), "Label Deleted", Toast.LENGTH_SHORT).show();
                }catch(IndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        labelViewModel = new ViewModelProvider(this).get(LabelViewModel.class);
        ImageButton mSaveLabel = view.findViewById(R.id.saveLabel);
        ImageView backButton = view.findViewById(R.id.backButton);
        mCreateLabel = view .findViewById(R.id.createLabel);
        mSaveLabel.setOnClickListener(this::onClick);

        backButton.setOnClickListener(v -> {
            Fragment fragment = new NotesFragment();
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).
                    getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        return view;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        labelViewModel.labelMutableLiveData.observe(getViewLifecycleOwner(),
                arrayListViewState -> {
            if (arrayListViewState instanceof ViewState.Loading) {
                Toast.makeText(getContext(), "Loading", Toast.LENGTH_SHORT).show();
            } else if (arrayListViewState instanceof ViewState.Success) {
                ArrayList<FirebaseLabelModel> labels = ((ViewState.
                        Success<ArrayList<FirebaseLabelModel>>)arrayListViewState).
                        getData();
                labelAdapter = new LabelAdapter(labels, (position, viewHolder) -> {
                    String labelId = labelAdapter.getItem(position).getLabel();
                    String docID = labelAdapter.getItem(position).getLabelId();
                    EditLabelFragment editLabelFragment = new EditLabelFragment();
                    Bundle args1 = new Bundle();

                    args1.putString("label", labelId);
                    args1.putString("docID", docID);
                    editLabelFragment.setArguments(args1);
                    editLabelFragment.setArguments(args1);
                    assert getFragmentManager() != null;
                    getFragmentManager().beginTransaction().replace(R.id.
                            fragment_container, editLabelFragment).commit();
                });
                        recyclerView.setAdapter(labelAdapter);
                labelAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(getContext(), "Something went Wrong",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClick(View v) {
        String label = mCreateLabel.getText().toString();
        if (label.isEmpty()) {
            Toast.makeText(getContext(), "Both fields are Required", Toast.LENGTH_SHORT).show();
        } else {
            String currentUID = firebaseUser.getUid();
            DocumentReference exist = firebaseFirestore.collection("Users").
                    document(firebaseUser.getUid());
            if (currentUID.equals(exist.toString())) {
                FirebaseNoteManager firebaseNoteManager = new FirebaseNoteManager();
                firebaseNoteManager.addLabel(label, new CallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(getContext(),
                                "Label Created", Toast.LENGTH_SHORT).show();
                        mCreateLabel.setText(null);
                        InputMethodManager imm = (InputMethodManager) Objects.requireNonNull(getActivity())
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getContext(),
                                "Failed To Create Label", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                FirebaseNoteManager firebaseNoteManager = new FirebaseNoteManager();
                firebaseNoteManager.addLabel(label, new CallBack<String>() {
                    @Override
                    public void onSuccess(String data) {
                        Toast.makeText(getContext(),
                                "Label Created", Toast.LENGTH_SHORT).show();
                        mCreateLabel.setText(null);
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
                        FirebaseLabelModel firebaseLabelModel = new FirebaseLabelModel(label, data);
                        addLabelListener.onLabelAdded(firebaseLabelModel);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getContext(),
                                "Failed To Create Label", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    public void addLabel(FirebaseLabelModel label) {
        labelAdapter.addLabel(label);
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