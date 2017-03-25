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
    private static final String mArticleRef = "ARTICLES";
    private static final String QUERY_BY_CATEGORY = "category";

    private RecyclerViewAdapter mRecyclerViewAdapter;


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

        Query queryCategory = databaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory);
        Log.d(TAG, "onCreate: query: " + queryCategory.toString());
        queryCategory.addValueEventListener(this);
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

        mRecyclerViewAdapter = new RecyclerViewAdapter(new ArrayList<Article>(), getContext());
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
        Collections.sort(articleArrayList, new Comparator<Article>()
        {
            @Override
            public int compare(Article o1, Article o2)
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
        mRecyclerViewAdapter.loadArticleData(articleArrayList);
        Log.d(TAG, "onDataChange: list: " + articleArrayList.toString());
        Log.d(TAG, "onDataChange: CURRENT SIZE OF LIST: " + articleArrayList.size());
    }


    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }
}