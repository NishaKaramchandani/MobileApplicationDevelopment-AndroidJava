package com.example.nisha.gallery.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nisha.gallery.R;
import com.example.nisha.gallery.data.Photo;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class PhotosListAdapter extends RecyclerView.Adapter<PhotosListAdapter.ViewHolder> {

    List<Photo> mPhotoList;
    PhotoItemListener mPhotoItemListener;

    public PhotosListAdapter(List<Photo> photos, PhotoItemListener photoItemListener){
        setList(photos);
        mPhotoItemListener = photoItemListener;
    }

    public void replaceData(List<Photo> photos){
        setList(photos);
        notifyDataSetChanged();
    }

    public void setList(List<Photo> photos){ mPhotoList = checkNotNull(photos);};

    public class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView title;
        public ImageView thumbNail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.photo_title);
            thumbNail = (ImageView) itemView.findViewById(R.id.photo_thumbnail);
        }
    }

    @NonNull
    @Override
    public PhotosListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_photo, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotosListAdapter.ViewHolder viewHolder, int i) {
        final Photo photo = mPhotoList.get(i);
        viewHolder.title.setText(photo.getTitle());
        //TODO: setThumbnail Image
        Picasso.get()
                .load(photo.getUrl())
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(viewHolder.thumbNail);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPhotoItemListener.onItemClick(photo);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPhotoList.size();
    }

    public interface PhotoItemListener{
        void onItemClick(Photo photo);
    }
}
