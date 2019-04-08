package com.example.nisha.gallery.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.nisha.gallery.R;
import com.example.nisha.gallery.data.source.AlbumsIntractor;
import com.example.nisha.gallery.presenter.AlbumsPresenter;
import com.example.nisha.gallery.util.ActivityUtils;

public class MainActivity extends AppCompatActivity {

    AlbumsPresenter mAlbumsPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AlbumsFragment albumsFragment =
                (AlbumsFragment) getSupportFragmentManager().findFragmentById(R.id.content);
        if (albumsFragment == null) {
            // Create the fragment
            albumsFragment = AlbumsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), albumsFragment, R.id.content);
        }

        mAlbumsPresenter = new AlbumsPresenter(albumsFragment, new AlbumsIntractor());
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}
