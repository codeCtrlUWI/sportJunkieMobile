package com.example.renadoparia.sportjunkiem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
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
 * Created by Renado_Paria on 3/27/2017 at 10:17 PM.
 */

public class LatestContentFragment extends Fragment implements ValueEventListener
{
    private static final String mArticleRef = "ARTICLES";

    private DatabaseReference mDatabaseReference;
    private String mCategory;
    private String mTitle;

    private static final String TAG = "LatestContentFragment";

    private static final String QUERY_BY_AUTHOR_ID = "authorUID";
    private static final String QUERY_BY_CATEGORY = "category";

    private LatestViewRecyclerAdapter mLatestViewAdapter;

    public LatestContentFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mArticleRef);
        mCategory = getArguments().getString("Tag");
        Query queryArticles;
        if (mCategory != null)
        {
            if (mCategory.equalsIgnoreCase(HomeActivity.NO_MENU_ITEM_SELECTED))
            {
                queryArticles = mDatabaseReference.orderByChild(QUERY_BY_AUTHOR_ID).limitToFirst(100);
                queryArticles.addValueEventListener(this);
                mTitle = getString(R.string.home);
            }
            else
            {
                queryArticles = mDatabaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory);
                queryArticles.addValueEventListener(this);
                mTitle = mCategory;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        mLatestViewAdapter = new LatestViewRecyclerAdapter(new ArrayList<Article>(), getContext());
        recyclerView.setAdapter(mLatestViewAdapter);

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
        mLatestViewAdapter.loadArticleData(articleArrayList);
    }

    @Override
    public void onCancelled(DatabaseError databaseError)
    {

    }
}
