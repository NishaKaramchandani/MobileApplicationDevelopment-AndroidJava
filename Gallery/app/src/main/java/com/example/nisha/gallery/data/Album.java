package com.example.nisha.gallery.data;

import android.support.v4.app.NotificationCompat;

import com.google.gson.annotations.SerializedName;

public class Album {

    @SerializedName("userId")
    private String userId;
    @SerializedName("id")
    private String id;
    @SerializedName("title")
    private String title;

    public Album(String userId, String id, String title){
        this.userId = userId;
        this.id = id;
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
