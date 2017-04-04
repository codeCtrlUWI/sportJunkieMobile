package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.util.ArrayList;
import java.util.Collections;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Renado_Paria on 4/2/2017 at 11:55 AM for SportJunkieM.
 */

public class UserFavoritesFragment extends Fragment implements OnRecyclerClickListener
{
    private static final String TAG = "UserFavoritesFragment";
    private static final String USERS_REF = "USERS";
    private static final String FAVORITES_REF = "favorites";
    private static final String ARTICLES_REF = "ARTICLES";

    private UserFavoritesRVAdapter mUserFavoritesRVAdapter;
    private ArrayList<String> mFavoritesList = new ArrayList<>();

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    public UserFavoritesFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        retrieveFavoritesList();
    }

    @Override
    public void onItemClick(View view, int position)
    {
        Article article = mUserFavoritesRVAdapter.getArticle(position);
        updateArticleClicks(article.getArticleID());
        goToActualArticle(article);
    }

    @Override
    public void onItemLongClick(View view, int position)
    {
        Article article = mUserFavoritesRVAdapter.getArticle(position);
        sharedIntent(article);
    }

    @Override
    public void onItemDoubleTap(View view, int position)
    {
        Article article = mUserFavoritesRVAdapter.getArticle(position);
        updateFav(article.getArticleID());
    }

    private void retrieveFavoritesList()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String UIDOfCurrentUSER;
        if (currentUser != null)
        {
            UIDOfCurrentUSER = currentUser.getUid();
            final DatabaseReference test = FirebaseDatabase.getInstance().getReference()
                    .child(USERS_REF)
                    .child(UIDOfCurrentUSER)
                    .child(FAVORITES_REF);

            ValueEventListener retrievingFavoritesList = new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.getValue() != null)
                    {
                        GenericTypeIndicator<ArrayList<String>> arrayListGenericTypeIndicator
                                = new GenericTypeIndicator<ArrayList<String>>()
                        {
                        };
                        //http://stackoverflow.com/questions/4819635/how-to-remove-all-null-elements-from-a-arraylist-or-string-array
                        mFavoritesList = dataSnapshot.getValue(arrayListGenericTypeIndicator);

                        //noinspection SuspiciousMethodCalls
                        mFavoritesList.removeAll(Collections.singleton(null));
                        final ArrayList<Article> articleListing = new ArrayList<>();

                        for (int i = 0; i < mFavoritesList.size(); i++)
                        {
                            DatabaseReference dbref = FirebaseDatabase.getInstance()
                                    .getReference()
                                    .child(ARTICLES_REF)
                                    .child(mFavoritesList.get(i));
                            dbref.addListenerForSingleValueEvent(new ValueEventListener()
                            {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot)
                                {
                                    Article article = dataSnapshot.getValue(Article.class);
                                    articleListing.add(article);
                                    mUserFavoritesRVAdapter.loadArticleData(articleListing);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError)
                                {

                                }
                            });

                        }
                    }


                    else if (dataSnapshot.getValue() == null)
                    {
                        mUserFavoritesRVAdapter.clearData();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                }
            };
            test.addValueEventListener(retrievingFavoritesList);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);

//        JUST To TEST, CUSTOMIZE AFTER
        int tilePadding = getResources().getDimensionPixelSize(R.dimen.tile_padding);
        recyclerView.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        //
        recyclerView.addOnItemTouchListener(new RecyclerItemClicker(getContext(), recyclerView, this));

        mUserFavoritesRVAdapter = new UserFavoritesRVAdapter(new ArrayList<Article>(), getContext());
        recyclerView.setAdapter(mUserFavoritesRVAdapter);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    /*--------------------------------------------------------------------------------------------*/
    private void sharedIntent(Article actualArticle)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, actualArticle.getTitle() + " - " + actualArticle.getUrlToImage());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share With.."));
    }

    private void updateFav(final String articleID)
    {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String UIDOfCurrentUser;
        if (firebaseUser != null)
        {
            UIDOfCurrentUser = firebaseUser.getUid();

            final DatabaseReference test = FirebaseDatabase.getInstance().getReference()
                    .child(USERS_REF)
                    .child(UIDOfCurrentUser)
                    .child(FAVORITES_REF);
            test.addListenerForSingleValueEvent(new ValueEventListener()
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
                        test.setValue(oneTimeInit);
                    }
                    else if (dataSnapshot.getValue() != null)
                    {
                        ArrayList<String> listOfFavorites = dataSnapshot.getValue(arrayListGenericTypeIndicator);

                        if (listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.remove(articleID);
                            test.setValue(listOfFavorites);
                        }
                        else if (!listOfFavorites.contains(articleID))
                        {
                            listOfFavorites.add(articleID);
                            test.setValue(listOfFavorites);
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
        Intent fullArticle = new Intent(getApplicationContext(), ArticleDetailActivity.class);
        fullArticle.putExtra(key, article.toString());
        startActivity(fullArticle);
    }
}
