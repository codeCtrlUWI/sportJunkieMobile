package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpFormActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "SignUpFormActivity";

    private static final int GALLERY_REQUEST_CODE = 1;

    private EditText mFname;
    private EditText mLname;
    private EditText mEmail;
    private EditText mPassword;
    private ImageButton mProfilePictureButton;
    private Button mRegisterButton;
    private Uri mImageUri;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    //TODO: Add Confirmation Password Field, Then Check To See if It's Valid against Initial Password Entered

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);

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

        initializeWidgets();//Initializes All The Necessary Widgets For This Activity
    }

    private void initializeWidgets()
    {
        mFname = (EditText) findViewById(R.id.actualFname);
        mLname = (EditText) findViewById(R.id.actuaLname);
        mEmail = (EditText) findViewById(R.id.actualEmail);
        mPassword = (EditText) findViewById(R.id.actualPW);
        mProfilePictureButton = (ImageButton) findViewById(R.id.profilePicture);
        mRegisterButton = (Button) findViewById(R.id.saveInfo);
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
        mProfilePictureButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
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

    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.profilePicture:
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                break;
            case R.id.saveInfo:
                String fName = mFname.getText().toString().trim();
                String lName = mLname.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                doRegistration(fName, lName, email, password, v);
                break;
        }
    }

    private void doRegistration(String fName, String lName, String email, String password, View v)
    {
        if (!isValid(fName, lName, email, password))
        {
            return;
        }
        else
        {
            Log.d(TAG, "doRegistration: we are able to sign up");
        }
    }

    private boolean isValid(String fName, String lName, String email, String password)
    {
        final int fName_lName_limit = 3;
        final int passwordLimit = 8;
        boolean valid = true;
        //Validating First Name
        if (TextUtils.isEmpty(fName) || fName.length() < fName_lName_limit)
        {
            mFname.setError(getString(R.string.name_entry_limit));
            valid = false;
        }
        else if (!TextUtils.isEmpty(fName) || fName.length() >= fName_lName_limit)
        {
            mFname.setError(null);
        }
        //Validating Last Name
        if (TextUtils.isEmpty(lName) || lName.length() < fName_lName_limit)
        {
            mLname.setError(getString(R.string.name_entry_limit));
            valid = false;
        }
        else if (!TextUtils.isEmpty(fName) || fName.length() >= fName_lName_limit)
        {
            mFname.setError(null);
        }
        //Validating Email
        if (TextUtils.isEmpty(email) || (!Patterns.EMAIL_ADDRESS.matcher(email).matches()))
        {
            mEmail.setError(getString(R.string.enter_valid_email));
            valid = false;
        }
        else
        {
            mEmail.setError(null);
        }
        //Validating Password
        if (TextUtils.isEmpty(password) || password.length() < passwordLimit)
        {
            //TODO:Validate Password Characters So That It Contains At Least Some Other Character Or Capital Letter
            mPassword.setError("Please Enter A Longer Password");
            valid = false;
        }
        else
        {
            mPassword.setError(null);
        }
        return valid;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK)
        {
            mImageUri = data.getData();
            Log.d(TAG, "onActivityResult: " + mImageUri.toString());
            mProfilePictureButton.setImageURI(mImageUri);
        }
    }
}
