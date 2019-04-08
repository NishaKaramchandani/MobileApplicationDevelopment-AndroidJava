package com.example.nisha.gallery.data.source;

import android.util.Log;

import com.example.nisha.gallery.data.Album;
import com.example.nisha.gallery.view.AlbumsContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumsIntractor implements AlbumsContract.Intractor {
    @Override
    public void getAlbumsList(final OnFinishedListener onFinishedListener) {
        AlbumsDataSource dataSource = RetrofitClientInstance.getRetrofitInstance().create(AlbumsDataSource.class);
        Call<List<Album>> albumsListCall = dataSource.getAlbums();
        String msg = albumsListCall.request().url().toString();
        Log.d("Retrofit: ", msg);

        albumsListCall.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                String msg = response.body().toString();
                Log.d("Retrofit body: ", msg);

                onFinishedListener.onFinished(response.body());

            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable error) {
                Log.d("Retrofit: ", "Something went wrong");
                onFinishedListener.onFailure(error);

            }
        });
    }
}
