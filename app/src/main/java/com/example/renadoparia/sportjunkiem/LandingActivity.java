package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LandingActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener
{
    private static final String TAG = "LandingActivity";
    private static final int RC_SIGN_IN = 1738;
    private static final String DB_CHILD = "USERS";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseReference;
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: starts ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        //Initialize Buttons
        initializeWidgets();
        makeFullScreen();

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseReference = database.getReference().child(DB_CHILD);

        //Google Auth Stuffs
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();


        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    //TODO:Add Redirection
                    Intent tempIntent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(tempIntent);

                    Toast.makeText(getApplicationContext(), "Name: " + user.getDisplayName(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "onAuthStateChanged: User is Signed In: " + user.getEmail());
                }
                else
                {
                    Log.d(TAG, "onAuthStateChanged: User Is Not Signed In");
                }
            }
        };
        Log.d(TAG, "onCreate: ends");
    }

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
        findViewById(R.id.email_signin_button).setOnClickListener(this);
        findViewById(R.id.google_signin_button).setOnClickListener(this);
        findViewById(R.id.hasNoAcc).setOnClickListener(this);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
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
                googleSignIn();
                break;
            case R.id.hasNoAcc:
                Intent signUpForAcc = new Intent(getApplicationContext(), SignUpFormActivity.class);
                startActivity(signUpForAcc);
                break;
//            case R.id.tempsineout:
//                signOut();
//                break;
        }
    }

    private void signOut()
    {
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

    private void googleSignIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN)
        {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInResult.isSuccess())
            {
                GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
                Log.d(TAG, "onActivityResult:Full name " + googleSignInAccount.getDisplayName());
                fireBaseAuthWithGoogle(googleSignInAccount);
            }
        }
        else
        {
            //Do Something To UI here
        }
    }

    /*Note: A Google account's email address can change, so don't use it to identify a user.
     Instead, use the account's ID, which you can get on the client with GoogleSignInAccount.getId,
     and on the backend from the sub claim of the ID token.*/

    /*https://developers.google.com/identity/sign-in/android/people*/

    private void fireBaseAuthWithGoogle(final GoogleSignInAccount googleSignInAccount)
    {
        Log.d(TAG, "fireBaseAuthWithGoogle: " + googleSignInAccount.getId());
        Log.d(TAG, "fireBaseAuthWithGoogle: " + googleSignInAccount.getIdToken());
        AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        String googleFirstName = googleSignInAccount.getGivenName();
                        String googleLastName = googleSignInAccount.getFamilyName();
                        if (task.isSuccessful())
                        {

                            User googleUser = new User(googleFirstName, googleLastName,
                                    task.getResult().getUser().getEmail(),
                                    task.getResult().getUser().getUid(),
                                    task.getResult().getUser().getPhotoUrl().toString());

                            mDatabaseReference.child(googleSignInAccount.getId()).setValue(googleUser);

                            Log.d(TAG, "onComplete: signed in complete ");
                            Log.d(TAG, "onComplete: Name: " + task.getResult().getUser().getDisplayName());
                            Log.d(TAG, "onComplete: Email:" + task.getResult().getUser().getEmail());
                            Log.d(TAG, "onComplete: Photo URL: " + task.getResult().getUser().getPhotoUrl());
                        }
                        else
                        {
                            Log.d(TAG, "onComplete: " + task.getException());
                            Log.d(TAG, "onComplete: signed in not complete");
                        }
                    }
                });
        Log.d(TAG, "fireBaseAuthWithGoogle: ends");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
        Log.d(TAG, "onConnectionFailed: " + connectionResult.toString());
        Toast.makeText(getApplicationContext(), "Google Error", Toast.LENGTH_LONG).show();
    }
}
