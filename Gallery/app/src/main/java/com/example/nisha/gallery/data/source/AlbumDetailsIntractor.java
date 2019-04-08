package com.example.nisha.gallery.data.source;

import android.util.Log;

import com.example.nisha.gallery.data.Photo;
import com.example.nisha.gallery.view.AlbumDetailContract;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumDetailsIntractor implements AlbumDetailContract.Intractor {

    private static final String TAG = "AlbumDetailsIntractor";

    @Override
    public void getAlbumDetails(final String albumId, final OnFinishedListener onFinishedListener) {
        AlbumsDataSource dataSource = RetrofitClientInstance.getRetrofitInstance().create(AlbumsDataSource.class);
        Call<List<Photo>> photoListCall = dataSource.getAlbumDetails();
        String msg = photoListCall.request().url().toString();
        Log.d("Retrofit: ", msg);

        photoListCall.enqueue(new Callback<List<Photo>>() {
            @Override
            public void onResponse(Call<List<Photo>> call, Response<List<Photo>> response) {
                List<Photo> photoList = new ArrayList<>();
                String msg = response.body().toString();
                Log.d("Retrofit body: ", msg);
                for (Photo photo : response.body()) {
                    if (photo.getAlbumId().equals(albumId)) {
                        photoList.add(photo);
                    }
                }
                Log.d(TAG, "onResponse: ALbumId:" + albumId + " photo list size:" + photoList.size() + " Original photolist size::" + response.body().size());
                onFinishedListener.onFinished(photoList);
            }

            @Override
            public void onFailure(Call<List<Photo>> call, Throwable t) {
                onFinishedListener.onFailure(t);
            }
        });
    }
}
