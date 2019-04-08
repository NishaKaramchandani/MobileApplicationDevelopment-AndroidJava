package com.example.nisha.gallery.view;

import com.example.nisha.gallery.BasePresenter;
import com.example.nisha.gallery.BaseView;
import com.example.nisha.gallery.data.Album;

import java.util.List;

public interface AlbumsContract {

    interface View extends BaseView<Presenter> {

        void showAlbums(List<Album> albums);

        void showAlbumDetailsUI(Album album);

    }

    interface Presenter extends BasePresenter {
        void loadAlbums();
        void navigateToAlbumDetails(Album album);
    }

    interface Intractor {

        interface OnFinishedListener {
            void onFinished(List<Album> albums);
            void onFailure(Throwable t);
        }

        void getAlbumsList(OnFinishedListener onFinishedListener);
    }
}
