package com.example.loginapp.fragments.notes;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.loginapp.data_manager.FirebaseNoteManager;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;
import com.example.loginapp.util.CallBack;
import com.example.loginapp.util.ViewState;

import java.util.ArrayList;

public class NotesViewModel extends ViewModel {
    MutableLiveData<ViewState<ArrayList<FirebaseNoteModel>>> notesMutableLiveData = new MutableLiveData<>();
    private static final String TAG = "NotesViewModel";
    private final FirebaseNoteManager firebaseNoteManager;

    public NotesViewModel() {
        firebaseNoteManager = new FirebaseNoteManager();
        loadNotes();
    }

    private void loadNotes() {
        notesMutableLiveData.setValue(new ViewState.Loading<>());
        firebaseNoteManager.getAllNotes(new CallBack<ArrayList<FirebaseNoteModel>>() {
            @Override
            public void onSuccess(ArrayList<FirebaseNoteModel> data) {
                Log.e(TAG, "onNoteReceived: " + data);
                notesMutableLiveData.setValue(new ViewState.Success<>(data));
            }

            @Override
            public void onFailure(Exception exception) {
                notesMutableLiveData.setValue(new ViewState.Failure<>(exception));
            }
        });
    }
}
