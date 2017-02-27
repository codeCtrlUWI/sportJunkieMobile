package com.example.renadoparia.sportjunkiem;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class SignUpFormActivity extends AppCompatActivity implements View.OnClickListener
{
    private static final String TAG = "SignUpFormActivity";
    private static final int GALLERY_REQUEST_CODE = 1;
    private ImageButton mProfilePictureButton;
    private Uri mImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_form);

        mProfilePictureButton = (ImageButton) findViewById(R.id.profilePicture);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mProfilePictureButton.setOnClickListener(this);
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
        }
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
