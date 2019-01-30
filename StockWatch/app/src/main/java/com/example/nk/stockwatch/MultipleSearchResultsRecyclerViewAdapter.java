package com.example.nk.stockwatch;


import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MultipleSearchResultsRecyclerViewAdapter extends RecyclerView.Adapter<MultipleSearchResultsRecyclerViewAdapter.ViewHolder> {
    private HashMap<String, String> mStockHashMap;
    private List<String> mSearchResults;
    private MainActivity mMainActivity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout mConstraintLayout;
        public TextView mSymbolNameTextView;

        public ViewHolder(ConstraintLayout v) {
            super(v);
            mConstraintLayout = v;
            mSymbolNameTextView = (TextView) v.findViewById(R.id.symbolNameTextView);
        }
    }

    public MultipleSearchResultsRecyclerViewAdapter(MainActivity mainActivity, ArrayList searchResults, HashMap stockHashMap) {
        mSearchResults = searchResults;
        mStockHashMap = stockHashMap;
        mMainActivity = mainActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multiple_search_results_row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.mSymbolNameTextView.setText(mSearchResults.get(position) + " - " + mStockHashMap.get(mSearchResults.get(position)));
        holder.mSymbolNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMainActivity.downloadStockAndDismissDialog(mSearchResults.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSearchResults.size();
    }
}

