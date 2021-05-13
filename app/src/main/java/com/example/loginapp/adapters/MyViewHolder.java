package com.example.loginapp.adapters;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.example.loginapp.R;

public class MyViewHolder extends ViewHolder {
    TextView noteTitle, noteContent;
    View view;
    CardView mCardView;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        noteTitle = itemView.findViewById(R.id.note_title);
        noteContent = itemView.findViewById(R.id.note_content);
        mCardView = itemView.findViewById(R.id.note_card);
        view = itemView;
    }
}
