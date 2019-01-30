package com.example.nk.knowyourgovernment;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OfficialActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mLocationTextView;
    private TextView mOfficeNameTextView;
    private TextView mOfficialNameTextView;
    private TextView mPartyNameTextView;
    private TextView mAddressValueTextView;
    private TextView mPhoneValueTextView;
    private TextView mEmailValueTextView;
    private TextView mWebsiteValueTextView;
    private ImageView mFaceBookImageView;
    private ImageView mTwitterImageView;
    private ImageView mGooglePlusImageView;
    private ImageView mYouTubeImageView;
    private ImageView mOfficialPhotoImageView;
    private SearchLocation mSearchLocation;
    private Official mOfficial;
    private ScrollView mScrollView;
    private ConstraintLayout mConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        mScrollView = (ScrollView) findViewById(R.id.activity_official_scrollview);
        mConstraintLayout = (ConstraintLayout) findViewById(R.id.activity_official_constraintlayout);
        mLocationTextView = (TextView) findViewById(R.id.location_textview);
        mOfficeNameTextView = (TextView) findViewById(R.id.officename_textview);
        mOfficialNameTextView = (TextView) findViewById(R.id.name_textview);
        mPartyNameTextView = (TextView) findViewById(R.id.partyname_textview);
        mAddressValueTextView = (TextView) findViewById(R.id.addressvalue_textview);
        mPhoneValueTextView = (TextView) findViewById(R.id.phonevalue_textview);
        mEmailValueTextView = (TextView) findViewById(R.id.emailvalue_textview);
        mWebsiteValueTextView = (TextView) findViewById(R.id.websitevalue_textview);

        mFaceBookImageView = (ImageView) findViewById(R.id.facebook_imageview);
        mTwitterImageView = (ImageView) findViewById(R.id.twitter_imageview);
        mGooglePlusImageView = (ImageView) findViewById(R.id.googleplus_imageview);
        mYouTubeImageView = (ImageView) findViewById(R.id.youtube_imageview);
        mOfficialPhotoImageView = (ImageView) findViewById(R.id.officialphoto_imageview);

        mFaceBookImageView.setOnClickListener(this);
        mTwitterImageView.setOnClickListener(this);
        mGooglePlusImageView.setOnClickListener(this);
        mYouTubeImageView.setOnClickListener(this);
        mOfficialPhotoImageView.setOnClickListener(this);

        mSearchLocation = getIntent().getExtras().getParcelable("SearchLocation");
        mOfficial = getIntent().getExtras().getParcelable("Official");

        mLocationTextView.setText(mSearchLocation.getCity() + ", " + mSearchLocation.getState() + " " + mSearchLocation.getZip());
        mOfficeNameTextView.setText(mOfficial.getOfficeName());
        mOfficialNameTextView.setText(mOfficial.getName());
        mPartyNameTextView.setText("(" + mOfficial.getParty() + ")");
        mAddressValueTextView.setText(mOfficial.getLineAddress() + "\n" + mOfficial.getCity() + ", " + mOfficial.getState() + " " + mOfficial.getZip());
        mPhoneValueTextView.setText(mOfficial.getPhone());
        mEmailValueTextView.setText(mOfficial.getEmail());
        mWebsiteValueTextView.setText(mOfficial.getUrl());

        setActivityBackgroundColor();
        downloadProfilePhoto();

        Linkify.addLinks(mWebsiteValueTextView, Linkify.WEB_URLS);
        Linkify.addLinks(mPhoneValueTextView, Linkify.PHONE_NUMBERS);
        Linkify.addLinks(mAddressValueTextView, Linkify.MAP_ADDRESSES);
        Linkify.addLinks(mEmailValueTextView, Linkify.EMAIL_ADDRESSES);
    }

    private void setActivityBackgroundColor() {
        if (mOfficial.getParty().equalsIgnoreCase("democratic")) {
            mScrollView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
        } else if (mOfficial.getParty().equalsIgnoreCase("republican")) {
            mScrollView.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            mScrollView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mConstraintLayout.setBackgroundColor(getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.facebook_imageview:
                //Toast.makeText(this, "Facebook icon clicked!", Toast.LENGTH_SHORT).show();
                facebookClicked(v);
                break;
            case R.id.twitter_imageview:
                //Toast.makeText(this, "Twitter icon clicked!", Toast.LENGTH_SHORT).show();
                twitterClicked(v);
                break;
            case R.id.googleplus_imageview:
                googlePlusClicked(v);
                //Toast.makeText(this, "Google Plus icon clicked!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.youtube_imageview:
                //Toast.makeText(this, "YouTube icon clicked!", Toast.LENGTH_SHORT).show();
                youTubeClicked(v);
                break;
            case R.id.officialphoto_imageview:
                // Toast.makeText(this, "YouTube icon clicked!", Toast.LENGTH_SHORT).show();
                if (mOfficial.getPhotoURL() != null && !mOfficial.getPhotoURL().equals("") && !mOfficial.getPhotoURL().equals("No Data Provided")) {
                    Intent photoDetailIntent = new Intent(this, PhotoDetailActivity.class);
                    photoDetailIntent.putExtra("SearchLocation", mSearchLocation);
                    photoDetailIntent.putExtra("Official", mOfficial);
                    startActivity(photoDetailIntent);
                }
                break;
        }
    }

    public void youTubeClicked(View v) {
        String name = mOfficial.getChannels().get(AppConstants.YOUTUBE_KEY);
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }

    public void googlePlusClicked(View v) {
        String name = mOfficial.getChannels().get(AppConstants.GOOGLE_PLUS_KEY);
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }

    public void facebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + mOfficial.getChannels().get(AppConstants.FACEBOOK_KEY);
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + mOfficial.getChannels().get(AppConstants.FACEBOOK_KEY);
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void twitterClicked(View v) {
        Intent intent = null;
        String name = mOfficial.getChannels().get(AppConstants.TWITTER_KEY);
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    private void downloadProfilePhoto() {
        if (mOfficial.getPhotoURL() != null && !mOfficial.getPhotoURL().equals("") && !mOfficial.getPhotoURL().equals("No Data Provided")) {
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
            mOfficialPhotoImageView.setImageDrawable(getResources().getDrawable(R.drawable.missingimage));
        }
    }

}
