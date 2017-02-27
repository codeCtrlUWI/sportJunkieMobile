package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LandingActivity extends AppCompatActivity
{
    private static final String TAG = "LandingActivity";
    private Button signInButton;
    private Button newPersonButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: starts ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        //Initialize Buttons
        signInButton = (Button) findViewById(R.id.signIn);
        newPersonButton = (Button) findViewById(R.id.newPerson);

        //Set Button Listeners
        signInButton.setOnClickListener(new Clicker());
        newPersonButton.setOnClickListener(new Clicker());

        Log.d(TAG, "onCreate: ends");
    }

    class Clicker implements View.OnClickListener
    {
        @Override
        public void onClick(View v)
        {
            int id = v.getId();

            switch (id)
            {
                case R.id.newPerson:
                    Log.d(TAG, "onClick: new Person Button Clicked");
                    Intent signUp = new Intent(getApplicationContext(), SignUpOptionActivity.class);
                    startActivity(signUp);
                    break;
                case R.id.signIn:
                    Log.d(TAG, "onClick: Sign In Button Clicked");
                    Intent signInOptions = new Intent(getApplicationContext(), SignInOptionActivity.class);
                    startActivity(signInOptions);
                    break;
            }
        }
    }
}
