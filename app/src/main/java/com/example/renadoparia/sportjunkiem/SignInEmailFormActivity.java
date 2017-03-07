package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class SignInEmailFormActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "SignInEmailFormActivity";

    private EditText mEmailText;
    private EditText mPasswordText;
    private Button mSignInButton;
    private Button mNoAccountButton;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_email_form);
        makeFullScreen();
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
                    Log.d(TAG, "onAuthStateChanged: User Is Not Signed In");
                }

            }
        };
        initializeWidgets();
    }

    /*Duplicated Code, Fix This later*/
    private void makeFullScreen()
    {
        final View decorView = getWindow().getDecorView();
        final int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
        {
            @Override
            public void onSystemUiVisibilityChange(int visibility)
            {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0)
                {
                    decorView.setSystemUiVisibility(uiOptions);
                }
            }
        });
    }

    private void initializeWidgets()
    {
        mEmailText = (EditText) findViewById(R.id.emale);
        mPasswordText = (EditText) findViewById(R.id.password);
        mSignInButton = (Button) findViewById(R.id.sineIn);
        mNoAccountButton = (Button) findViewById(R.id.hasNoAccount);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSignInButton.setOnClickListener(this);
        mNoAccountButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.sineIn:
                String email = mEmailText.getText().toString().trim();
                String password = mPasswordText.getText().toString().trim();
                signIn(email, password, v);
                break;
            case R.id.hasNoAccount:
                /*Maybe i Could Add a putExtra here, in the case of the user is an idiot and try to sign in without an account,
                * I can take the email, pass it through to the sign up in EMail Activity and put it there For him to use*/
                Intent intent = new Intent(getApplicationContext(), SignUpFormActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void signIn(String email, String password, final View v)
    {
        if (!isValid(email, password))
        {
            Log.d(TAG, "signIn: Invalid Credentials");
        }
        else
        {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                //Do Query In Future For SnackBar To Get Their Name
                                Snackbar.make(v, "Welcome: " + task.getResult().getUser().getEmail(), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }


    private boolean isValid(String email, String password)
    {
        final int passwordLimit = 8;
        boolean valid = true;
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            mEmailText.setError("Enter A Valid Email");
            valid = false;
        }
        else
        {
            mEmailText.setError(null);
        }
        if (TextUtils.isEmpty(password) || password.length() < passwordLimit)
        {
            mPasswordText.setError("Password is too short");
            valid = false;
        }
        else
        {
            mPasswordText.setError(null);
        }
        return valid;
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
}
