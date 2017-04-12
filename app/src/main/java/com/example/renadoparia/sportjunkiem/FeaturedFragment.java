package com.example.renadoparia.sportjunkiem;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Renado on 3/18/2017 at 10:21 PM for SportJunkieM.
 */

public class FeaturedFragment extends Fragment implements ValueEventListener, OnRecyclerClickListener
{

    private static final String TAG = "FeaturedFragment";
    private static final String mArticleRef = "MICRO-ARTICLES";
    private static final String QUERY_ALL_ARTICLES = "numberOfClicks";

    private String mTitle;

    private FirebaseAuth mAuth;
    private static final String USERS_REF = "USERS";
    private static final String FAVORITES_REF = "favorites";

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private DatabaseReference mDatabaseReference;
    private Query queryArticles;
    private String mCategory;

    public FeaturedFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mCategory = getArguments().getString("Tag");
        mTitle = getString(R.string.home);
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
        mRecyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<FeaturedArticle>(), getContext());
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

    @Override
    public void onDataChange(DataSnapshot dataSnapshot)
    {
        ArrayList<FeaturedArticle> featuredArticles = new ArrayList<>();
        FeaturedArticle featuredArticle;
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
        mRecyclerViewAdapter.loadArticleData(featuredArticles);
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
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mArticleRef);

        if (mCategory != null && mCategory.equalsIgnoreCase(HomeActivity.NO_MENU_ITEM_SELECTED))
        {
            queryArticles = mDatabaseReference.orderByChild(QUERY_ALL_ARTICLES).startAt(0).endAt(Long.MAX_VALUE);
            queryArticles.addValueEventListener(this);
            // queryArticles.removeEventListener(this);

        }
        Log.d(TAG, "onResume: called");
    }

    @Override
    public void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause: called");
        queryArticles.removeEventListener(this);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG, "onDestroy: destroyed called");
    }

//    http://stackoverflow.com/questions/33776195/how-to-keep-track-of-listeners-in-firebase-on-android
//    The above is a potential solution
}
