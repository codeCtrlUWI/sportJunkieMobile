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
 * Created by Renado_Paria on 3/27/2017 at 10:41 PM for SportJunkieM.
 */

class LatestViewRecyclerAdapter extends RecyclerView.Adapter<LatestViewRecyclerAdapter.ListViewHolder>
{
    private List<Article> mArticlesList;
    private Context mContext;

    public LatestViewRecyclerAdapter(List<Article> articlesList, Context context)
    {
        mArticlesList = articlesList;
        mContext = context;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.latest_content, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ListViewHolder holder, int position)
    {

        Article actualArticle = mArticlesList.get(position);

        String timeAndDateString = actualArticle.getTimeAndDateCreated();
        holder.mTitle.setText(actualArticle.getTitle());
        holder.mDateView.setText(timeAndDateString);
        Picasso.with(mContext)
                .load(actualArticle.getUrlToImage())
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.sportPicture);
    }

    @Override
    public int getItemCount()
    {
        if ((mArticlesList != null) && (mArticlesList.size() != 0))
        {
            return mArticlesList.size();
        }
        else
        {
            return 0;
        }
    }

    void loadArticleData(List<Article> data)
    {
        mArticlesList = data;
        notifyDataSetChanged();
    }

    public Article getArticle(int position)
    {
        return ((mArticlesList != null) && (mArticlesList.size() != 0) ? mArticlesList.get(position) : null);
    }

    static class ListViewHolder extends RecyclerView.ViewHolder
    {
        ImageView sportPicture;
        TextView mTitle;
        TextView mDateView;

        ListViewHolder(View itemView)
        {
            super(itemView);
            sportPicture = (ImageView) itemView.findViewById(R.id.smol_sport_picture);
            mTitle = (TextView) itemView.findViewById(R.id.article_title);
            mDateView = (TextView) itemView.findViewById(R.id.time_date_view);
        }
    }
}
