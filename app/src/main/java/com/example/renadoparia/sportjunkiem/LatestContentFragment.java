package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;

/**
 * Created by Renado_Paria on 3/27/2017 at 10:17 PM.
 */

public class LatestContentFragment extends Fragment implements ValueEventListener, OnRecyclerClickListener
{
    private static final String mArticleRef = "MICRO-ARTICLES";

    private DatabaseReference mDatabaseReference;
    private String mCategory;
    private String mTitle;

    private FirebaseAuth mAuth;

    private static final String TAG = "LatestContentFragment";

    private static final String QUERY_BY_AUTHOR_ID = "authorUID";
    private static final String QUERY_BY_CATEGORY = "category";

    private static final String USERS_REF = "USERS";
    private static final String FAVORITES_REF = "favorites";

    private LatestViewRecyclerAdapter mLatestViewAdapter;
    private Query queryArticles;

    public LatestContentFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mCategory = getArguments().getString("Tag");
        mAuth = FirebaseAuth.getInstance();
        if (mCategory != null)
        {
            if (mCategory.equalsIgnoreCase(HomeActivity.NO_MENU_ITEM_SELECTED))
            {
                mTitle = getString(R.string.home);
            }
            else
            {
                mTitle = mCategory;
            }
        }

    }

    @Override
    public void onResume()
    {
        super.onResume();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mArticleRef);
        if (mCategory != null)
        {
            if (mCategory.equalsIgnoreCase(HomeActivity.NO_MENU_ITEM_SELECTED))
            {
                queryArticles = mDatabaseReference.orderByChild("articleID").limitToFirst(100);
                queryArticles.addValueEventListener(this);
            }
            else
            {
                queryArticles = mDatabaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory);
                queryArticles.addValueEventListener(this);
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(mTitle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnItemTouchListener(new RecyclerItemClicker(getContext(), recyclerView, this));

        mLatestViewAdapter = new LatestViewRecyclerAdapter(new ArrayList<FeaturedArticle>(), getContext());
        recyclerView.setAdapter(mLatestViewAdapter);

        return recyclerView;

    }

    @Override
    public void onItemClick(View view, int position)
    {
        FeaturedArticle article = mLatestViewAdapter.getArticle(position);
        updateArticleClicks(article.getArticleID());
        updateMainArticles(article.getArticleID());
        goToActualArticle(article);
    }

    @Override
    public void onItemLongClick(View view, int position)
    {
        FeaturedArticle article = mLatestViewAdapter.getArticle(position);
        sharedIntent(article);
    }

    @Override
    public void onItemDoubleTap(View view, int position)
    {
        FeaturedArticle article = mLatestViewAdapter.getArticle(position);
        updateFav(article.getArticleID(), view);
    }

    private void sharedIntent(FeaturedArticle actualArticle)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, actualArticle.getTitle() + " - " + "https://sjapp-4c72a.firebaseapp.com/#/" + actualArticle.getCategory().toLowerCase() + "/view/" + actualArticle.getArticleID());
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
        mLatestViewAdapter.loadArticleData(featuredArticles);
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }

    private void goToActualArticle(FeaturedArticle article)
    {
        final String key = "articledata";
        Intent fullArticle = new Intent(getContext(), ArticleDetailActivity.class);
        fullArticle.putExtra(key, article.getArticleID());
        startActivity(fullArticle);
    }

    @Override
    public void onStop()
    {
        super.onStop();

    }

    private void updateMainArticles(String id)
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

    @Override
    public void onPause()
    {
        super.onPause();
       queryArticles.removeEventListener(this);
    }
}
