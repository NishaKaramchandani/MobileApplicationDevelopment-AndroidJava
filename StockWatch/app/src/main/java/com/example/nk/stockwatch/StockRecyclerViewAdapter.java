package com.example.nk.stockwatch;

import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

public class StockRecyclerViewAdapter extends RecyclerView.Adapter<StockRecyclerViewAdapter.ViewHolder> {
    private List<Stock> mStockList;
    private MainActivity mMainActivity;
    private static DecimalFormat df2 = new DecimalFormat("##.##");

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout mConstraintLayout;
        public TextView mSymbolTextView;
        public TextView mCompanyNameTextView;
        public TextView mLatestPriceTextView;
        public TextView mChangePriceTextView;
        public ImageView mChangeImageView;

        public ViewHolder(ConstraintLayout v) {
            super(v);
            mConstraintLayout = v;
            mSymbolTextView = (TextView) v.findViewById(R.id.symbolTextView);
            mCompanyNameTextView = (TextView) v.findViewById(R.id.companyNameTextView);
            mLatestPriceTextView = (TextView) v.findViewById(R.id.latestPriceTextView);
            mChangePriceTextView = (TextView) v.findViewById(R.id.changeTextView);
            mChangeImageView = (ImageView) v.findViewById(R.id.changeSymbolImageView);
        }
    }

    public StockRecyclerViewAdapter(MainActivity mainActivity, List stockList) {
        mMainActivity = mainActivity;
        mStockList = stockList;
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Stock stock = mStockList.get(position);
        holder.mConstraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mMainActivity.deleteStock(position);
                return false;
            }
        });
        holder.mConstraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(AppConstants.BROWSER_URL+stock.getSymbol()));
                mMainActivity.startActivity(i);
            }
        });
        if(Math.abs(stock.getChange())==stock.getChange()){
            holder.mSymbolTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_green_dark));
            holder.mCompanyNameTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_green_dark));
            holder.mLatestPriceTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_green_dark));
            holder.mChangePriceTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_green_dark));
            holder.mChangeImageView.setImageDrawable(mMainActivity.getResources().getDrawable(R.drawable.up_arrow));
        }else{
            holder.mSymbolTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_red_dark));
            holder.mCompanyNameTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_red_dark));
            holder.mLatestPriceTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_red_dark));
            holder.mChangePriceTextView.setTextColor(mMainActivity.getResources().getColor(android.R.color.holo_red_dark));
            holder.mChangeImageView.setImageDrawable(mMainActivity.getResources().getDrawable(R.drawable.down_arrow));
        }
        holder.mSymbolTextView.setText(stock.getSymbol());
        holder.mCompanyNameTextView.setText(stock.getCompanyName());
        holder.mLatestPriceTextView.setText("" + stock.getLatestPrice());
        holder.mChangePriceTextView.setText(df2.format(stock.getChange()) + "(" + df2.format(stock.getChangePercent()) + "%)");
    }

    @Override
    public int getItemCount() {
        return mStockList.size();
    }
}
