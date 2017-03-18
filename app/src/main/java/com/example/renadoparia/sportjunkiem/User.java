package com.example.renadoparia.sportjunkiem;

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
    private Boolean isAdmin;

    public User(String firstName, String lastName, String email, String UID, String profilePicURL)
    {
        mFirstName = firstName;
        mLastName = lastName;
        mEmail = email;
        mUID = UID;
        mProfilePicURL = profilePicURL;
        isAdmin = false;
    }

    public Boolean getAdmin()
    {
        return isAdmin;
    }

    public void setAdmin(Boolean admin)
    {
        isAdmin = admin;
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
}
