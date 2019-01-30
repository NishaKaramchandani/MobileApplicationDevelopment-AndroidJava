package com.example.nk.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;


public class NewsFragment extends Fragment {
    private static final String TAG = "NewsFragment";
    static MainActivity mMainActivity;

    public TextView mArticleTitleTextView;
    public TextView mArticleDateTextView;
    public TextView mArticleAuthorTextView;
    public ImageView mArticleImageTextView;
    public TextView mArticleDescriptionTextView;
    public TextView mArticleCountTextView;


    public static final NewsFragment newInstance(MainActivity mainActivity, Article article, int articleNo, int noOfArticles) {
        NewsFragment fragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("BundleArticle", article);
        bundle.putInt("ArticleNo", articleNo);
        bundle.putInt("NoOfArticles", noOfArticles);
        mMainActivity = mainActivity;
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_news, container, false);
        try {
            mArticleTitleTextView = view.findViewById(R.id.title_textview);
            mArticleDateTextView = view.findViewById(R.id.date_textview);
            mArticleAuthorTextView = view.findViewById(R.id.author_textview);
            mArticleImageTextView = view.findViewById(R.id.image_textview);
            mArticleDescriptionTextView = view.findViewById(R.id.description_textview);
            mArticleCountTextView = view.findViewById(R.id.count_textview);

            Article article = (Article) getArguments().getSerializable("BundleArticle");
            final String articleUrl = article.getUrlToArticle();

            String title = article.getTitle();
            mArticleTitleTextView.setText(title);

            mArticleTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Clicked Title");
                    Uri uri = Uri.parse(articleUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            String publishedDate = article.getPublishedAt();
            if (publishedDate == null || publishedDate.equals("null")) {
                mArticleDateTextView.setVisibility(View.GONE);
            }
            mArticleDateTextView.setText(changeDateFormat(publishedDate));


            String author = article.getAuthor();
            if (author == null || author.equals("null")) {
                mArticleAuthorTextView.setVisibility(View.GONE);
            }
            mArticleAuthorTextView.setText(author);

            String imageUrl = article.getUrlToImage();
            setImage(mArticleImageTextView, imageUrl);

            mArticleImageTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Clicked Image");
                    Uri uri = Uri.parse(articleUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });

            String description = article.getDescription();
            if (description == null || description.equals("null") || description.isEmpty()) {
                description = getString(R.string.no_description);
            }
            mArticleDescriptionTextView.setText(description);

            mArticleDescriptionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: Clicked Description");
                    Uri uri = Uri.parse(articleUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            mArticleDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());

            mArticleCountTextView.setText(getArguments().getInt("ArticleNo") + 1 + " of " + getArguments().getInt("NoOfArticles"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public void setImage(final ImageView articleImage, final String Url) {
        if (Url == null) {
            articleImage.setImageResource(R.drawable.no_img);
        } else {

            Picasso picasso = new Picasso.Builder(mMainActivity)
                    .listener(new Picasso.Listener() {
                        @Override
                        public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                            final String changedUrl = Url.replace("http:", "https:");
                            picasso.load(changedUrl)
                                    .error(R.drawable.brokenimage)
                                    .placeholder(R.drawable.placeholder)
                                    .into(articleImage);
                        }
                    })
                    .build();

            picasso.load(Url)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(articleImage);
        }
    }

    private String changeDateFormat(String articledate) {
        SimpleDateFormat format_old = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date date_old = null;
        try {
            if (articledate != null) {
                date_old = format_old.parse(articledate);
                SimpleDateFormat format_new = new SimpleDateFormat("MMM dd, yyyy HH:mm");
                String date_new = format_new.format(date_old);
                return date_new;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";

    }

}
