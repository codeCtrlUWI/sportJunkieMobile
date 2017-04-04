package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "HomeActivity";
    private static final String FOOTBALL_TAG = "Football";
    private static final String CRICKET_TAG = "Cricket";
    private static final String SWIMMING_TAG = "Swimming";
    private static final String KEY = "Tag";
    public static final String NO_MENU_ITEM_SELECTED = "NO_QUERY";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private GoogleApiClient mGoogleApiClient;

    private DrawerLayout mDrawerLayout;

    private static final String MENU_TAG = "MENU_TAG";
    private int menuState;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initGoogleStuff();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);

        if (savedInstanceState != null)
        {
            Log.d(TAG, "onCreate: Saved Instance State Is Not Null");
            menuState = savedInstanceState.getInt(MENU_TAG);
            if (menuState == 0)
            {
                menuState = R.id.home;
                displaySelectedItem(menuState);
            }
            else
            {
                displaySelectedItem(menuState);
            }
        }
        else
        {
            Log.d(TAG, "onCreate: There is No Saved State");
            loadHome(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        NavigationView navigationView = (NavigationView) findViewById(R.id.drawer_nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null)
        {
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle
                (this,
                        mDrawerLayout,
                        toolbar,
                        R.string.open,
                        R.string.close);

        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    Log.d(TAG, "onAuthStateChanged: User is Signed In: " + user.getEmail());
                }
                else
                {
                    Intent intent = new Intent(getApplicationContext(), LandingActivity.class);
                    startActivity(intent);
                    Log.d(TAG, "onAuthStateChanged: User Is Not Signed In");
                }
            }
        };

    }

    private void loadHome(ViewPager viewPager)
    {
        Bundle bundle = new Bundle();
        Fragment fragment = new FeaturedFragment();
        Fragment latestFragos = new LatestContentFragment();
        bundle.putString(KEY, NO_MENU_ITEM_SELECTED);
        fragment.setArguments(bundle);
        latestFragos.setArguments(bundle);
        setUpViewPager(viewPager, fragment, latestFragos);
    }

    private void initGoogleStuff()
    {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        displaySelectedItem(item.getItemId());
        return true;
    }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    private void displaySelectedItem(int id)
    {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        Fragment categoryFragment;
        Fragment latestContentFragment;
        Bundle bundle = new Bundle();
        switch (id)
        {
            /*I only passed variables because i was using a fragment for differnet queries, but i don't need to anymore
            * for the featured fragment, but i too damn lazy and it ain't doing anybody no harm, so i gonna leave it*/
            case R.id.home:
                menuState = id;
                categoryFragment = new FeaturedFragment();
                latestContentFragment = new LatestContentFragment();
                bundle.putString(KEY, NO_MENU_ITEM_SELECTED);
                categoryFragment.setArguments(bundle);
                latestContentFragment.setArguments(bundle);
                setUpViewPager(viewPager, categoryFragment, latestContentFragment);
                break;
            case R.id.foosball:
                categoryFragment = new FeaturedCategoryFragment();
                latestContentFragment = new LatestContentFragment();
                bundle.putString(KEY, FOOTBALL_TAG);
                categoryFragment.setArguments(bundle);
                latestContentFragment.setArguments(bundle);
                setUpViewPager(viewPager, categoryFragment, latestContentFragment);
                menuState = id;
                break;
            case R.id.cricket:
                menuState = id;
                categoryFragment = new FeaturedCategoryFragment();
                latestContentFragment = new LatestContentFragment();
                bundle.putString(KEY, CRICKET_TAG);
                categoryFragment.setArguments(bundle);
                latestContentFragment.setArguments(bundle);
                setUpViewPager(viewPager, categoryFragment, latestContentFragment);
                break;
            case R.id.Swimming:
                categoryFragment = new FeaturedCategoryFragment();
                latestContentFragment = new LatestContentFragment();
                bundle.putString(KEY, SWIMMING_TAG);
                categoryFragment.setArguments(bundle);
                latestContentFragment.setArguments(bundle);
                setUpViewPager(viewPager, categoryFragment, latestContentFragment);
                menuState = id;
                break;
            case R.id.loggo:
                signOut();
                break;
            case R.id.prof:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            default:
                menuState = R.id.home;
                break;
        }
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home)
        {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        if (id == R.id.profile)
        {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d(TAG, "onStart: starts");
        mAuth.addAuthStateListener(mAuthStateListener);
        Log.d(TAG, "onStart: ends");
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (mAuthStateListener != null)
        {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
    }


    private void signOut()
    {
        Log.d(TAG, "signOut: called");
        mAuth.signOut();


        Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                .setResultCallback(new ResultCallback<Status>()
                {
                    @Override
                    public void onResult(@NonNull Status status)
                    {
                        Log.d(TAG, "onResult: " + status.getStatus().toString());
                    }
                });
    }

    //http://stackoverflow.com/questions/18747975/difference-between-fragmentpageradapter-and-fragmentstatepageradapter
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

//    private void setUpViewPager(ViewPager viewPager)
//    {
//        Adapter adapter = new Adapter(getSupportFragmentManager());
//        adapter.addFragment(new FeaturedFragment(), "Featured");
//        viewPager.setAdapter(adapter);
//    }

    private void setUpViewPager(ViewPager viewPager, Fragment fragment, Fragment latestFragment)
    {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(fragment, "Featured");
        adapter.addFragment(latestFragment, "Latest");
        viewPager.setAdapter(adapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        Log.d(TAG, "onSaveInstanceState: called");
        Log.d(TAG, "onSaveInstanceState: saving: " + menuState);
        outState.putInt(MENU_TAG, menuState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        menuState = savedInstanceState.getInt(MENU_TAG);
        Log.d(TAG, "onRestoreInstanceState: Restored: " + menuState);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume: called");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause: called");
    }


}
