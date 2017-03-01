package com.example.renadoparia.sportjunkiem;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class SignUpFormActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "SignUpFormActivity";
    private static final String DB_CHILD = "USERS";
    private static final String NAME_OF_FOLDER_FOR_PROFILEPIC = "Profile Pictures";

    private static final int GALLERY_REQUEST_CODE = 1;

    private EditText mFname;
    private EditText mLname;
    private EditText mEmail;
    private EditText mPassword;
    private ImageButton mProfilePictureButton;
    private Button mRegisterButton;
    private Button mTempLogoutButton;//A Temporary Testing Button To Logout;
    private Button mClearPhotoButton;
    private Uri mImageUri;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;

    //TODO: Add Confirmation Password Field, Then Check To See if It's Valid against Initial Password Entered
    //TODO: Consider Offline Storage
    //TODO: Check For Internet Connection
    //TODO:Add Ability To Clear Individual Text Fields With The Tap Of A Button

    // http://stackoverflow.com/questions/4896223/how-to-get-an-uri-of-an-image-resource-in-android
    private static Uri resourceToUri(Context context, int resID)
    {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabaseRef = database.getReference(DB_CHILD);
        mStorageRef = FirebaseStorage.getInstance().getReference().child(NAME_OF_FOLDER_FOR_PROFILEPIC);

        mAuthStateListener = new FirebaseAuth.AuthStateListener()
        {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth)
            {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null)
                {
                    //TODO:Add Redirection
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
        mProfilePictureButton = (ImageButton) findViewById(R.id.proPicBut);
        mRegisterButton = (Button) findViewById(R.id.saveInfo);
        mTempLogoutButton = (Button) findViewById(R.id.tempSignOutButton);
        mClearPhotoButton = (Button) findViewById(R.id.clearPhoto);


        /*Setting Default URI To PlaceHolder Image, So If the user does not select an image to post,
        * their default will be a placeholder image*/

        /*Another option if a user does not select an image is to set their imageURL
        * in the database entry to some arbitray constant, that way, the field will exists in the db,
        * even if they don't select an image(unlike, if you put null, the db doesn't consider the field),
        **/

        /*Come back to this option, When doing the update option(When they edit their credentials)*/
        //  mImageUri = resourceToUri(getApplicationContext(), R.drawable.placeholder_person);
        mImageUri = null;
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
        mTempLogoutButton.setOnClickListener(this);
        mClearPhotoButton.setOnClickListener(this);
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
            case R.id.proPicBut:
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
            case R.id.tempSignOutButton:
                signOut();
                break;
            case R.id.clearPhoto:
                mProfilePictureButton.setImageResource(R.drawable.placeholder_person);
                // mImageUri = resourceToUri(getApplicationContext(), R.drawable.placeholder_person);
                mImageUri = null;
                break;
        }
    }

    private void doRegistration(final String fName, final String lName, final String email, final String password, View v)
    {
        final View view = v;
        if (!isValid(fName, lName, email, password))
        {
            Log.d(TAG, "doRegistration: Unable To Sign Up");
        }
        else
        {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                Log.d(TAG, "onComplete: SuccessFully Created Account: " + task.getResult().getUser().getEmail());
                                final String userUID = task.getResult().getUser().getUid();
                                if (mImageUri != null)
                                {
                                    Log.d(TAG, "onComplete: imageURI: " + mImageUri.toString());
                                    StorageReference profilePicRefPath =
                                            mStorageRef.child(email + "-" + userUID + "-" + UUID.randomUUID().toString());
                                    profilePicRefPath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                    {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                        {
                                            Uri profilePicUri = taskSnapshot.getDownloadUrl();
                                            User user = new User(fName, lName, email, userUID, profilePicUri.toString());
                                            mDatabaseRef.push().setValue(user);
                                            Snackbar.make(view, "Welcome:" + fName + " " + lName, Snackbar.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Log.d(TAG, "onFailure: Failed To Upload");
                                            e.printStackTrace();
                                        }
                                    });
                                }
                                else if (mImageUri == null)
                                {
                                    StorageReference getDefaultProfilePic = mStorageRef.child("placeholder_person.png");
                                    getDefaultProfilePic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                        public void onSuccess(Uri uri)
                                        {
                                            User user = new User(fName, lName, email, userUID, uri.toString());
                                            mDatabaseRef.push().setValue(user);
                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Log.d(TAG, "onFailure: Failure To Retrieve URL");
                                            /*If For Some Weird Reason, The File Is Not In Storage, I Am Manually uploading from the drawable*/
                                            final Uri appBasedImage = resourceToUri(getApplicationContext(), R.drawable.placeholder_person);
                                            StorageReference putDefaultpic = mStorageRef.child("placeholder_person.png");
                                            putDefaultpic.putFile(appBasedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                            {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                                {
                                                    User user = new User(fName, lName, email, userUID, appBasedImage.toString());
                                                    mDatabaseRef.push().setValue(user);
                                                }
                                            }).addOnFailureListener(new OnFailureListener()
                                            {
                                                @Override
                                                public void onFailure(@NonNull Exception e)
                                                {
                                                    //Ok, Fuck It All, You Don't wanna work, fuck it all
                                                }
                                            });

                                            e.printStackTrace();
                                        }
                                    });

                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener()
                    {
                        @Override
                        public void onFailure(@NonNull Exception e)
                        {
                            e.printStackTrace();
                        }
                    });
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
            mPassword.setError(getString(R.string.longer_than_eight));
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

    private void signOut()
    {
        mAuth.signOut();
    }
}
