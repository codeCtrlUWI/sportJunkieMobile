package com.example.renadoparia.sportjunkiem;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity
{
    private static final String TAG = "ProfileActivity";
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private ImageView mProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.foldUpBar);
        mProfilePicture = (ImageView) findViewById(R.id.profile_picture);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabitha);
        tabLayout.setupWithViewPager(viewPager);
        setUpViewPager(viewPager);
        setUpProfile();
    }

    private void setUpProfile()
    {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child("USERS").child("tC3tXLgw3SRX7RcwxhTHy49gXk63");
        databaseReference.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null)
                {
                    Log.d(TAG, "onDataChange: user: " + dataSnapshot.toString());
                    User user = dataSnapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: user = " + user.toString());
                    mCollapsingToolbarLayout.setTitle(user.getFirstName() + " " + user.getLastName());
                    Picasso.with(getApplicationContext())
                            .load(user.getProfilePicURL())
                            .error(R.drawable.lgb2_blur)
                            .into(mProfilePicture);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager)
    {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new UserFavoritesFragment(), "Favorites");
        viewPager.setAdapter(adapter);
    }


    private static class Adapter extends FragmentStatePagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        Adapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }
}
