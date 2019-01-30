package com.example.nk.stockwatch;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StockDownloader extends AsyncTask<String, Void, Stock> {
    private static final String TAG = "NameDownloader";
    private MainActivity mainActivity;

    public StockDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(Stock stock) {
        mainActivity.updateStockList(stock);
    }

    @Override
    protected Stock doInBackground(String... params) {

        Uri.Builder buildURL = Uri.parse(AppConstants.STOCK_URL2 + params[0] + AppConstants.STOCK_URL_2TRAILING).buildUpon();
        String urlToUse = buildURL.build().toString();
        Log.d(TAG, "doInBackground: " + urlToUse);

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            Log.d(TAG, "doInBackground: Response Code: " + conn.getResponseCode() + ", " + conn.getResponseMessage());

            conn.setRequestMethod("GET");

            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
            Log.d(TAG, "doInBackground: " + sb.toString());

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }

        Stock stock = parseJSON(sb.toString());
        return stock;
    }

    private Stock parseJSON(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            Stock stock = new Stock();
            stock.setSymbol(jsonObject.getString(AppConstants.JSON_TAG_SYMBOL));
            stock.setCompanyName(jsonObject.getString(AppConstants.JSON_TAG_COMPANY_NAME));
            stock.setLatestPrice(jsonObject.getDouble(AppConstants.JSON_TAG_LATESTPRICE));
            stock.setChange(jsonObject.getDouble(AppConstants.JSON_TAG_CHANGE));
            stock.setChangePercent(jsonObject.getDouble(AppConstants.JSON_TAG_CHANGE_PERCENTAGE));

            Log.d(TAG, "parseJSON: stock data parsed to hash map " + stock.toString());
            return stock;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

