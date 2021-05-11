package com.example.loginapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.loginapp.dashboard.HomeActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class note_details extends Fragment {


    private TextView mtitleofnotedetail,mcontentofnotedetail;
    FloatingActionButton mgotoeditnote;
    HomeActivity homeActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //  View view = inflater.inflate(R.layout.fragment_notes, container, false);
        return inflater.inflate(R.layout.note_details, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mtitleofnotedetail= mtitleofnotedetail.findViewById(R.id.titleofnotedetail);
        mcontentofnotedetail = mtitleofnotedetail.findViewById(R.id.contentofnotedetail);
        mgotoeditnote= mgotoeditnote.findViewById(R.id.gotoeditnote);
        Toolbar toolbar = (Toolbar)getActivity().findViewById(R.id.toolbarofnotedetail);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data=getActivity().getIntent();

        mgotoeditnote.setOnClickListener(v -> {
            Fragment fragment = new editnote();
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, fragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        mcontentofnotedetail.setText(data.getStringExtra("content"));
        mtitleofnotedetail.setText(data.getStringExtra("title"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            homeActivity.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}