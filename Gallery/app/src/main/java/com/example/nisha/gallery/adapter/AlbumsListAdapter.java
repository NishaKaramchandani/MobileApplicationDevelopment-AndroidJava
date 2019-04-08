package com.example.nisha.gallery.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.nisha.gallery.R;
import com.example.nisha.gallery.data.Album;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AlbumsListAdapter extends RecyclerView.Adapter<AlbumsListAdapter.ViewHolder> {

    private List<Album> mAlbums;
    private AlbumItemListener mAlbumItemListener;

    public AlbumsListAdapter(List<Album> albums, AlbumItemListener albumItemListener) {
        setList(albums);
        mAlbumItemListener = albumItemListener;
    }

    public void replaceData(List<Album> albums) {
        setList(albums);
        notifyDataSetChanged();
    }

    private void setList(List<Album> tasks) {
        mAlbums = checkNotNull(tasks);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.album_title);
        }
    }

    @NonNull
    @Override
    public AlbumsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_album, viewGroup, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumsListAdapter.ViewHolder viewHolder, int i) {
        final Album album = mAlbums.get(i);
        viewHolder.title.setText(album.getTitle());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAlbumItemListener.onAlbumClicked(album);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlbums.size();
    }

    public interface AlbumItemListener{
        void onAlbumClicked(Album album);
    }
}
