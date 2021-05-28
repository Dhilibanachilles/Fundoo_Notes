package com.example.loginapp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.R;
import com.example.loginapp.data_manager.model.FirebaseLabelModel;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;

import java.util.ArrayList;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.LabelViewHolder> {
    private final ArrayList<FirebaseLabelModel> labelList;
    private final onLabelListener onLabelListener;
    private ArrayList<FirebaseNoteModel> notesSource;
    private static final String TAG = "LabelAdapter";

    public LabelAdapter(ArrayList<FirebaseLabelModel> labelList , onLabelListener onLabelListener){
        this.labelList = labelList;
        this.onLabelListener = onLabelListener;
    }


    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_view,parent,false);
        Log.e(TAG, "onCreateViewHolder: " );
        return new LabelViewHolder(view, onLabelListener);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, final int position) {
        FirebaseLabelModel label = labelList.get(position);
        holder.labelView.setText(label.getLabel());
        Log.e(TAG, "onBindViewHolder: "+ position );
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public FirebaseLabelModel getItem(int position) {
        return labelList.get(position);
    }

    public void removeNote(int position) {
        labelList.remove(position);
        notifyItemRemoved(position);
    }

    public void addLabel(FirebaseLabelModel label) {
        labelList.add(0,label);
        notifyItemInserted(0);
    }


    public static class LabelViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView labelView;
        onLabelListener onLabellistener;

        public LabelViewHolder(View itemview, onLabelListener onLabellistener) {
            super(itemview);
            labelView = itemview.findViewById(R.id.label_item);
            this.onLabellistener = onLabellistener;
            itemview.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onLabellistener.OnLabelClick(getBindingAdapterPosition(), v);
        }
    }

    public interface onLabelListener {
        void OnLabelClick(int position,View viewHolder);
    }

    public void addNote(FirebaseLabelModel label) {
        labelList.add(0,label);
        notifyItemInserted(0);
    }
}
