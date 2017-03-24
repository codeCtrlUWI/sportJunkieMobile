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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Renado_Paria on 3/18/2017 at 5:29 AM.
 */

public class FeaturedFragment extends Fragment implements ValueEventListener
{

    private static final String TAG = "FeaturedFragment";
    private static final String mArticleRef = "ARTICLES";
    private static final String QUERY_ALL_ARTICLES = "numberOfClicks";

    private DatabaseReference mDatabaseReference;
    private String mCategory;
    private String mTitle;

    private RecyclerViewAdapter mRecyclerViewAdapter;


    public FeaturedFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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
        mRecyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Article>(), getContext());
        recyclerView.setAdapter(mRecyclerViewAdapter);

        return recyclerView;
    }

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
