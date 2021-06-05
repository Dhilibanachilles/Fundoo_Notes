package com.example.loginapp.fragments.notes;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.loginapp.AlertReceiver;
import com.example.loginapp.DatePickerFragment;
import com.example.loginapp.R;
import com.example.loginapp.TimePickerFragment;
import com.example.loginapp.data_manager.SharedPreferenceHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditNotesFragment extends Fragment {

    EditText editTitleInNote, editDescriptionInNote;
    FloatingActionButton editNoteSaveButton;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    ProgressBar viewEditProgressBar;
    Button timePicker, datePicker;
    SharedPreferenceHelper sharedPreferences;
    private static final String TAG = "EditNotes";
    public Calendar schedule;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_note, container, false);
        assert getArguments() != null;
        String title = getArguments().getString("Title");
        String description = getArguments().getString("Description");
        String docID = getArguments().getString("docID");
        Log.e(TAG, "onCreate: " + title);
        Log.e(TAG, "onCreate: " + description);

        editTitleInNote = view.findViewById(R.id.edit_note_title);
        editDescriptionInNote = view.findViewById(R.id.edit_note_description);
        editNoteSaveButton =  view.findViewById(R.id.update_button);
        viewEditProgressBar = view.findViewById(R.id.edit_note_progressbar);
        timePicker = view.findViewById(R.id.timePicker);
        datePicker = view.findViewById(R.id.datePicker);
        editDescriptionInNote.setText(description);
        editTitleInNote.setText(title);
        ImageView backButton = view.findViewById(R.id.addNoteBackButton);
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        sharedPreferences = new SharedPreferenceHelper(Objects.requireNonNull(getContext()));
        schedule = Calendar.getInstance();

        timePicker.setOnClickListener(v1 -> {
            DialogFragment timePicker =new TimePickerFragment();
            assert getFragmentManager() != null;
            timePicker.show(getFragmentManager(),"time picker");
        });

        datePicker.setOnClickListener(v -> {
            DialogFragment datePicker = new DatePickerFragment();
            assert getFragmentManager() != null;
            datePicker.show(getFragmentManager(), "date picker");
        });

        backButton.setOnClickListener(v -> {
            Fragment fragment = new NotesFragment();
            FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).
                    getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            InputMethodManager keyBoard = (InputMethodManager)getActivity()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            keyBoard.hideSoftInputFromWindow(v.getWindowToken(), 0);
        });

        editNoteSaveButton.setOnClickListener(v -> {
            String newNoteTitle= editTitleInNote.getText().toString();
            String newNoteDescription= editDescriptionInNote.getText().toString();

            if(newNoteTitle.isEmpty()||newNoteDescription.isEmpty()) {
                Toast.makeText(getContext(),"Fields are empty",Toast.LENGTH_SHORT).show();
            } else {
                firebaseFirestore=FirebaseFirestore.getInstance();
                DocumentReference documentReference = firebaseFirestore
                        .collection("Users")
                        .document(firebaseUser.getUid())
                        .collection("User Notes").document(docID);
                Map<String,Object> note=new HashMap<>();
                note.put("Title", newNoteTitle);
                note.put("Description", newNoteDescription);
                note.put("Creation Date", System.currentTimeMillis());

                documentReference.set(note).addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(),"Note updated",Toast.LENGTH_SHORT).show();
                    sharedPreferences.setNoteTitle(newNoteTitle);
                    sharedPreferences.setNoteDescription(newNoteDescription);
                    startAlarm(schedule);
                    Fragment fragment = new NotesFragment();
                    FragmentManager fragmentManager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, fragment);
                    assert getFragmentManager() != null;
                    getFragmentManager().popBackStackImmediate();
                    fragmentTransaction.commit();
                }).addOnFailureListener(e -> Toast.makeText(getContext(),"Failed To update",Toast.LENGTH_SHORT).show());
            }
        });
        return view;
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) Objects.requireNonNull(getActivity()).getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);

        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
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