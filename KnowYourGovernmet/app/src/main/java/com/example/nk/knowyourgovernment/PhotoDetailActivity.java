package com.example.nk.knowyourgovernment;

import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    private TextView mOfficeNameTextView;
    private TextView mOfficialNameTextView;
    private TextView mLocationTextView;
    private ImageView mOfficialPhotoImageView;
    private SearchLocation mSearchLocation;
    private Official mOfficial;
    private ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        mConstraintLayout = (ConstraintLayout)findViewById(R.id.activity_photo_detail_constraintlayout);

        mLocationTextView = (TextView) findViewById(R.id.location_textview);
        mOfficeNameTextView = (TextView) findViewById(R.id.officename_textview);
        mOfficialNameTextView = (TextView) findViewById(R.id.officialnameparty_textview);
        mOfficialPhotoImageView = (ImageView) findViewById(R.id.officialphoto_imageview);

        mSearchLocation = getIntent().getExtras().getParcelable("SearchLocation");
        mOfficial = getIntent().getExtras().getParcelable("Official");

        mLocationTextView.setText(mSearchLocation.getCity() + ", " + mSearchLocation.getState() + " " + mSearchLocation.getZip());
        mOfficeNameTextView.setText(mOfficial.getOfficeName());
        mOfficialNameTextView.setText(mOfficial.getName());

        downloadProfilePhoto();
        setActivityBackgroundColor();
    }

    private void setActivityBackgroundColor() {
        if (mOfficial.getParty().equalsIgnoreCase("democratic")) {
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        } else if (mOfficial.getParty().equalsIgnoreCase("republican")) {
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.black));
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.black));
        }
    }

    private void downloadProfilePhoto() {
        if (mOfficial.getPhotoURL() != null || !mOfficial.getPhotoURL().equals("")) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
// Here we try https if the http image attempt failed
                    final String changedUrl = mOfficial.getPhotoURL().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .error(R.drawable.missingimage)
                            .placeholder(R.drawable.placeholder)
                            .into(mOfficialPhotoImageView);
                }
            }).build();
            picasso.load(mOfficial.getPhotoURL())
                    .error(R.drawable.missingimage)
                    .placeholder(R.drawable.placeholder)
                    .into(mOfficialPhotoImageView);
        } else {
            Picasso.get().load(mOfficial.getPhotoURL())
                    .error(R.drawable.missingimage)
                    .placeholder(R.drawable.placeholder)
                    .into(mOfficialPhotoImageView);
        }
    }
}
