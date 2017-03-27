package com.example.renadoparia.sportjunkiem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Renado_Paria on 3/26/2017 at 9:29 PM.
 */

class RelatedArticlesRVAdapter extends RecyclerView.Adapter<RelatedArticlesRVAdapter.RelatedArticleViewHolder>
{
    private static final String TAG = "RelatedArticlesRVAdapte";
    private List<Article> mArticleList;
    private Context mContext;

    RelatedArticlesRVAdapter(List<Article> articleList, Context context)
    {
        mArticleList = articleList;
        mContext = context;
    }

    @Override
    public RelatedArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.related_articles_view, parent, false);
        return new RelatedArticleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelatedArticleViewHolder holder, int position)
    {
        Article actualArticle = mArticleList.get(position);
        holder.mTextView.setText(actualArticle.getTitle());
        Picasso.with(mContext)
                .load(actualArticle.getUrlToImage())
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.mImageView);
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

    void loadRelatedArticleData(List<Article> data)
    {
        mArticleList = data;
        notifyDataSetChanged();
    }

    static class RelatedArticleViewHolder extends RecyclerView.ViewHolder
    {
        ImageView mImageView;
        TextView mTextView;

        RelatedArticleViewHolder(View itemView)
        {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.related_image_view);
            mTextView = (TextView) itemView.findViewById(R.id.related_text_view);
        }
    }

}
