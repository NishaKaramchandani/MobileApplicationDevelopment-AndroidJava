package com.example.nisha.gallery.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.nisha.gallery.R;
import com.example.nisha.gallery.data.Photo;
import com.squareup.picasso.Picasso;

public class PhotoFragment extends Fragment {

    private String mPhotoURL;
    private String mPhotoTitle;
    private ImageView mImageView;

    public PhotoFragment() {
    }

    public static PhotoFragment newInstance() {
        return new PhotoFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_photo_detail, container, false);
        mImageView = root.findViewById(R.id.photo_detail);
        mPhotoTitle = getArguments().getString("PhotoTitle");
        ((MainActivity) getActivity()).setActionBarTitle(mPhotoTitle);
        mPhotoURL = getArguments().getString("PhotoURL");
        Picasso.get()
                .load(mPhotoURL)
                .placeholder(R.drawable.ic_launcher_foreground)
                .error(R.drawable.ic_launcher_foreground)
                .into(mImageView);
        return root;
    }
}
