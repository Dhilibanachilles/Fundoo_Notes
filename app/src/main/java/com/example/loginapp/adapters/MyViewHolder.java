//package com.example.loginapp.adapters;
//
//import android.view.View;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.cardview.widget.CardView;
//import androidx.recyclerview.widget.RecyclerView.ViewHolder;
//
//import com.example.loginapp.R;
//import com.google.firebase.auth.FirebaseAuth;
//
//public class MyViewHolder extends ViewHolder implements  View.OnClickListener  {
//    TextView noteTitle, noteDescription;
//    View view;
//    CardView mCardView;
//    FirebaseAuth firebaseAuthenticator;
//    OnNoteListener onNoteListener;
//
//    public MyViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
//        super(itemView);
//        noteTitle = itemView.findViewById(R.id.note_title);
//        noteDescription = itemView.findViewById(R.id.note_description);
//        mCardView = itemView.findViewById(R.id.note_card);
//        view = itemView;
//        firebaseAuthenticator = FirebaseAuth.getInstance();
//        this.onNoteListener = onNoteListener;
//        itemView.setOnClickListener(this);
//    }
//
//    @Override
//    public void onClick(View v) {
//        onNoteListener.onNoteClick(getBindingAdapterPosition(), v);
//    }
//}
