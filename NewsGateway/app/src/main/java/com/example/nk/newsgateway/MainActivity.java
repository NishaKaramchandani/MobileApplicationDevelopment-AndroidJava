package com.example.nk.newsgateway;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private NewsFragmentPagerAdapter mNewsFragmentPagerAdapter;
    private List<Fragment> mFragmentList;
    private ViewPager mViewPager;
    HashSet<String> category_set = new HashSet<String>();
    static int source_pos = -1;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    static private ArrayList<String> items = new ArrayList<>();
    static private ArrayList<String> items_id = new ArrayList<>();
    static long adapter_counter = 0;

    static final String ACTION_NEWS_STORY = "ANS";
    static final String ACTION_MSG_TO_SVC = "AMTS";
    Intent intent;
    public NewsServiceReceiver newsReceiver;
    ArrayList<Article> mArticleList = new ArrayList<>();
    static String sourcename;

    private ArrayList<Sources> mSourceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("On Create", "Called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newsReceiver = new NewsServiceReceiver();
        intent = new Intent(MainActivity.this, NewsService.class);
        startService(intent);


        IntentFilter filter = new IntentFilter(ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);
        deleteCache(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerList = findViewById(R.id.drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_item, items));
        mDrawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        selectItem(position);
                    }
                }
        );
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mFragmentList = getmFragmentList();
        mNewsFragmentPagerAdapter = new NewsFragmentPagerAdapter(getSupportFragmentManager());
        mNewsFragmentPagerAdapter.baseId = adapter_counter;
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(mNewsFragmentPagerAdapter);

        new NewsSourceDownloader(MainActivity.this).execute();
    }

    public void sources_data_to_add(ArrayList<Sources> sources_data) {
        items.clear();
        items_id.clear();
        for (int i = 0; i < sources_data.size(); i++) {
            items.add(sources_data.get(i).getName());
            items_id.add(sources_data.get(i).getId());
            category_set.add(sources_data.get(i).getCategory());
        }
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_item, items));
        mSourceList = sources_data;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        getMenuInflater().inflate(R.menu.opt_menu, menu);

        int i = 0;
        for (Iterator<String> it = category_set.iterator(); it.hasNext(); ) {
            MenuItem item = menu.add(Menu.NONE, i, 2, it.next());
            i++;
        }
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    public void noDataFound() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Make Sure that WiFi or Mobile-data is turned on.");
        builder.setTitle("No Data Found");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private List<Fragment> getmFragmentList() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        return fragmentList;
    }

    private void selectItem(int position) {
        source_pos = position;
        sourcename = items.get(position);
        setTitle(items.get(position));
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_MSG_TO_SVC);
        intent.putExtra("SOURCE_DATA", items_id.get(position));
        sendBroadcast(intent);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        stopService(intent);
        super.onDestroy();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState1) {
        setTitle(savedInstanceState1.getString("Source"));
        adapter_counter = savedInstanceState1.getLong("baseId_count");
        Log.d("adapter_counter:- ", Long.toString(adapter_counter));

        mArticleList.clear();
        mArticleList.addAll((ArrayList) savedInstanceState1.getSerializable("ArticleList"));

        showFragmentsOnRestore(mArticleList, source_pos);
        super.onRestoreInstanceState(savedInstanceState1);
    }

    public static void deleteCache(Context context) {
        File dir = context.getExternalCacheDir();
        if (dir != null && dir.isDirectory())
            deleteDir(dir);

    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putLong("baseId_count", mNewsFragmentPagerAdapter.baseId);
        outState.putInt("Source_index", source_pos);
        outState.putString("Source", sourcename);

        outState.putSerializable("ArticleList", mArticleList);

        super.onSaveInstanceState(outState);

    }

    private void showFragments(ArrayList<Article> articlesList, int pos) {
        mViewPager.setBackground(null);
        for (int i = 0; i < mNewsFragmentPagerAdapter.getCount(); i++)
            mNewsFragmentPagerAdapter.notifyChangeInPosition(i);
        mFragmentList.clear();
        for (int i = 0; i < articlesList.size(); i++) {
            mFragmentList.add(NewsFragment.newInstance(this, articlesList.get(i), i, articlesList.size()));
        }
        mNewsFragmentPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);
    }

    private void showFragmentsOnRestore(ArrayList<Article> articlesList, int pos) {
        if (articlesList.size() == 0)
            setTitle(R.string.app_name);
        else
            mViewPager.setBackground(null);
        mFragmentList.clear();
        for (int i = 0; i < articlesList.size(); i++) {
            mFragmentList.add(NewsFragment.newInstance(this, articlesList.get(i), i, articlesList.size()));
        }

        for (int i = 0; i < mNewsFragmentPagerAdapter.getCount(); i++)
            mNewsFragmentPagerAdapter.notifyChangeInPosition(i);

        mNewsFragmentPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        new NewsSourceDownloader(MainActivity.this).execute(item.toString());
        return true;
    }

    private class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
        private long baseId = 0;

        public NewsFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public long getItemId(int position) {
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }
    }

    class NewsServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    if (intent.hasExtra("IntentArticleList")) {
                        mArticleList.clear();
                        mArticleList.addAll((ArrayList) intent.getSerializableExtra("IntentArticleList"));
                        showFragments(mArticleList, source_pos);
                    }
                    break;
            }
        }
    }
}