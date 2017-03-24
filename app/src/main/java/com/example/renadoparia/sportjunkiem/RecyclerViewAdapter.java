package com.example.renadoparia.sportjunkiem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public RecyclerViewAdapter(List<Article> articleList, Context context)
    {
        mArticleList = articleList;
        mContext = context;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        Log.d(TAG, "onCreateViewHolder: new View");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position)
    {
        Log.d(TAG, "onBindViewHolder: called: ");
        Article actualArticle = mArticleList.get(position);
        holder.mCategory.setText(actualArticle.getCategory());
        holder.mTitle.setText(actualArticle.getTitle());
        Picasso.with(mContext)
                .load(actualArticle.getUrlToImage())
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.mSportPicture);

    }

    @Override
    public int getItemCount()
    {
        Log.d(TAG, "getItemCount: called");
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
        Log.d(TAG, "loadArticleData: load Article Called " + articleList.toString());
        mArticleList = articleList;
        notifyDataSetChanged();
        Log.d(TAG, "loadArticleData: load Article Ended: ");
    }


    static class ArticleViewHolder extends RecyclerView.ViewHolder
    {
        ImageView mSportPicture;
        TextView mCategory;
        TextView mTitle;

        public ArticleViewHolder(final View itemView)
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
                    Log.d(TAG, "onClick: Article Has Been Clicked ");
                }
            });
        }

    }
}

