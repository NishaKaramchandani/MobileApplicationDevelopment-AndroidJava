package com.example.nk.newsgateway;

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
import java.util.ArrayList;

public class NewsArticleDownloader extends AsyncTask<String, Integer, String> {

    private final String URL_ARTICLE_HEAD = "https://newsapi.org/v2/top-headlines?pageSize=50&sources=";
    private final String URL_ARTICLE_TAIL = "&apiKey=";
    private final String KEY = "b3366556e8e84d0e9e568b7ec396cfbf";
    private static final String TAG = "NewsArticleDownloader";
    static NewsService newsService = new NewsService();

    @Override
    protected void onPostExecute(String s) {
        ArrayList<Article> articleList = parseJSON(s);
        newsService.setArticles(articleList);
    }

    @Override
    protected String doInBackground(String... params) {
        Uri dataUri;
        dataUri = Uri.parse(URL_ARTICLE_HEAD + params[0].toLowerCase() + URL_ARTICLE_TAIL + KEY);
        String urlToUse = dataUri.toString();

        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int HTTP_NOT_FOUND = conn.getResponseCode();
            if (HTTP_NOT_FOUND == 404) {
                return null;
            } else {
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append('\n');
                }
                return sb.toString();

            }
        } catch (Exception e) {
            return null;
        }
    }

    private ArrayList<Article> parseJSON(String s) {
        try {
            Log.d(TAG, "parseJSON");
            ArrayList<Article> articleList = new ArrayList<>();
            JSONObject jObjMain = new JSONObject(s);

            JSONArray articles = jObjMain.getJSONArray("articles");
            Log.d(TAG, "Length " + articles.length());
            for (int i = 0; i < articles.length(); i++) {
                JSONObject source_object = (JSONObject) articles.get(i);
                articleList.add(new Article(source_object.getString("author"), source_object.getString("title"), source_object.getString("description"), source_object.getString("urlToImage"), source_object.getString("publishedAt"), source_object.getString("url")));
            }
            return articleList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

