package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SignInOptionActivity extends AppCompatActivity implements View.OnClickListener
{
    private Button mSignInEmailButton;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_option);
        initializeWidgets();
    }

    private void initializeWidgets()
    {
        mSignInEmailButton = (Button) findViewById(R.id.email_signin_button);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSignInEmailButton.setOnClickListener(this);
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
        }
    }
}
