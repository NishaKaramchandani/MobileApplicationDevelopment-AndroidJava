package com.example.nk.stockwatch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class NameDownloader extends AsyncTask<String, Void, String> {
    private static final String TAG = "NameDownloader";
    private MainActivity mainActivity;
    private HashMap<String, String> stockData = new HashMap<>();
    private Bitmap bitmap;

    public NameDownloader(MainActivity ma) {
        mainActivity = ma;
    }

    @Override
    protected void onPostExecute(String s) {
        mainActivity.getStockSymbolData(stockData);
    }

    @Override
    protected String doInBackground(String... params) {

        Uri.Builder buildURL = Uri.parse(AppConstants.STOCK_URL1).buildUpon();
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

        parseJSON(sb.toString());


        return null;
    }

    private void parseJSON(String s) {
        try {
            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                stockData.put(jsonObject.getString(AppConstants.JSON_TAG_SYMBOL), jsonObject.getString(AppConstants.JSON_TAG_NAME));
            }
            Log.d(TAG, "parseJSON: stock data parsed to hash map "+stockData.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
