package com.example.renadoparia.sportjunkiem;

import java.util.ArrayList;

/**
 * Created by Renado_Paria on 2/26/2017 at 11:42 PM.
 */

class User
{
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mUID;
    private String mProfilePicURL;
    private Boolean isAuthor;
    private ArrayList<String> mFavorites;


    public User(String firstName, String lastName, String email, String UID, String profilePicURL)
    {
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mUID = UID;
        mProfilePicURL = profilePicURL;
        isAuthor = false;
        mFavorites = null;
    }

    public User(String firstName,
                String lastName,
                String email,
                String UID,
                String profilePicURL,
                Boolean isAuthor,
                ArrayList<String> favorites)
    {
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mUID = UID;
        mProfilePicURL = profilePicURL;
        this.isAuthor = isAuthor;
        mFavorites = favorites;
    }

    public Boolean getAuthor()
    {
        return isAuthor;
    }

    public void setAuthor(Boolean author)
    {
        isAuthor = author;
    }

    public User()
    {
    }

    public String getEmail()
    {
        return mEmail;
    }

    public void setEmail(String email)
    {
        mEmail = email;
    }

    public String getFirstName()
    {
        return mFirstName;
    }

    public void setFirstName(String firstName)
    {
        mFirstName = firstName;
    }

    public String getLastName()
    {
        return mLastName;
    }

    public void setLastName(String lastName)
    {
        mLastName = lastName;
    }

    public String getProfilePicURL()
    {
        return mProfilePicURL;
    }

    public void setProfilePicURL(String profilePicURL)
    {
        mProfilePicURL = profilePicURL;
    }

    public String getUID()
    {
        return mUID;
    }

    public void setUID(String UID)
    {
        mUID = UID;
    }

    @Override
    public String toString()
    {
        return "User{" +
                "mFirstName='" + mFirstName + '\'' +
                ", mLastName='" + mLastName + '\'' +
                ", mEmail='" + mEmail + '\'' +
                ", mUID='" + mUID + '\'' +
                ", mProfilePicURL='" + mProfilePicURL + '\'' +
                ", isAuthor=" + isAuthor +
                ", mFavorites=" + mFavorites +
                '}';
    }
}
