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
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Renado_Paria on 3/20/2017 at 4:26 AM.
 */

public class FeaturedCategoryFragment extends Fragment implements ValueEventListener
{
    private static final String TAG = "FeaturedCategoryFragmen";
    private String mCategory;
    private static final String mArticleRef = "MICRO-ARTICLES";
    private static final String QUERY_BY_CATEGORY = "category";

    private RecyclerViewAdapter mRecyclerViewAdapter;
    private Query queryCategory;


    public FeaturedCategoryFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(mArticleRef);
        mCategory = getArguments().getString("Tag");
        Log.d(TAG, "onCreate: tag: " + mCategory);

        queryCategory = databaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory);
        Log.d(TAG, "onCreate: query: " + queryCategory.toString());
        queryCategory.addListenerForSingleValueEvent(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView: here we are");
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<FeaturedArticle>(), getContext());
        recyclerView.setAdapter(mRecyclerViewAdapter);

        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(mCategory);
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
        Collections.sort(featuredArticles, new Comparator<FeaturedArticle>()
        {
            @Override
            public int compare(FeaturedArticle o1, FeaturedArticle o2)
            {
                if (o1.getNumberOfClicks() < o2.getNumberOfClicks())
                {
                    return 1;
                }
                else
                {
                    return -1;
                }
            }
        });
        mRecyclerViewAdapter.loadArticleData(featuredArticles);
        Log.d(TAG, "onDataChange: list: " + featuredArticles.toString());
        Log.d(TAG, "onDataChange: CURRENT SIZE OF LIST: " + featuredArticles.size());
    }


    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }

    @Override
    public void onStop()
    {
        super.onStop();
        queryCategory.removeEventListener(this);
    }
}