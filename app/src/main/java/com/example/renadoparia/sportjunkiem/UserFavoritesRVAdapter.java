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
 * Created by Renado_Paria on 4/2/2017 at 12:10 PM for SportJunkieM.
 */

class UserFavoritesRVAdapter extends RecyclerView.Adapter<UserFavoritesRVAdapter.GridViewHolder>
{
    private static final String TAG = "UserFavoritesRVAdapter";
    private List<FeaturedArticle> mArticleList;
    private Context mContext;

    UserFavoritesRVAdapter(List<FeaturedArticle> articleList, Context context)
    {
        mArticleList = articleList;
        mContext = context;
    }

    @Override
    public GridViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GridViewHolder holder, int position)
    {
        FeaturedArticle actualArticle = mArticleList.get(position);
        holder.title.setText(actualArticle.getTitle());
        Picasso.with(mContext)
                .load(actualArticle.getUrlToImage())
                .error(R.drawable.lgb2_blur)
                .into(holder.backGroundImage);
        /*http://stackoverflow.com/questions/10503728/android-imageview-long-press-and-regular-press*/
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

    void clearData()
    {
        mArticleList.clear();
        notifyDataSetChanged();
    }

    void loadArticleData(List<FeaturedArticle> articleList)
    {
        mArticleList = articleList;
        notifyDataSetChanged();
    }

    FeaturedArticle getArticle(int position)
    {
        return ((mArticleList != null) && (mArticleList.size() != 0) ? mArticleList.get(position) : null);
    }

    static class GridViewHolder extends RecyclerView.ViewHolder
    {
        ImageView backGroundImage;
        TextView title;

        GridViewHolder(View itemView)
        {
            super(itemView);
            backGroundImage = (ImageView) itemView.findViewById(R.id.tile_picture);
            title = (TextView) itemView.findViewById(R.id.tile_title);
        }
    }

}
