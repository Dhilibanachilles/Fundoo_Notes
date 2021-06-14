package com.example.loginapp.data_manager.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.NoteManager;
import com.example.loginapp.util.CallBack;
import com.example.loginapp.util.ViewState;

import java.util.ArrayList;

public class LabelViewModel extends ViewModel {
    public MutableLiveData<ViewState<ArrayList<FirebaseLabelModel>>> labelMutableLiveData =
            new MutableLiveData<>();
    private static final String TAG = "LabelViewModel";
    private final NoteManager firebaseNoteManager;

    public LabelViewModel() {
        firebaseNoteManager = (NoteManager) new FirebaseNoteManager();
        loadLabel();
    }

    private void loadLabel() {
        labelMutableLiveData.setValue(new ViewState.Loading<>());
        firebaseNoteManager.getAllLabels(new CallBack<ArrayList<FirebaseLabelModel>>() {
            @Override
            public void onSuccess(ArrayList<FirebaseLabelModel> data) {
                labelMutableLiveData.setValue(new ViewState.Success<>(data));
            }

            @Override
            public void onFailure(Exception exception) {
                labelMutableLiveData.setValue(new ViewState.Failure<>(exception));

            }
        });
    }
}
