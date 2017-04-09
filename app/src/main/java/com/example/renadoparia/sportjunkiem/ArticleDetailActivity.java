package com.example.renadoparia.sportjunkiem;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import java.util.ArrayList;

public class ArticleDetailActivity extends AppCompatActivity implements OnRecyclerClickListener, ValueEventListener, View.OnClickListener
{
    private static final String TAG = "ArticleDetailActivity";
    private static final String KEY = "articledata";
    private static final String ARTICLE_REF = "ARTICLES";
    private static final String QUERY_BY_CATEGORY = "category";
    private static final String QUERY_BY_AUTHOR_ID = "authorUID";
    private static final String USERS_REF = "USERS";
    private static final String FAVORITES_REF = "favorites";
    private static final String GALLERY_REF = "GALLERY";

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
    private String mGalleryID;

    private CollapsingToolbarLayout mCollapsingToolbar;

    //    private ImageSwitcher mImageSwitcher;
    private TextView mTitleArticle;
    private TextView mSubTitleArticle;
    private TextView mActualArticle;
    private ImageView mCoverPhoto;
    private ImageView mAuthorProfilePic;

    private TextView mAuthorFullName;
    private TextView mDateView;

    private AuthorRelatedRVAdapter mAuthorRelatedRVAdapter;
    private RelatedArticlesRVAdapter mRelatedArticlesRVAdapter;
    private GalleryRVAdapter mGalleryAdapter;

    private FirebaseAuth mAuth;
    private Query query;

    private Article mArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: Starts");
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


        final String articleID = getIntent().getStringExtra(KEY);
        Log.d(TAG, "onCreate: article data: " + articleID);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child(ARTICLE_REF);
        query = databaseRef.child(articleID);
        query.addListenerForSingleValueEvent(this);

//        else if (mGalleryID == null)
//        {
//            RecyclerView galleryRecyclerView = (RecyclerView) findViewById(R.id.gallery_recyclerview);
//            galleryRecyclerView.setVisibility(View.GONE);
//            TextView galleryWarning = (TextView) findViewById(R.id.gallery_warning);
//            galleryWarning.setVisibility(View.VISIBLE);
//        }
        Log.d(TAG, "onCreate: ends");
    }

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        mArticle = dataSnapshot.getValue(Article.class);
        mAuthorUID = mArticle.getAuthorUID();
        mArticleData = mArticle.getArticleData();
        mAuthorLname = mArticle.getAuthorLname();
        mAuthorFname = mArticle.getAuthorFname();
        mTitle = mArticle.getTitle();
        mSubtitle = mArticle.getSubtitle();
        mCategory = mArticle.getCategory();
        mTimeAndDateCreated = mArticle.getTimeAndDateCreated();
        mGalleryID = mArticle.getGalleryID();
        mUrlToImage = mArticle.getUrlToImage();

        setUpArticleFluff();
        relatedArticlesRecyclerViewSetup();
        queryRelatedArticles();
        authorRelatedArticlesRecyclerViewSetUp();
        queryAuthorRelatedArticleData();
        if (mGalleryID != null)
        {
            galleryRecyclerViewSetUp();
            queryGallery();
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }

    private void initWidgets()
    {
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingtoolbar);
        mCoverPhoto = (ImageView) findViewById(R.id.dexters_blood_slides);
        mTitleArticle = (TextView) findViewById(R.id.title);
        mSubTitleArticle = (TextView) findViewById(R.id.subtitle);
        mActualArticle = (TextView) findViewById(R.id.articleData);

        mAuthorFullName = (TextView) findViewById(R.id.authorName);
        mDateView = (TextView) findViewById(R.id.dateCreated);
        mAuthorProfilePic = (ImageView) findViewById(R.id.authorProfilePicture);
        FloatingActionButton homeFab = (FloatingActionButton) findViewById(R.id.homefab);
        //Maybe we could just set the listener, let's try below
        findViewById(R.id.sharefab).setOnClickListener(this);
        homeFab.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        if (id == R.id.homefab)
        {
            startActivity(new Intent(this, HomeActivity.class));
        }
        if (id == R.id.sharefab)
        {
            sharedIntent();
        }
    }

    private void relatedArticlesRecyclerViewSetup()
    {
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.related_articles);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerItemClicker(getApplicationContext(), recyclerView, this));
        mRelatedArticlesRVAdapter = new RelatedArticlesRVAdapter(new ArrayList<FeaturedArticle>(), getApplicationContext());
        recyclerView.setAdapter(mRelatedArticlesRVAdapter);
    }

    private void authorRelatedArticlesRecyclerViewSetUp()
    {
        RecyclerView authorRelatedArticlesRecyclerView = (RecyclerView) findViewById(R.id.author_related_articles);
        authorRelatedArticlesRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManger = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        authorRelatedArticlesRecyclerView.setLayoutManager(linearLayoutManger);
        authorRelatedArticlesRecyclerView.addOnItemTouchListener(new RecyclerItemClicker(getApplicationContext(), authorRelatedArticlesRecyclerView, this));
        mAuthorRelatedRVAdapter = new AuthorRelatedRVAdapter(new ArrayList<FeaturedArticle>(), getApplicationContext());
        authorRelatedArticlesRecyclerView.setAdapter(mAuthorRelatedRVAdapter);
    }

    private void galleryRecyclerViewSetUp()
    {
        RecyclerView galleryRecyclerView = (RecyclerView) findViewById(R.id.gallery_recyclerview);
        galleryRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManger = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        galleryRecyclerView.setLayoutManager(linearLayoutManger);
        galleryRecyclerView.addOnItemTouchListener(new RecyclerItemClicker(getApplicationContext(), galleryRecyclerView, this));
        mGalleryAdapter = new GalleryRVAdapter(new ArrayList<String>(), getApplicationContext());
        galleryRecyclerView.setAdapter(mGalleryAdapter);

    }

    private void queryRelatedArticles()
    {
        final String microArticleRef = "MICRO-ARTICLES";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(microArticleRef);
        Query queryCategory = databaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory).limitToLast(3);
        queryCategory.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<FeaturedArticle> featuredArticles = new ArrayList<>();
                FeaturedArticle featuredArticle = null;
                for (DataSnapshot snapData : dataSnapshot.getChildren())
                {
                    Log.d(TAG, "onDataChange: data: " + snapData.toString());
                    String articleID = snapData.child("articleID").getValue().toString();
                    String title = snapData.child("title").getValue().toString();
                    String category = snapData.child("category").getValue().toString();
                    String urlToImage = snapData.child("urlToImage").getValue().toString();
                    Long numberOFClicks = (Long) snapData.child("numberOfClicks").getValue();

                    featuredArticle = new FeaturedArticle(articleID, title, category, urlToImage, numberOFClicks);
                    featuredArticles.add(featuredArticle);

                }
                mRelatedArticlesRVAdapter.loadRelatedArticleData(featuredArticles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void queryAuthorRelatedArticleData()
    {
        final String microArticleRef = "MICRO-ARTICLES";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(microArticleRef);
        Query queryCategory = databaseReference.orderByChild(QUERY_BY_AUTHOR_ID).equalTo(mAuthorUID).limitToLast(5);
        queryCategory.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<FeaturedArticle> featuredArticles = new ArrayList<>();
                FeaturedArticle featuredArticle = null;
                for (DataSnapshot snapData : dataSnapshot.getChildren())
                {
                    Log.d(TAG, "onDataChange: data: " + snapData.toString());
                    String articleID = snapData.child("articleID").getValue().toString();
                    String authorUID = snapData.child("authorUID").getValue().toString();
                    String title = snapData.child("title").getValue().toString();
                    String category = snapData.child("category").getValue().toString();
                    String urlToImage = snapData.child("urlToImage").getValue().toString();
                    Long numberOFClicks = (Long) snapData.child("numberOfClicks").getValue();

                    featuredArticle = new FeaturedArticle(articleID, authorUID, title, category, urlToImage, numberOFClicks);
                    featuredArticles.add(featuredArticle);

                }
                mAuthorRelatedRVAdapter.loadArticleData(featuredArticles);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void queryGallery()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(GALLERY_REF).child(mGalleryID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                GenericTypeIndicator<ArrayList<String>> arrayListGenericTypeIndicator
                        = new GenericTypeIndicator<ArrayList<String>>()
                {
                };
                if (dataSnapshot.getValue() != null)
                {
                    ArrayList<String> listOfGalleryImages = dataSnapshot.getValue(arrayListGenericTypeIndicator);
                    Log.d(TAG, "onDataChange: list of images = " + listOfGalleryImages.toString());
                    mGalleryAdapter.LoadPhotos(listOfGalleryImages);
                }
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
        Log.d(TAG, "onResume: starts");

        Log.d(TAG, "onResume: ends");
    }

    private void setUpArticleFluff()
    {
        mCollapsingToolbar.setTitle(mCategory);
        mTitleArticle.setText(mTitle);
        mSubTitleArticle.setText(mSubtitle);
        mActualArticle.setText(mArticleData);
        Glide.with(getApplicationContext())
                .load(mUrlToImage)
                .error(R.drawable.ic_image_black_48dp)
                .into(mCoverPhoto);
        mAuthorFullName.setText(mAuthorFname + " " + mAuthorLname);
        mDateView.setText(mTimeAndDateCreated);
        setUpAuthorPic();
    }

    private void setUpAuthorPic()
    {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("USERS").child(mAuthorUID).child("profilePicURL");
        usersRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Glide.with(getApplicationContext())
                        .load(dataSnapshot.getValue().toString())
                        .fitCenter()
                        .crossFade()
                        .into(mAuthorProfilePic);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.author_related_articles_card_view:
                FeaturedArticle article = mAuthorRelatedRVAdapter.getArticle(position);
                updateArticleClicks(article.getArticleID());
                goToActualArticle(article);
                break;
            case R.id.related_articles_view:
                FeaturedArticle related_article = mRelatedArticlesRVAdapter.getArticle(position);
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
                FeaturedArticle article = mAuthorRelatedRVAdapter.getArticle(position);
                sharedIntent(article);
                break;
            case R.id.related_articles_view:
                FeaturedArticle related_article = mRelatedArticlesRVAdapter.getArticle(position);
                sharedIntent(related_article);
                break;
            case R.id.gallery_imagecard_view:
                String urlToImage = mGalleryAdapter.getPhotoURL(position);
                Dialog builder = new Dialog(this);
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                ImageView imageView = new ImageView(this);
                Picasso.with(getApplicationContext()).load(urlToImage).error(R.drawable.ic_image_black_48dp).into(imageView);
                builder.addContentView(imageView, new RelativeLayout.LayoutParams
                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                builder.show();
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
                FeaturedArticle article = mAuthorRelatedRVAdapter.getArticle(position);
                updateFav(article.getArticleID(), view);
                break;
            case R.id.related_articles_view:
                FeaturedArticle related_article = mRelatedArticlesRVAdapter.getArticle(position);
                updateFav(related_article.getArticleID(), view);
                break;
            case R.id.gallery_imagecard_view:
                Snackbar.make(view, "Double Tapped The Image", Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    private void sharedIntent(FeaturedArticle actualArticle)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra
                (Intent.EXTRA_TEXT, actualArticle.getTitle() + " - " + "https://sjapp-4c72a.firebaseapp.com/#/" +
                        actualArticle.getCategory().toLowerCase() + "/view/" + actualArticle.getArticleID());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share With.."));
    }

    private void sharedIntent()
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra
                (Intent.EXTRA_TEXT, mArticle.getTitle() + " - " + "https://sjapp-4c72a.firebaseapp.com/#/" +
                        mArticle.getCategory().toLowerCase() + "/view/" + mArticle.getArticleID());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share With.."));
    }

    private void updateFav(final String articleID, final View view)
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
                        Snackbar.make(view, "Added To Favorites", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (dataSnapshot.getValue() != null)
                    {
                        ArrayList<String> listOfFavorites = dataSnapshot.getValue(arrayListGenericTypeIndicator);

                        if (listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.remove(articleID);
                            test.setValue(listOfFavorites);
                            Snackbar.make(view, "Removed From Favorites", Snackbar.LENGTH_SHORT).show();
                        }
                        else if (!listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.add(articleID);
                            test.setValue(listOfFavorites);
                            Snackbar.make(view, "Added To Favorites", Snackbar.LENGTH_SHORT).show();
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
        final String articleRef = "MICRO-ARTICLES";
        final String numClicksRef = "numberOfClicks";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(articleRef).child(id).child(numClicksRef);
        databaseReference.runTransaction(new Transaction.Handler()
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
        Log.d(TAG, "updateArticleClicks: " + databaseReference.toString());
    }

    private void goToActualArticle(FeaturedArticle article)
    {
        final String key = "articledata";
        Intent fullArticle = new Intent(getApplicationContext(), ArticleDetailActivity.class);
        fullArticle.putExtra(key, article.getArticleID());
        // fullArticle.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(fullArticle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.article_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.home)
        {
            startActivity(new Intent(this, HomeActivity.class));
        }
        if (id == R.id.profile)
        {
            startActivity(new Intent(this, ProfileActivity.class));
        }
        if (id == R.id.share)
        {
            sharedIntent();
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStop()
    {
        super.onStop();
        query.removeEventListener(this);
    }
}


//        try
//        {
//            JSONObject articleJsonObject = new JSONObject(articleData);
//            mArticleId = articleJsonObject.getString("mArticleID");
//            mAuthorUID = articleJsonObject.getString("mAuthorUID");
//            mAuthorFname = articleJsonObject.getString("mAuthorFname");
//            mAuthorLname = articleJsonObject.getString("mAuthorLname");
//            mTitle = articleJsonObject.getString("mTitle");
//            mSubtitle = articleJsonObject.getString("mSubtitle");
//            mArticleData = articleJsonObject.getString("mArticleData");
//            mCategory = articleJsonObject.getString("mCategory");
//            mTimeAndDateCreated = articleJsonObject.getString("mTimeAndDateCreated");
//            mLastUpdated = articleJsonObject.getString("mLastUpdated");
//            mUrlToImage = articleJsonObject.getString("mUrlToImage");
//            mGalleryID = articleJsonObject.getString("mGalleryID");
//
//        }
//        catch (JSONException e)
//        {
//            e.printStackTrace();
//        }