package com.example.nisha.gallery.presenter;

import com.example.nisha.gallery.data.Album;
import com.example.nisha.gallery.view.AlbumsContract;

import java.util.List;



public class AlbumsPresenter implements AlbumsContract.Presenter, AlbumsContract.Intractor.OnFinishedListener {

    private AlbumsContract.View view;
    private AlbumsContract.Intractor intractor;

    public AlbumsPresenter(AlbumsContract.View mainView, AlbumsContract.Intractor intractor){
        this.view = mainView;
        this.intractor = intractor;

        view.setPresenter(this);
    }

    @Override
    public void loadAlbums() {
        intractor.getAlbumsList(this);
    }

    @Override
    public void onFinished(List<Album> albums) {
        if(this.view != null){
            view.showAlbums(albums);
        }
    }

    @Override
    public void navigateToAlbumDetails(Album album) {
        view.showAlbumDetailsUI(album);
    }

    @Override
    public void onFailure(Throwable t) {
        if(this.view != null){
        }
    }

    @Override
    public void start() {
        loadAlbums();
    }
}
