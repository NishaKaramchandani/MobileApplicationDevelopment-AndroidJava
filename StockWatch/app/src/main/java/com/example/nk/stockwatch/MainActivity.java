package com.example.nk.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private StockRecyclerViewAdapter mStockAdapter;
    private List<Stock> mStockList = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private static final String TAG = "MainActivity";
    private HashMap<String, String> mStockURLHashMap = new HashMap<>();
    private HashMap<String, String> mStockDBHashMap = new HashMap<>();
    private AlertDialog searchResultsDialog;
    private DatabaseHelper mDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.stock_recyclerview);
        mSwipeRefreshLayout = findViewById(R.id.swiperefresh);

        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mStockAdapter = new StockRecyclerViewAdapter(MainActivity.this, mStockList);
        mRecyclerView.setAdapter(mStockAdapter);

        if (isNetworkAvailable()) {
            NameDownloader alt = new NameDownloader(this);
            alt.execute();
        } else {
            showNoNetworkDialog("Stocks cannot be updated without a network connection");
        }

        mDatabaseHelper = new DatabaseHelper(this);
        loadStocksFromDatabase();

        mSwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        loadStocksFromDatabase();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );
    }

    private void loadStocksFromDatabase() {
        mStockDBHashMap = mDatabaseHelper.loadStocks();
        if (isNetworkAvailable()) {
            mStockList.clear();
            Iterator symbolIterator = mStockDBHashMap.keySet().iterator();
            while (symbolIterator.hasNext()) {
                StockDownloader alt = new StockDownloader(MainActivity.this);
                alt.execute(symbolIterator.next().toString());
            }
        } else {
            showNoNetworkDialog("Stocks cannot be updated without a network connection");
            if (mStockList.size() == 0) {
                Iterator iterator = mStockDBHashMap.keySet().iterator();
                while (iterator.hasNext()) {
                    String symbol = iterator.next().toString();
                    Stock stock = new Stock();
                    stock.setSymbol(symbol);
                    stock.setCompanyName(mStockDBHashMap.get(symbol));
                    mStockList.add(stock);
                    mStockAdapter.notifyDataSetChanged();
                }
            }
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            //Toast.makeText(this, "Cannot access ConnectivityManager", Toast.LENGTH_SHORT).show();
            return false;
        }
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            //Toast.makeText(this, "Connected to the internet!", Toast.LENGTH_LONG);
            return true;
        } else {
            //Toast.makeText(this, "Not connected to the internet!", Toast.LENGTH_LONG);
            return false;
        }
    }

    private void showNoNetworkDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(message)
                .setTitle("No Network Connection");
        AlertDialog noStockFoundDialog = builder.create();
        noStockFoundDialog.show();
    }

    public void getStockSymbolData(HashMap stockData) {
        mStockURLHashMap = stockData;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_stock:
                if (isNetworkAvailable()) {
                    showAddStockDialog();
                } else {
                    showNoNetworkDialog("Stocks cannot be added without a network connection");
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAddStockDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.addstock_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText searchStockEditText = (EditText) dialogView.findViewById(R.id.stockNameEditText);
        searchStockEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

        dialogBuilder.setTitle("Stock Selection");
        dialogBuilder.setMessage("Please enter a stock symbol");
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                ArrayList<String> results = new ArrayList<String>();
                results = searchStockHashMap(searchStockEditText.getText().toString().trim());
                if (results == null || results.size() == 0) {
                    //No matching stock
                    showNoSTockFoundDialog(searchStockEditText.getText().toString());
                } else if (results.size() == 1) {
                    //One stock matching
                    StockDownloader alt = new StockDownloader(MainActivity.this);
                    alt.execute(results.get(0).toString());
                } else {
                    //Multiple matching stocks
                    showMultipleStockListDialog(results);
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog enterStockSymbolEditTextDialog = dialogBuilder.create();
        enterStockSymbolEditTextDialog.show();
    }

    public ArrayList<String> searchStockHashMap(String regex) {
        ArrayList<String> results = new ArrayList<String>();
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Iterator<String> keysIterator = mStockURLHashMap.keySet().iterator();
        while (keysIterator.hasNext()) {
            String candidate = keysIterator.next();
            Matcher m = p.matcher(candidate);
            if (m.find()) {
                System.out.println("it matches" + candidate);
                results.add(candidate);
            }
        }

        return results;
    }

    public void updateStockList(Stock stock) {
        //Duplicate Condition
        boolean isDuplicate = false;
        for (int i = 0; i < mStockList.size(); i++) {
            if (mStockList.get(i).getSymbol().equalsIgnoreCase(stock.getSymbol())) {
                isDuplicate = true;
                break;
            }
        }
        if (isDuplicate) {
            //Duplicate stock error
            showDuplicateStockError(stock.getSymbol());
        } else {
            mStockList.add(stock);
            Collections.sort(mStockList, symbolComparator);
            mStockAdapter.notifyDataSetChanged();
            //First time records are already in DB so we need this loop.
            if (!mStockDBHashMap.containsKey(stock.getSymbol())) {
                mDatabaseHelper.addStock(stock);
                mStockDBHashMap.put(stock.getSymbol(), stock.getCompanyName());
            }
        }
    }

    private void showNoSTockFoundDialog(String searchText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("No data for symbol")
                .setTitle("Symbol not found " + searchText);
        AlertDialog noStockFoundDialog = builder.create();
        noStockFoundDialog.show();
    }

    private void showDuplicateStockError(String stockSymbol) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Stock symbol " + stockSymbol + " is already displayed.").setIcon(getResources().getDrawable(R.drawable.duplicate_stock_icon))
                .setTitle("Duplicate Stock");
        AlertDialog duplicateStockDialog = builder.create();
        duplicateStockDialog.show();
    }

    private void showMultipleStockListDialog(ArrayList results) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.multiple_searchresults_dialog, null);
        dialogBuilder.setView(dialogView);
        RecyclerView recyclerView = dialogView.findViewById(R.id.searchresults_recyclerview);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(mLayoutManager);

        MultipleSearchResultsRecyclerViewAdapter multipleSearchResultsRecyclerViewAdapter = new MultipleSearchResultsRecyclerViewAdapter(MainActivity.this, results, mStockURLHashMap);
        recyclerView.setAdapter(multipleSearchResultsRecyclerViewAdapter);

        dialogBuilder.setTitle("Make a selection");
        dialogBuilder.setPositiveButton("NEVERMIND", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        searchResultsDialog = dialogBuilder.create();
        searchResultsDialog.show();
    }

    public void downloadStockAndDismissDialog(String stockSymbol) {
        StockDownloader alt = new StockDownloader(MainActivity.this);
        alt.execute(stockSymbol);
        searchResultsDialog.dismiss();
    }

    public void deleteStock(int position) {
        showDeleteDialog(position);
    }

    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Delete Stock Symbol " + mStockList.get(position).getSymbol())
                .setTitle("Delete Stock").setIcon(MainActivity.this.getResources().getDrawable(R.drawable.delete_icon));
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                mDatabaseHelper.deleteStock(mStockList.get(position).getSymbol());
                mStockList.remove(position);
                mStockAdapter.notifyDataSetChanged();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                //pass
            }
        });
        AlertDialog deleteStockDialog = builder.create();
        deleteStockDialog.show();
    }

    Comparator<Stock> symbolComparator = new Comparator<Stock>() {
        @Override
        public int compare(Stock o1, Stock o2) {
            return o1.getSymbol().compareTo(o2.getSymbol());
        }
    };

}
