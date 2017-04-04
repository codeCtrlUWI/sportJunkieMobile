package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ArticleDetailActivity extends AppCompatActivity implements OnRecyclerClickListener
{
    private static final String TAG = "ArticleDetailActivity";
    private static final String KEY = "articledata";
    private static final String ARTICLE_REF = "ARTICLES";
    private static final String QUERY_BY_CATEGORY = "category";
    private static final String QUERY_BY_AUTHOR_ID = "authorUID";
    private static final String USERS_REF = "USERS";
    private static final String FAVORITES_REF = "favorites";

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

    private TextView mAuthorFullName;
    private TextView mDateView;

    private AuthorRelatedRVAdapter mAuthorRelatedRVAdapter;
    private RelatedArticlesRVAdapter mRelatedArticlesRVAdapter;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        initWidgets();
        mAuth = FirebaseAuth.getInstance();

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
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.related_articles);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerItemClicker(getApplicationContext(), recyclerView, this));
        mRelatedArticlesRVAdapter = new RelatedArticlesRVAdapter(new ArrayList<Article>(), getApplicationContext());
        recyclerView.setAdapter(mRelatedArticlesRVAdapter);
    }

    private void authorRelatedArticlesRecyclerViewSetUp()
    {
        RecyclerView authorRelatedArticlesRecyclerView = (RecyclerView) findViewById(R.id.author_related_articles);
        authorRelatedArticlesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManger = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        authorRelatedArticlesRecyclerView.setLayoutManager(linearLayoutManger);
        authorRelatedArticlesRecyclerView.addOnItemTouchListener(new RecyclerItemClicker(getApplicationContext(), authorRelatedArticlesRecyclerView, this));
        mAuthorRelatedRVAdapter = new AuthorRelatedRVAdapter(new ArrayList<Article>(), getApplicationContext());
        authorRelatedArticlesRecyclerView.setAdapter(mAuthorRelatedRVAdapter);
    }

    private void queryRelatedArticles()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(ARTICLE_REF);
        Query queryCategory = databaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory).limitToLast(5);
        queryCategory.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                ArrayList<Article> articleArrayList = new ArrayList<>();
                Article actualArticle;
                for (DataSnapshot snapData : dataSnapshot.getChildren())
                {
                    actualArticle = snapData.getValue(Article.class);
                    articleArrayList.add(actualArticle);
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
        Query queryCategory = databaseReference.orderByChild(QUERY_BY_AUTHOR_ID).equalTo(mAuthorUID).limitToFirst(10);
        queryCategory.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<Article> articleArrayList = new ArrayList<>();
                Article actualArticle;
                for (DataSnapshot snapData : dataSnapshot.getChildren())
                {
                    actualArticle = snapData.getValue(Article.class);
                    articleArrayList.add(actualArticle);
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

    @Override
    public void onItemClick(View view, int position)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.author_related_articles_card_view:
                Article article = mAuthorRelatedRVAdapter.getArticle(position);
                updateArticleClicks(article.getArticleID());
                goToActualArticle(article);
                break;
            case R.id.related_articles_view:
                Article related_article = mRelatedArticlesRVAdapter.getArticle(position);
                updateArticleClicks(related_article.getArticleID());
                goToActualArticle(related_article);
                break;
        }
    }

    @Override
    public void onItemLongClick(View view, int position)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.author_related_articles_card_view:
                Article article = mAuthorRelatedRVAdapter.getArticle(position);
                sharedIntent(article);
                break;
            case R.id.related_articles_view:
                Article related_article = mRelatedArticlesRVAdapter.getArticle(position);
                sharedIntent(related_article);
                break;
        }
    }

    @Override
    public void onItemDoubleTap(View view, int position)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.author_related_articles_card_view:
                Article article = mAuthorRelatedRVAdapter.getArticle(position);
                updateFav(article.getArticleID());
                break;
            case R.id.related_articles_view:
                Article related_article = mRelatedArticlesRVAdapter.getArticle(position);
                updateFav(related_article.getArticleID());
                break;
        }
    }

    private void sharedIntent(Article actualArticle)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, actualArticle.getTitle() + " - " + actualArticle.getUrlToImage());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share With.."));
    }

    private void updateFav(final String articleID)
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String UIDOfCurrentUser;
        if (firebaseUser != null)
        {
            UIDOfCurrentUser = firebaseUser.getUid();

            final DatabaseReference test = FirebaseDatabase.getInstance().getReference()
                    .child(USERS_REF)
                    .child(UIDOfCurrentUser)
                    .child(FAVORITES_REF);
            test.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                    GenericTypeIndicator<ArrayList<String>> arrayListGenericTypeIndicator
                            = new GenericTypeIndicator<ArrayList<String>>()
                    {
                    };
                    if (dataSnapshot.getValue() == null)
                    {
                        ArrayList<String> oneTimeInit = new ArrayList<>();
                        oneTimeInit.add(articleID);
                        test.setValue(oneTimeInit);
                    }
                    else if (dataSnapshot.getValue() != null)
                    {
                        ArrayList<String> listOfFavorites = dataSnapshot.getValue(arrayListGenericTypeIndicator);

                        if (listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.remove(articleID);
                            test.setValue(listOfFavorites);
                        }
                        else if (!listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.add(articleID);
                            test.setValue(listOfFavorites);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

    private void updateArticleClicks(String id)
    {
        final String articleRef = "ARTICLES";
        final String numClicksRef = "numberOfClicks";
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(articleRef).child(id).child(numClicksRef);
        mDatabaseReference.runTransaction(new Transaction.Handler()
        {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData)
            {
                Long currentValue = mutableData.getValue(Long.class);
                if (currentValue == null || currentValue < 0)
                {
                    mutableData.setValue(1);
                }
                else
                {
                    mutableData.setValue(currentValue + 1);
                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "onComplete: Transaction Completed");
            }
        });
        Log.d(TAG, "updateArticleClicks: " + mDatabaseReference.toString());
    }

    private void goToActualArticle(Article article)
    {
        final String key = "articledata";
        Intent fullArticle = new Intent(getApplicationContext(), ArticleDetailActivity.class);
        fullArticle.putExtra(key, article.toString());
        startActivity(fullArticle);
    }
}
