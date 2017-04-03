package com.example.renadoparia.sportjunkiem;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Renado_Paria on 4/2/2017 at 11:55 AM for SportJunkieM.
 */

public class UserFavoritesFragment extends Fragment
{
    private static final String TAG = "UserFavoritesFragment";
    private UserFavoritesRVAdapter mUserFavoritesRVAdapter;
    private ArrayList<String> mFavoritos = new ArrayList<>();

    public UserFavoritesFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        retrieveFavoritesList();
    }

    private void retrieveFavoritesList()
    {
        final DatabaseReference test = FirebaseDatabase.getInstance().getReference()
                .child("USERS")
                .child("nyrS4XcuhTfTb2y9AhJEtJaElkH3")
                .child("favorites");

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
                    mFavoritos = dataSnapshot.getValue(arrayListGenericTypeIndicator);
                    mFavoritos.removeAll(Collections.singleton(null));
                    Log.d(TAG, "onDataChange: LIST OF FAVORITOS = " + mFavoritos.toString());
                    final ArrayList<Article> articlelist = new ArrayList<>();

                    for (int i = 0; i < mFavoritos.size(); i++)
                    {
                        DatabaseReference dbref = FirebaseDatabase.getInstance()
                                .getReference()
                                .child("ARTICLES")
                                .child(mFavoritos.get(i));
                        dbref.addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                Article article = dataSnapshot.getValue(Article.class);
                                articlelist.add(article);
                                mUserFavoritesRVAdapter.loadArticleData(articlelist);
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
                    ArrayList<Article> temp3 = new ArrayList<>();
                    Log.d(TAG, "onDataChange: You Are Not Favored O Great One");
                    Article temp = new Article("You Currently Have No Favorites",
                            "You Currently Have No Favorites",
                            "You Currently Have No Favorites",
                            "You Currently Have No Favorites",
                            "You Currently Have No Favorites",
                            "You Currently Have No Favorites",
                            "You Currently Have No Favorites",
                            "You Currently Have No Favorites",
                            0, "You Currently Have No Favorites", "You Currently Have No Favorites");
                    temp3.add(temp);
                    mUserFavoritesRVAdapter.loadArticleData(temp3);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        };
        test.addValueEventListener(retrievingFavoritesList);
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

        mUserFavoritesRVAdapter = new UserFavoritesRVAdapter(new ArrayList<Article>(), getContext());
        recyclerView.setAdapter(mUserFavoritesRVAdapter);
        return recyclerView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }
}
