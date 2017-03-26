package com.example.renadoparia.sportjunkiem;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class ArticleDetailActivity extends AppCompatActivity
{
    private static final String TAG = "ArticleDetailActivity";
    private static final String KEY = "articledata";

    private String mArticleId;
    private String mAuthorUID;
    private String mAuthorFname;
    private String mAuthorLname;
    private String mTitle;
    private String mSubtitle;
    private String mArticleData;
    private String mCategory;
    private String mTimeAndDateCreated;
    private String mLastUpdated;
    private String mUrlToImage;

    private CollapsingToolbarLayout mCollapsingToolbar;
    //    private ImageSwitcher mImageSwitcher;
    private TextView mTitleArticle;
    private TextView mSubTitleArticle;
    private TextView mActualArticle;
    private ImageView mCoverPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        initWidgets();
        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }


        String articleData = getIntent().getStringExtra(KEY);
        Log.d(TAG, "onCreate: article data: " + articleData);
        try
        {
            JSONObject articleJsonObject = new JSONObject(articleData);
            mArticleId = articleJsonObject.getString("mArticleID");
            mAuthorUID = articleJsonObject.getString("mAuthorUID");
            mAuthorFname = articleJsonObject.getString("mAuthorFname");
            mAuthorLname = articleJsonObject.getString("mAuthorLname");
            mTitle = articleJsonObject.getString("mTitle");
            mSubtitle = articleJsonObject.getString("mSubtitle");
            mArticleData = articleJsonObject.getString("mArticleData");
            mCategory = articleJsonObject.getString("mCategory");
            mTimeAndDateCreated = articleJsonObject.getString("mTimeAndDateCreated");
            mLastUpdated = articleJsonObject.getString("mLastUpdated");
            mUrlToImage = articleJsonObject.getString("mUrlToImage");

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private void initWidgets()
    {
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbar);
//        mImageSwitcher = (ImageSwitcher) findViewById(R.id.dexters_blood_slides);
//
//
//        final int appbarheight = getResources().getDimensionPixelSize(R.dimen.app_bar_height);
//        mImageSwitcher.setFactory(new ViewSwitcher.ViewFactory()
//        {
//            @Override
//            public View makeView()
//            {
//                ImageView myView = new ImageView(getApplicationContext());
//                myView.setLayoutParams
//                        (new ImageSwitcher.LayoutParams
//                                (CollapsingToolbarLayout.LayoutParams.MATCH_PARENT, CollapsingToolbarLayout.LayoutParams.MATCH_PARENT));
//                return myView;
//            }
//        });
//        mImageSwitcher.setImageResource(R.drawable.user_profile_bg);


        mCoverPhoto = (ImageView) findViewById(R.id.dexters_blood_slides);
        mTitleArticle = (TextView) findViewById(R.id.title);
        mSubTitleArticle = (TextView) findViewById(R.id.subtitle);
        mActualArticle = (TextView) findViewById(R.id.articleData);
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        mCollapsingToolbar.setTitle(mCategory);
        mTitleArticle.setText(mTitle);
        mSubTitleArticle.setText(mSubtitle);
        mActualArticle.setText(mArticleData);
        Picasso.with(getApplicationContext())
                .load(mUrlToImage)
                .error(R.drawable.ic_image_black_48dp)
                .into(mCoverPhoto);

    }
}
