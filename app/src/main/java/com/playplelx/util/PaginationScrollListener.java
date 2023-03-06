package com.playplelx.util;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {

    LinearLayoutManager layoutManager;

    public PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

        if (dy > 0) {

            final int visibleThreshold = 2;

           /* int visibleItemCount = recyclerView.getLayoutManager().getChildCount();
            int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            int firstVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
            Log.e("firstVisibleItemPosition", "=" + firstVisibleItemPosition);
            if (!isLoading() && !isLastPage()) {
                if ((visibleItemCount + firstVisibleItemPosition) >=
                        totalItemCount) {
                    loadMoreItems();
                }
            }*/

            int lastItem  = layoutManager.findLastCompletelyVisibleItemPosition();
            int currentTotalCount = layoutManager.getItemCount();
            if(currentTotalCount <= lastItem + visibleThreshold){
                loadMoreItems();
                //show your loading view
                // load content in background

            }
        }

    }

    protected abstract void loadMoreItems();

    public abstract boolean isLastPage();

    public abstract boolean isLoading();
}

