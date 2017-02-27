package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SignUpOptionActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "SignUpOptionActivity";
    private Button mSignUpWithEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_option);
        mSignUpWithEmail = (Button) findViewById(R.id.email_signup_button);
        mSignUpWithEmail.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.email_signup_button:
                Log.d(TAG, "onClick: Email Sign Up Button Clicked");
                Intent intent = new Intent(getApplicationContext(), SignUpFormActivity.class);
                startActivity(intent);
                break;
        }

    }
}
