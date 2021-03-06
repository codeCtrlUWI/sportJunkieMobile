package com.example.renadoparia.sportjunkiem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Renado_Paria on 3/27/2017 at 3:41 PM.
 * Hello There
 */

class AuthorRelatedRVAdapter extends RecyclerView.Adapter<AuthorRelatedRVAdapter.AuthorRelatedArticlesViewHolder>
{
    private List<FeaturedArticle> mArticleList;
    private Context mContext;

    AuthorRelatedRVAdapter(List<FeaturedArticle> articleList, Context context)
    {
        mArticleList = articleList;
        mContext = context;
    }

    @Override
    public AuthorRelatedArticlesViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.author_related_articles_card_view, parent, false);
        return new AuthorRelatedArticlesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AuthorRelatedArticlesViewHolder holder, int position)
    {
        FeaturedArticle actualArticle = mArticleList.get(position);
        holder.mCategory.setText(actualArticle.getCategory());
        holder.mTitle.setText(actualArticle.getTitle());
        Glide.with(mContext)
                .load(actualArticle.getUrlToImage())
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.mSportPicture);
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

    void loadArticleData(List<FeaturedArticle> data)
    {
        mArticleList = data;
        notifyDataSetChanged();
    }

    FeaturedArticle getArticle(int position)
    {
        return ((mArticleList != null) && (mArticleList.size() != 0) ? mArticleList.get(position) : null);
    }

    static class AuthorRelatedArticlesViewHolder extends RecyclerView.ViewHolder
    {
        ImageView mSportPicture;
        TextView mCategory;
        TextView mTitle;

        AuthorRelatedArticlesViewHolder(View itemView)
        {
            super(itemView);
            mSportPicture = (ImageView) itemView.findViewById(R.id.related_card_image);
            mCategory = (TextView) itemView.findViewById(R.id.related_card_category);
            mTitle = (TextView) itemView.findViewById(R.id.related_card_title);
        }
    }
}
