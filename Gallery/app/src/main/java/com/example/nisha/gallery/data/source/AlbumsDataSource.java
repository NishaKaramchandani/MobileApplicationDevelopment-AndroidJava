package com.example.nisha.gallery.data.source;

import com.example.nisha.gallery.data.Album;
import com.example.nisha.gallery.data.Photo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AlbumsDataSource {
    @GET("/albums")
    Call<List<Album>> getAlbums();

    @GET("/photos")
    Call<List<Photo>> getAlbumDetails();
}