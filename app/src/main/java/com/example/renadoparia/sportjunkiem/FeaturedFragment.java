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
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

/**
 * Created by Renado_Paria on 3/18/2017 at 5:29 AM.
 */

public class FeaturedFragment extends Fragment
{

    private static final String TAG = "FeaturedFragment";

    private static final String mArticleRef = "ARTICLES";
    private static final String QUERY_ALL_ARTICLES = "author";//Used author, but i think you could use any field
    private static final String QUERY_BY_CATEGORY = "category";
    private DatabaseReference mDatabaseReference;
    private static FirebaseRecyclerAdapter<Article, ArticleViewHolder> mFireBaseRecyclerAdapter;
    private String mCategory;
    private String mTitle;

    public FeaturedFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(mArticleRef);
        mCategory = getArguments().getString("Tag");
        Log.d(TAG, "onCreate: tag: " + mCategory);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreateView: starts");
        Query queryArticles;
        if (mCategory.equalsIgnoreCase(HomeActivity.NO_MENU_ITEM_SELECTED))
        {
            queryArticles = mDatabaseReference.orderByChild(QUERY_ALL_ARTICLES);
            mTitle = getString(R.string.home);
        }
        else
        {
            queryArticles = mDatabaseReference.orderByChild(QUERY_BY_CATEGORY).equalTo(mCategory);
            mTitle = mCategory;
        }
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        mFireBaseRecyclerAdapter = new FirebaseRecyclerAdapter<Article, ArticleViewHolder>(
                Article.class,
                R.layout.card_view,
                ArticleViewHolder.class,
                queryArticles)
        {
            @Override
            protected void populateViewHolder(ArticleViewHolder viewHolder, Article model, int position)
            {
                viewHolder.mCategory.setText(model.getCategory());
                viewHolder.mTitle.setText(model.getTitle());
                Picasso.with(getContext())
                        .load(model.getUrlToImage())
                        .error(R.drawable.ic_image_black_48dp)
                        .into(viewHolder.mSportPicture);
            }
        };
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mFireBaseRecyclerAdapter);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        getActivity().setTitle(mTitle);
    }

    private static class ArticleViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView mSportPicture;
        private TextView mCategory;
        private TextView mTitle;

        public ArticleViewHolder(View itemView)
        {
            super(itemView);
            mSportPicture = (ImageView) itemView.findViewById(R.id.card_image);
            mCategory = (TextView) itemView.findViewById(R.id.card_category);
            mTitle = (TextView) itemView.findViewById(R.id.card_title);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Log.d(TAG, "onClick: " + mFireBaseRecyclerAdapter.getItem(getAdapterPosition()).toString());
                }
            });
        }

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
        Log.d(TAG, "onDestroy: destroyed call");
    }
}
