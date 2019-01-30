package com.example.nk.knowyourgovernment;

import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class OfficeRecyclerViewAdapter extends RecyclerView.Adapter<OfficeRecyclerViewAdapter.ViewHolder> {
    private List<Official> mOfficialList = new ArrayList<>();
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout mCardView;
        public TextView mOfficeNameTextView;
        private TextView mOfficialNameTextView;

        public ViewHolder(ConstraintLayout v) {
            super(v);
            mCardView = v;
            mOfficeNameTextView = v.findViewById(R.id.officename_textview);
            mOfficialNameTextView = v.findViewById(R.id.officialnameparty_textview);
        }
    }

    public OfficeRecyclerViewAdapter(Context context, List<Official> officialList) {
        mContext = context;
        mOfficialList = officialList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        ConstraintLayout v = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_row_layout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //holder.mTextView.setText(mDataset[position]);
        final Official official = mOfficialList.get(position);
        holder.mOfficeNameTextView.setText(official.getOfficeName());
        holder.mOfficialNameTextView.setText(official.getName() + "(" + official.getParty() + ")");
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "On Click event triggered", Toast.LENGTH_SHORT).show();
                Intent aboutIntent = new Intent(mContext, OfficialActivity.class);
                aboutIntent.putExtra("SearchLocation",((MainActivity)mContext).getSearchLocation());
                aboutIntent.putExtra("Official",official);
                mContext.startActivity(aboutIntent);
            }
        });
        holder.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent aboutIntent = new Intent(mContext, OfficialActivity.class);
                aboutIntent.putExtra("SearchLocation",((MainActivity)mContext).getSearchLocation());
                aboutIntent.putExtra("Official",official);
                mContext.startActivity(aboutIntent);
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mOfficialList.size();
    }
}
