package com.example.renadoparia.sportjunkiem;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Renado_Paria on 3/24/2017 at 6:54 AM.
 */

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ArticleViewHolder>
{
    private static final String TAG = "RecyclerViewAdapter";
    private static final String FAVORITES_REF = "favorites";
    private static final String USERS_REF = "USERS";

    private List<FeaturedArticle> mFeaturedArticles;
    private Context mContext;
    private FirebaseAuth mAuth;


    private ImageButton favButton;

    private DatabaseReference mDatabaseReference;


    public RecyclerViewAdapter(List<FeaturedArticle> featuredArticles, Context context)
    {
        mFeaturedArticles = featuredArticles;
        mContext = context;
    }

    @Override
    public ArticleViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ArticleViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ArticleViewHolder holder, int position)
    {
        final FeaturedArticle featuredArticle = mFeaturedArticles.get(position);
        holder.mCategory.setText(featuredArticle.getCategory());
        holder.mTitle.setText(featuredArticle.getTitle());
        Picasso.with(mContext)
                .load(featuredArticle.getUrlToImage())
                .error(R.drawable.ic_image_black_48dp)
                .into(holder.mSportPicture);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String id = featuredArticle.getArticleID();
                updateArticleClicks(id);
                goToActualArticle(featuredArticle);
            }
        });
        holder.itemView.findViewById(R.id.share_button).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                sharedIntent(featuredArticle);
            }
        });

        holder.itemView.findViewById(R.id.read_more).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String id = featuredArticle.getArticleID();
                updateArticleClicks(id);
                goToActualArticle(featuredArticle);
            }
        });

        favButton = (ImageButton) holder.itemView.findViewById(R.id.favorite_button);
        favButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateFavorites(featuredArticle.getArticleID(), v);

            }
        });
    }

    private void updateFavorites(final String articleID, final View view)
    {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null)
        {
            String UIDOfCurrentUser = firebaseUser.getUid();

            final DatabaseReference favoritesReference = FirebaseDatabase.getInstance().getReference()
                    .child(USERS_REF)
                    .child(UIDOfCurrentUser)
                    .child(FAVORITES_REF);

            favoritesReference.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    Log.d(TAG, "onDataChange: " + dataSnapshot.toString());
                    GenericTypeIndicator<ArrayList<String>> arrayListGenericTypeIndicator
                            = new GenericTypeIndicator<ArrayList<String>>()
                    {
                    };
                    if (dataSnapshot.getValue() == null)
                    {
                        ArrayList<String> oneTimeInit = new ArrayList<>();
                        oneTimeInit.add(articleID);
                        favoritesReference.setValue(oneTimeInit);
                        Snackbar.make(view, "Added To Favorites", Snackbar.LENGTH_SHORT).show();
                    }
                    else if (dataSnapshot.getValue() != null)
                    {
                        ArrayList<String> listOfFavorites = dataSnapshot.getValue(arrayListGenericTypeIndicator);

                        if (listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.remove(articleID);
                            favoritesReference.setValue(listOfFavorites);
                            Snackbar.make(view, "Removed From Favorites", Snackbar.LENGTH_SHORT).show();
                        }
                        else if (!listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.add(articleID);
                            favoritesReference.setValue(listOfFavorites);
                            Snackbar.make(view, "Added To Favorites", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }
    }

    //http://stackoverflow.com/questions/4197135/how-to-start-activity-in-adapter
    private void sharedIntent(FeaturedArticle actualArticle)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, actualArticle.getTitle() + " - " +
                "https://sjapp-4c72a.firebaseapp.com/#/" + actualArticle.getCategory().toLowerCase() + "/view/" + actualArticle.getArticleID());
        shareIntent.setType("text/plain");
        mContext.startActivity(Intent.createChooser(shareIntent, "Share With.."));
    }

    //https://firebase.google.com/docs/database/admin/save-data#section-update
    private void updateArticleClicks(String id)
    {
        final String articleRef = "MICRO-ARTICLES";
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

    private void goToActualArticle(FeaturedArticle article)
    {
        final String key = "articledata";
        Intent fullArticle = new Intent(mContext, ArticleDetailActivity.class);
        fullArticle.putExtra(key, article.getArticleID());
        mContext.startActivity(fullArticle);
    }

    @Override
    public int getItemCount()
    {
        if ((mFeaturedArticles != null) && (mFeaturedArticles.size() != 0))
        {
            return mFeaturedArticles.size();
        }
        else
        {
            return 0;
        }
    }

    FeaturedArticle getArticle(int position)
    {
        return ((mFeaturedArticles != null) && (mFeaturedArticles.size() != 0) ? mFeaturedArticles.get(position) : null);
    }

    void loadArticleData(List<FeaturedArticle> articleList)
    {
        mFeaturedArticles = articleList;
        notifyDataSetChanged();
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

