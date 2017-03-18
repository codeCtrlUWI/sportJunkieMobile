package com.example.renadoparia.sportjunkiem;

import java.util.List;

/**
 * Created by Renado_Paria on 3/6/2017 at 9:11 PM.
 */

class Article
{
    private String mArticleID;
    private String mAuthorUID;
    private String mAuthorFname;
    private String mAuthorLname;
    private String mTitle;
    private String mSubtitle;
    private String mArticleData;
    private String mCategory;
    private long mTimeAndDateCreated;
    private String mLastUpdated;
    private String mUrlToImage;
    private int mNumberOfClicks;
    private List<String> mGalleryImages;

    public Article()
    {
    }

    //private String mLinkToArticle;
    /*This may be added, for sharing purposes later on, if it is added, remember to remake constructors accordingly*/
    public Article(String articleID, String authorUID, String authorFname, String authorLname, String title, String subtitle, String articleData, String category, long timeAndDateCreated, String lastUpdated, String urlToImage, int numberOfClicks, List<String> galleryImages)
    {
        mArticleID = articleID;
        mAuthorUID = authorUID;
        mAuthorFname = authorFname;
        mAuthorLname = authorLname;
        mTitle = title;
        mSubtitle = subtitle;
        mArticleData = articleData;
        mCategory = category;
        mTimeAndDateCreated = timeAndDateCreated;
        mLastUpdated = lastUpdated;
        mUrlToImage = urlToImage;
        mNumberOfClicks = numberOfClicks;
        mGalleryImages = galleryImages;
    }

    public Article(String articleID, String authorUID, String authorFname, String authorLname, String title, String subtitle, String articleData, String category, long timeAndDateCreated, String lastUpdated, String urlToImage, List<String> galleryImages)
    {
        mArticleID = articleID;
        mAuthorUID = authorUID;
        mAuthorFname = authorFname;
        mAuthorLname = authorLname;
        mTitle = title;
        mSubtitle = subtitle;
        mArticleData = articleData;
        mCategory = category;
        mTimeAndDateCreated = timeAndDateCreated;
        mLastUpdated = lastUpdated;
        mUrlToImage = urlToImage;
        mNumberOfClicks = 0;
        mGalleryImages = galleryImages;
    }

    public String getArticleID()
    {
        return mArticleID;
    }

    public void setArticleID(String articleID)
    {
        mArticleID = articleID;
    }

    public String getAuthorUID()
    {
        return mAuthorUID;
    }

    public void setAuthorUID(String authorUID)
    {
        mAuthorUID = authorUID;
    }

    public String getAuthorFname()
    {
        return mAuthorFname;
    }

    public void setAuthorFname(String authorFname)
    {
        mAuthorFname = authorFname;
    }

    public String getAuthorLname()
    {
        return mAuthorLname;
    }

    public void setAuthorLname(String authorLname)
    {
        mAuthorLname = authorLname;
    }

    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public String getSubtitle()
    {
        return mSubtitle;
    }

    public void setSubtitle(String subtitle)
    {
        mSubtitle = subtitle;
    }

    public String getArticleData()
    {
        return mArticleData;
    }

    public void setArticleData(String articleData)
    {
        mArticleData = articleData;
    }

    public String getCategory()
    {
        return mCategory;
    }

    public void setCategory(String category)
    {
        mCategory = category;
    }

    public long getTimeAndDateCreated()
    {
        return mTimeAndDateCreated;
    }

    public void setTimeAndDateCreated(long timeAndDateCreated)
    {
        mTimeAndDateCreated = timeAndDateCreated;
    }

    public String getLastUpdated()
    {
        return mLastUpdated;
    }

    public void setLastUpdated(String lastUpdated)
    {
        mLastUpdated = lastUpdated;
    }

    public String getUrlToImage()
    {
        return mUrlToImage;
    }

    public void setUrlToImage(String urlToImage)
    {
        mUrlToImage = urlToImage;
    }

    public int getNumberOfClicks()
    {
        return mNumberOfClicks;
    }

    public void setNumberOfClicks(int numberOfClicks)
    {
        mNumberOfClicks = numberOfClicks;
    }

    public List<String> getGalleryImages()
    {
        return mGalleryImages;
    }

    public void setGalleryImages(List<String> galleryImages)
    {
        mGalleryImages = galleryImages;
    }

    @Override
    public String toString()
    {
        return "{" +
                "mArticleID='" + mArticleID + '\'' +
                ", mAuthorUID='" + mAuthorUID + '\'' +
                ", mAuthorFname='" + mAuthorFname + '\'' +
                ", mAuthorLname='" + mAuthorLname + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mSubtitle='" + mSubtitle + '\'' +
                ", mArticleData='" + mArticleData + '\'' +
                ", mCategory='" + mCategory + '\'' +
                ", mTimeAndDateCreated=" + mTimeAndDateCreated +
                ", mLastUpdated='" + mLastUpdated + '\'' +
                ", mUrlToImage='" + mUrlToImage + '\'' +
                ", mNumberOfClicks=" + mNumberOfClicks +
                ", mGalleryImages=" + mGalleryImages + '}';
    }
}


