package com.example.loginapp.adapters;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loginapp.BaseViewHolder;
import com.example.loginapp.R;
import com.example.loginapp.data_manager.model.FirebaseNoteModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Adapter extends RecyclerView.Adapter<BaseViewHolder> implements Filterable {
    private static final String TAG = "NoteAdapter";
    private final ArrayList<FirebaseNoteModel> notesList;
    private final OnNoteListener onNoteListener;
    private final List<FirebaseNoteModel> notesSearch;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;
    private boolean isLoaderVisible = false;

    public Adapter(ArrayList<FirebaseNoteModel> notesList, OnNoteListener onNoteListener) {
        this.notesList = notesList;
        this.onNoteListener = onNoteListener;
        notesSearch = notesList;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.view_note_layout, parent, false), (OnNoteListener) onNoteListener);
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, final int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == notesList.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getItemCount() {
        Log.e(TAG, "Get Item Count: " + notesList.size());
        return notesList.size();
    }

    public FirebaseNoteModel getItem(int position) {
        try{
            return notesList.get(position);
        }catch (ArrayIndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return null;
    }

    public void removeNote(int position) {
        notesList.remove(position);
        notifyItemRemoved(position);
    }

    public void addItems(ArrayList<FirebaseNoteModel> postItems) {
        notesList.addAll(postItems);
        notifyDataSetChanged();
    }

    public void addLoading() {
        isLoaderVisible = true;
        notesList.add(new FirebaseNoteModel());
        notifyItemInserted(notesList.size() - 1);
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = notesList.size() - 1;

        FirebaseNoteModel item = getItem(position);
        if (item != null) {
            notesList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public Filter getFilter() {
        return notesFilter;
    }

    private final Filter notesFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<FirebaseNoteModel> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0){
                Log.e(TAG, "performFiltering: " + constraint + " " + notesSearch.size());
                filteredList.addAll(notesSearch);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(FirebaseNoteModel note : notesSearch) {
                    if(note.getTitle().toLowerCase().contains(filterPattern)
                            || note.getDescription().toLowerCase().contains(filterPattern)) {
                        filteredList.add(note);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            notesList.clear();
            notesList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public  class ViewHolder extends BaseViewHolder implements View.OnClickListener {
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.note_title)
        TextView textViewTitle;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.note_description)
        TextView textViewDescription;
        @SuppressLint("NonConstantResourceId")
        @BindView(R.id.note_card)
        CardView cardView;
        OnNoteListener onNoteListener;
        ViewHolder(View itemView,OnNoteListener onNoteListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        protected void clear() {
        }

        public void onBind(int position) {
            super.onBind(position);
            FirebaseNoteModel item = notesList.get(position);
            textViewTitle.setText(item.getTitle());
            textViewDescription.setText(item.getDescription());
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getBindingAdapterPosition(),v);
        }
    }

    public static class ProgressHolder extends BaseViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void clear() {
        }
    }

    public void addNote(FirebaseNoteModel note) {
        notesList.add(0, note);
        notifyItemInserted(0);
    }
}