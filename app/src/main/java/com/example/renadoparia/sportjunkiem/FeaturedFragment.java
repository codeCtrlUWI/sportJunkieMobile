package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * Created by Renado on 3/18/2017 at 10:21 PM for SportJunkieM.
 */

public class FeaturedFragment extends Fragment implements ValueEventListener, OnRecyclerClickListener
{

    private static final String TAG = "FeaturedFragment";
    private static final String mArticleRef = "ARTICLES";
    private static final String QUERY_ALL_ARTICLES = "numberOfClicks";

    private DatabaseReference mDatabaseReference;
    private String mCategory;
    private String mTitle;

    private FirebaseAuth mAuth;
    private static final String USERS_REF = "USERS";
    private static final String FAVORITES_REF = "favorites";

    private RecyclerViewAdapter mRecyclerViewAdapter;

    public FeaturedFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mArticleRef);
        mCategory = getArguments().getString("Tag");
        Query queryArticles;

        if (mCategory != null && mCategory.equalsIgnoreCase(HomeActivity.NO_MENU_ITEM_SELECTED))
        {
            queryArticles = mDatabaseReference.orderByChild(QUERY_ALL_ARTICLES).startAt(0).endAt(Long.MAX_VALUE);
            queryArticles.addValueEventListener(this);
            mTitle = getString(R.string.home);
        }
        Log.d(TAG, "onCreate: tag: " + mCategory);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        /*THIS BELOW SECTION IS FOR TESTING PURPOSES ALONE*/

//        Query queryArticles;
//        Log.d(TAG, "onCreateView: Hello From Testing Section ");
//        Log.d(TAG, "onCreateView: category: " + mCategory);
//        queryArticles = mDatabaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory);
//        queryArticles.addValueEventListener(this);
//        Log.d(TAG, "onCreateView: testQuery: " + queryArticles);
        /*THIS ABOVE SECTION IS FOR TESTING ALONE*/

        Log.d(TAG, "onCreateView: starts");


        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addOnItemTouchListener(new RecyclerItemClicker(getContext(), recyclerView, this));
        mRecyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Article>(), getContext());
        recyclerView.setAdapter(mRecyclerViewAdapter);

        return recyclerView;
    }

    @Override
    public void onItemClick(View view, int position)
    {

    }

    @Override
    public void onItemLongClick(View view, int position)
    {

    }

    @Override
    public void onItemDoubleTap(View view, int position)
    {

    }

    private void goToActualArticle(Article article)
    {
        final String key = "articledata";
        Intent fullArticle = new Intent(getContext(), ArticleDetailActivity.class);
        fullArticle.putExtra(key, article.toString());
        startActivity(fullArticle);
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
        mRecyclerViewAdapter.loadArticleData(articleArrayList);
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(mTitle);
    }


    @Override
    public void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart: called");
    }

    @Override
    public void onStop()
    {
        super.onStop();
        Log.d(TAG, "onStop: called");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy: destroyed called");
    }
}
