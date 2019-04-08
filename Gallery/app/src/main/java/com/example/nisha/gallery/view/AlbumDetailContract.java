package com.example.nisha.gallery.view;

import com.example.nisha.gallery.BasePresenter;
import com.example.nisha.gallery.BaseView;
import com.example.nisha.gallery.data.Photo;

import java.util.List;

public interface AlbumDetailContract {

    interface View extends BaseView<Presenter> {
        void showAlbumDetails(List<Photo> photos);
        void showPhotoDetails(Photo photo);
    }

    interface Presenter extends BasePresenter {
        void navigateToPhotoDetails(Photo photo);
    }

    interface Intractor{
        interface OnFinishedListener{
            void onFinished(List<Photo> photos);
            void onFailure(Throwable t);
        }

        void getAlbumDetails(String albumId, OnFinishedListener onFinishedListener);
    }
}
