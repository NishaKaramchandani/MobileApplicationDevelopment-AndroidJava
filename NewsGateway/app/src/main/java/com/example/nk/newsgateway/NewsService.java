package com.example.nk.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class NewsService extends Service {

    private static final String TAG = "NewsService";
    static ArrayList<Article> articlesList = new ArrayList<>();
    static final String ACTION_MSG_TO_SVC = "AMTS";
    static NewsServiceReceiver newsServiceReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        newsServiceReceiver = new NewsServiceReceiver();
        IntentFilter filter = new IntentFilter(ACTION_MSG_TO_SVC);
        registerReceiver(newsServiceReceiver, filter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        if (articlesList.size() == 0)
                            Thread.sleep(250);
                        else
                            sendMessage();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return START_STICKY;
    }

    public void setArticles(ArrayList<Article> articles_data) {
        articlesList.clear();
        articlesList = articles_data;
    }

    private void sendMessage() {
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_NEWS_STORY);

        intent.putExtra("IntentArticleList", articlesList);
        sendBroadcast(intent);
        articlesList.clear();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: Destroyed");
        unregisterReceiver(newsServiceReceiver);
        super.onDestroy();
    }

    class NewsServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case ACTION_MSG_TO_SVC:
                    if (intent.hasExtra("SOURCE_DATA")) {
                        new NewsArticleDownloader().execute(intent.getStringExtra("SOURCE_DATA"));
                    }
                    break;
            }
        }
    }
}