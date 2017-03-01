package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class LandingActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "LandingActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: starts ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        //Initialize Buttons
        initalizeWidgets();
        Log.d(TAG, "onCreate: ends");
    }

    private void initalizeWidgets()
    {
        findViewById(R.id.email_signin_button).setOnClickListener(this);
        findViewById(R.id.google_signin_button).setOnClickListener(this);
        findViewById(R.id.hasNoAcc).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.email_signin_button:
                Intent intent = new Intent(getApplicationContext(), SignInEmailFormActivity.class);
                startActivity(intent);
                break;
            case R.id.google_signin_button:
                break;
            case R.id.hasNoAcc:
                Intent signUpForAcc = new Intent(getApplicationContext(), SignUpFormActivity.class);
                startActivity(signUpForAcc);
                break;
        }


    }
}
