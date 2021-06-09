package com.example.loginapp.adapters;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class PaginationListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "PaginationListener";
    public static final int LIMIT = 10;

    @NonNull
    private final StaggeredGridLayoutManager layoutManager;
    public PaginationListener(@NonNull StaggeredGridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int positionView = layoutManager.findFirstVisibleItemPositions(null)[0];

        if (!isLoading() && !isLastPage()) {
            if ((visibleItemCount + positionView) >= totalItemCount && positionView >= 0
                    && totalItemCount >= LIMIT) {
                loadMoreItems();
                Log.e(TAG, "total & limit " + totalItemCount + " : " + LIMIT );
            }
        }
    }
    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
