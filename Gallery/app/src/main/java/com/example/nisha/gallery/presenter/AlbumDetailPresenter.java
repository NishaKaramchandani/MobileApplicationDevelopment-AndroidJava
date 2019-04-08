package com.example.nisha.gallery.presenter;

import android.util.Log;

import com.example.nisha.gallery.data.Photo;
import com.example.nisha.gallery.view.AlbumDetailContract;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements AlbumDetailContract.Presenter, AlbumDetailContract.Intractor.OnFinishedListener {

    public AlbumDetailContract.Intractor mIntractor;
    public AlbumDetailContract.View mView;
    private String mAlbumId ;

    private static final String TAG = "AlbumDetailPresenter";
    public AlbumDetailPresenter(String albumId, AlbumDetailContract.View view, AlbumDetailContract.Intractor intractor){
        this.mView = view;
        this.mIntractor = intractor;
        this.mAlbumId = albumId;
        mView.setPresenter(this);
    }

    @Override
    public void navigateToPhotoDetails(Photo photo) {
        if(mView != null){
            mView.showPhotoDetails(photo);
        }
    }

    @Override
    public void start() {
        mIntractor.getAlbumDetails(mAlbumId, this);
    }

    @Override
    public void onFinished(List<Photo> photos) {
        if(mView != null){
            mView.showAlbumDetails(photos);
        }
    }

    @Override
    public void onFailure(Throwable t) {

    }
}
