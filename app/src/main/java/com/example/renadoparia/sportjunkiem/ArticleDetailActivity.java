package com.example.renadoparia.sportjunkiem;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArticleDetailActivity extends AppCompatActivity
{
    private static final String TAG = "ArticleDetailActivity";
    private static final String KEY = "articledata";
    private static final String ARTICLE_REF = "ARTICLES";
    private static final String QUERY_BY_CATEGORY = "category";
    private static final String QUERY_BY_AUTHOR_ID = "authorUID";

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
    private RecyclerView mRecyclerView;
    private RecyclerView mAuthorRelatedArticlesRecyclerView;

    private TextView mAuthorFullName;
    private TextView mDateView;

    private AuthorRelatedRVAdapter mAuthorRelatedRVAdapter;
    private RelatedArticlesRVAdapter mRelatedArticlesRVAdapter;

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
        relatedArticlesRecyclerViewSetup();
        queryRelatedArticles();
        authorRelatedArticlesRecyclerViewSetUp();
        queryAuthorRelatedArticleData();

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

        mAuthorFullName = (TextView) findViewById(R.id.authorName);
        mDateView = (TextView) findViewById(R.id.dateCreated);


    }

    private void relatedArticlesRecyclerViewSetup()
    {
        mRecyclerView = (RecyclerView) findViewById(R.id.related_articles);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRelatedArticlesRVAdapter = new RelatedArticlesRVAdapter(new ArrayList<Article>(), getApplicationContext());
        mRecyclerView.setAdapter(mRelatedArticlesRVAdapter);
    }

    private void authorRelatedArticlesRecyclerViewSetUp()
    {
        mAuthorRelatedArticlesRecyclerView = (RecyclerView) findViewById(R.id.author_related_articles);
        mAuthorRelatedArticlesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManger = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        mAuthorRelatedArticlesRecyclerView.setLayoutManager(linearLayoutManger);
        mAuthorRelatedRVAdapter = new AuthorRelatedRVAdapter(new ArrayList<Article>(), getApplicationContext());
        mAuthorRelatedArticlesRecyclerView.setAdapter(mAuthorRelatedRVAdapter);
    }

    private void queryRelatedArticles()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ARTICLE_REF);
        Query queryCategory = databaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory);
        queryCategory.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                ArrayList<Article> articleArrayList = new ArrayList<>();
                Article article;
                for (DataSnapshot snapData : dataSnapshot.getChildren())
                {
                    String authorUID = (String) snapData.child("authorUID").getValue();
                    String articleID = (String) snapData.child("articleID").getValue();
                    String authorFname = (String) snapData.child("authorFname").getValue();
                    String authorLname = (String) snapData.child("authorLname").getValue();

                    String category = (String) snapData.child("category").getValue();
                    String lastUpdated = (String) snapData.child("lastUpdated").getValue();
                    long numberOfClicks = (Long) snapData.child("numberOfClicks").getValue();
                    String subTitle = (String) snapData.child("subtitle").getValue();

                    long timeAndDateCreated = (Long) snapData.child("timeAndDateCreated").getValue();
                    String title = (String) snapData.child("title").getValue();
                    String urlToImage = (String) snapData.child("urlToImage").getValue();
                    String articleData = (String) snapData.child("articleData").getValue();

                    article =
                            new Article
                                    (articleID,
                                            authorUID,
                                            authorFname,
                                            authorLname,
                                            title,
                                            subTitle,
                                            articleData,
                                            category,
                                            timeAndDateCreated,
                                            lastUpdated,
                                            urlToImage,
                                            numberOfClicks);

                    Log.d(TAG, "onDataChange: We should be adding to the list by now ");
                    articleArrayList.add(article);
                }
                mRelatedArticlesRVAdapter.loadRelatedArticleData(articleArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void queryAuthorRelatedArticleData()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ARTICLE_REF);
        Query queryCategory = databaseReference.orderByChild(QUERY_BY_AUTHOR_ID).equalTo(mAuthorUID);
        queryCategory.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<Article> articleArrayList = new ArrayList<>();
                Article article;
                for (DataSnapshot snapData : dataSnapshot.getChildren())
                {
                    String authorUID = (String) snapData.child("authorUID").getValue();
                    String articleID = (String) snapData.child("articleID").getValue();
                    String authorFname = (String) snapData.child("authorFname").getValue();
                    String authorLname = (String) snapData.child("authorLname").getValue();

                    String category = (String) snapData.child("category").getValue();
                    String lastUpdated = (String) snapData.child("lastUpdated").getValue();
                    long numberOfClicks = (Long) snapData.child("numberOfClicks").getValue();
                    String subTitle = (String) snapData.child("subtitle").getValue();

                    long timeAndDateCreated = (Long) snapData.child("timeAndDateCreated").getValue();
                    String title = (String) snapData.child("title").getValue();
                    String urlToImage = (String) snapData.child("urlToImage").getValue();
                    String articleData = (String) snapData.child("articleData").getValue();

                    article =
                            new Article
                                    (articleID,
                                            authorUID,
                                            authorFname,
                                            authorLname,
                                            title,
                                            subTitle,
                                            articleData,
                                            category,
                                            timeAndDateCreated,
                                            lastUpdated,
                                            urlToImage,
                                            numberOfClicks);

                    Log.d(TAG, "onDataChange: We should be adding to the list by now ");
                    articleArrayList.add(article);
                }
                mAuthorRelatedRVAdapter.loadArticleData(articleArrayList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
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
        mAuthorFullName.setText(mAuthorFname + " " + mAuthorLname);
        mDateView.setText(mTimeAndDateCreated);
    }
}
