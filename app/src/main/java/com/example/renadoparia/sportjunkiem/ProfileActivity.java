package com.example.renadoparia.sportjunkiem;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity
{
    private TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mTabLayout = (TabLayout) findViewById(R.id.tabitha);
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_favorite_white_24dp));
        mTabLayout.addTab(mTabLayout.newTab().setIcon(R.drawable.ic_account_circle_white_24dp));

//        mTabLayout.addTab(mTabLayout.newTab().setText("Favorites"));
//        mTabLayout.addTab(mTabLayout.newTab().setText("Account"));
    }
}
