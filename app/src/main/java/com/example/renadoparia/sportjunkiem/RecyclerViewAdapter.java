package com.example.renadoparia.sportjunkiem;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Renado_Paria on 3/24/2017 at 6:54 AM.
 */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ArticleViewHolder>
{
    private static final String TAG = "RecyclerViewAdapter";
    private List<Article> mArticleList;
    private Context mContext;

    private DatabaseReference mDatabaseReference;

    public RecyclerViewAdapter(List<Article> articleList, Context context)
    {
        mArticleList = articleList;
        mContext = context;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //Log.d(TAG, "onCreateViewHolder: new View");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position)
    {
        // Log.d(TAG, "onBindViewHolder: called: ");
        final Article actualArticle = mArticleList.get(position);
        holder.mCategory.setText(actualArticle.getCategory());
        holder.mTitle.setText(actualArticle.getTitle());
        Picasso.with(mContext)
                .load(actualArticle.getUrlToImage())
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.mSportPicture);

        holder.itemView.setOnClickListener(new View.OnClickListener()    //TODO:Implement Call Backs To Clean Up OnBindViewHolder Code
        {
            @Override
            public void onClick(View v)
            {
                String id = actualArticle.getArticleID();
                Log.d(TAG, "onClick: Article Id: " + id);
                updateArticleClicks(id);
                goToActualArticle(actualArticle);
            }
        });
        holder.itemView.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sharedIntent(actualArticle);
            }
        });

        holder.itemView.findViewById(R.id.read_more).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String id = actualArticle.getArticleID();
                updateArticleClicks(id);
                goToActualArticle(actualArticle);
            }
        });
    }

    //    http://stackoverflow.com/questions/4197135/how-to-start-activity-in-adapter
    private void sharedIntent(Article actualArticle)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, actualArticle.getTitle() + " - " + actualArticle.getUrlToImage());
        shareIntent.setType("text/plain");
        mContext.startActivity(Intent.createChooser(shareIntent, "Share With.."));
    }

    //https://firebase.google.com/docs/database/admin/save-data#section-update
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
        Intent fullArticle = new Intent(mContext, ArticleDetailActivity.class);
        fullArticle.putExtra(key, article.toString());
        mContext.startActivity(fullArticle);
    }

    @Override
    public int getItemCount()
    {
        if ((mArticleList != null) && (mArticleList.size() != 0))
        {
            return mArticleList.size();
        }
        else
        {
            return 0;
        }
    }

    void loadArticleData(List<Article> articleList)
    {
        // Log.d(TAG, "loadArticleData: load Article Called " + articleList.toString());
        mArticleList = articleList;
        notifyDataSetChanged();
        //Log.d(TAG, "loadArticleData: load Article Ended: ");
    }

    public Article getArticle(int position)
    {
        if ((mArticleList != null) && (mArticleList.size() != 0))
        {
            return mArticleList.get(position);
        }
        else
        {
            return null;
        }
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder
    {
        ImageView mSportPicture;
        TextView mCategory;
        TextView mTitle;

        ArticleViewHolder(final View itemView)
        {
            super(itemView);
            mSportPicture = (ImageView) itemView.findViewById(R.id.card_image);
            mCategory = (TextView) itemView.findViewById(R.id.card_category);
            mTitle = (TextView) itemView.findViewById(R.id.card_title);
        }
    }
}

