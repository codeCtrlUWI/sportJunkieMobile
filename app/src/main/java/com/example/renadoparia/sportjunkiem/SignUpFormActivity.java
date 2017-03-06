package com.example.renadoparia.sportjunkiem;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

import static com.example.renadoparia.sportjunkiem.R.drawable.placeholder_person;

public class SignUpFormActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "SignUpFormActivity";
    private static final String DB_CHILD = "USERS";
    private static final String NAME_OF_FOLDER_FOR_PROFILEPIC = "Profile Pictures";
    private static final String PLACEHOLDER_IMAGE = "placeholder_person.png";
    private static final int PERMISSION_REQUEST_IMAGE_GALLERY = 1765;
    private static final int GALLERY_REQUEST_CODE = 1;
    private static final String PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;

    private EditText mFname;
    private EditText mLname;
    private EditText mEmail;
    private EditText mPassword;
    private ImageButton mProfilePictureButton;
    private Button mRegisterButton;
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
        Log.d(TAG, "resourceToUri: starts");
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.d(TAG, "onCreate: starts");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);
        makeFullScreen();
        galleryImagePermissionRequest();

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
        Log.d(TAG, "initializeWidgets: starts");
        mFname = (EditText) findViewById(R.id.actualFname);
        mLname = (EditText) findViewById(R.id.actuaLname);
        mEmail = (EditText) findViewById(R.id.actualEmail);
        mPassword = (EditText) findViewById(R.id.actualPW);
        mProfilePictureButton = (ImageButton) findViewById(R.id.proPicBut);
        mRegisterButton = (Button) findViewById(R.id.saveInfo);
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
        Log.d(TAG, "initializeWidgets: ends");
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
            case R.id.clearPhoto:
                mProfilePictureButton.setImageResource(placeholder_person);
                // mImageUri = resourceToUri(getApplicationContext(), R.drawable.placeholder_person);
                mImageUri = null;
                break;
        }
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
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume: starts");

        someHouseKeepingPermissions();
        photoButtonUI_Informaton();//With This Method, We can update the UI respectively,
        // depending on whether the button is enabled or disabled

        mProfilePictureButton.setOnClickListener(this);
        mRegisterButton.setOnClickListener(this);
        mClearPhotoButton.setOnClickListener(this);

        Log.d(TAG, "onResume: ends");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.d(TAG, "onPause: We are Paused");
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

    private void galleryImagePermissionRequest()
    {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Log.d(TAG, "galleryImagePermissionRequest: Persmission not granted, Requesting");
            requestGalleryPermission();
        }
    }

    /*Reason Why This is in a method, I reused it at some point, so I left it in here, just in case i needed it again*/
    private void requestGalleryPermission()
    {
        ActivityCompat.requestPermissions(this,
                new String[]{PERMISSION_READ_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_IMAGE_GALLERY);
    }

    //http://stackoverflow.com/questions/11556607/android-difference-between-invisible-and-gone
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case PERMISSION_REQUEST_IMAGE_GALLERY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(getApplicationContext(), "Permission For" + permissions[0] + "Granted", Toast.LENGTH_LONG).show();
                    mProfilePictureButton.setEnabled(true);
                    //mProfilePictureButton.setVisibility(View.VISIBLE);
                }
                else if (grantResults[0] == PackageManager.PERMISSION_DENIED)
                {
                    mProfilePictureButton.setEnabled(false);
                    //  mProfilePictureButton.setVisibility(View.INVISIBLE);
                    new AlertDialog.Builder(this)
                            .setIcon(R.drawable.ic_insert_photo_black_24dp)
                            .setTitle("Gallery Access")
                            .setMessage("Go to Settings > Apps > SportJunkie > Permissions > Enable Storage")
                            .setNegativeButton("Dismiss", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.dismiss();
                                    Log.d(TAG, "onClick: Dialog Clicked");
                                }
                            })
                            .setPositiveButton("Request Again", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent restart = getIntent();
                                    finish();
                                    startActivity(restart);
                                    Log.d(TAG, "onClick: User Requests Permission Again");
                                }
                            }).show();
                }
                break;
        }

    }

    /*This Method, It checks to see if the permissions are granted or not.
    * If they Are Granted, The profile picture button will be enabled, and the user can select their photos
    * If They Deny permission, The Button Will be disabled, and the user will not be able to select their photos
    * This method is put in the onResume method, in the case of the user goung to settings and manually enabling the storage option,
    * therefore, when they return from settings, the lifecycle returns to the onResume Method, and checks the permissions and updates
    * the UI Accordingly.*/
    private void someHouseKeepingPermissions()
    {
        if (ContextCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            mProfilePictureButton.setEnabled(true);
        }
        else if (ContextCompat.checkSelfPermission(this, PERMISSION_READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(getApplicationContext(), "Cannot Access Photos, Needs Permission", Toast.LENGTH_SHORT).show();
            mProfilePictureButton.setEnabled(false);
        }
    }

    /*This Method, Checks To See If The Buttons Are Enabled Not
    * Later On, We can Add more appropriate UI Changes so indicate That the button is not clickable or it is clickable*/
    private void photoButtonUI_Informaton()
    {
        if (!mProfilePictureButton.isEnabled())
        {
            Toast.makeText(getApplicationContext(), "Cannot Select A Picture, Needs Permission", Toast.LENGTH_LONG).show();
        }
        else if (mProfilePictureButton.isEnabled())
        {
            Toast.makeText(getApplicationContext(), "You Can Select A Profile Picture", Toast.LENGTH_LONG).show();
        }
    }

    //TODO:When user is registering, check if their email is arleady in the database,that means, that their account was already created

    /*I Should Refactor This Code Some more, Cause why the fuck not*/
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
                                            Snackbar.make(view, "Welcome: " + fName + " " + lName, Snackbar.LENGTH_LONG).show();
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
                                    StorageReference getDefaultProfilePic = mStorageRef.child(PLACEHOLDER_IMAGE);
                                    getDefaultProfilePic.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
                                    {
                                        @Override
                                        public void onSuccess(Uri uri)
                                        {
                                            User user = new User(fName, lName, email, userUID, uri.toString());
                                            mDatabaseRef.push().setValue(user);
                                            Snackbar.make(view, "Welcome: " + fName + " " + lName, Snackbar.LENGTH_LONG).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener()
                                    {
                                        @Override
                                        public void onFailure(@NonNull Exception e)
                                        {
                                            Log.d(TAG, "onFailure: Failure To Retrieve URL");
                                            /*If For Some Weird Reason, The File Is Not In Storage, I Am Manually uploading from the drawable*/
                                            final Uri appBasedImage = resourceToUri(getApplicationContext(), placeholder_person);
                                            StorageReference putDefaultpic = mStorageRef.child("placeholder_person.png");
                                            putDefaultpic.putFile(appBasedImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
                                            {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                                                {
                                                    Uri appBasedimg = taskSnapshot.getDownloadUrl();
                                                    User user = new User(fName, lName, email, userUID, appBasedimg.toString());
                                                    mDatabaseRef.push().setValue(user);
                                                    Snackbar.make(view, "Welcome: " + fName + " " + lName, Snackbar.LENGTH_LONG).show();
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
            try
            {
                mImageUri = data.getData();
                Log.d(TAG, "onActivityResult: " + mImageUri.toString());
                mProfilePictureButton.setImageURI(mImageUri);
            }
            catch (Exception e)
            {
                Log.e(TAG, "onActivityResult: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void signOut()
    {
        Log.d(TAG, "signOut: called");
        mAuth.signOut();
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
}
