package com.example.renadoparia.sportjunkiem;

/**
 * Created by Renado_Paria on 4/5/2017 at 11:05 AM for SportJunkieM.
 */

class FeaturedArticle
{
    private String mArticleID;
    private String mAuthorUID;
    private String mTitle;
    private String mCategory;
    private String mUrlToImage;
    private long mNumberOfClicks;
    private String mSubtitle;

    public FeaturedArticle(String articleID, String authorUID, String title, String category, String urlToImage, long numberOfClicks, String subtitle)
    {
        mArticleID = articleID;
        mAuthorUID = authorUID;
        mTitle = title;
        mCategory = category;
        mUrlToImage = urlToImage;
        mNumberOfClicks = numberOfClicks;
        mSubtitle = subtitle;
    }

    public FeaturedArticle(String articleID, String authorUID, String title, String category, String urlToImage, long numberOfClicks)
    {
        mArticleID = articleID;
        mAuthorUID = authorUID;
        mTitle = title;
        mCategory = category;
        mUrlToImage = urlToImage;
        mNumberOfClicks = numberOfClicks;
    }

    public FeaturedArticle()
    {
    }

    public FeaturedArticle(String articleID, String title, String category, String urlToImage, long numberOfClicks)
    {
        mArticleID = articleID;
        mTitle = title;
        mCategory = category;
        mUrlToImage = urlToImage;
        mNumberOfClicks = numberOfClicks;
    }

    public FeaturedArticle(String articleID, String title, String category, String urlToImage)
    {
        mArticleID = articleID;
        mTitle = title;
        mCategory = category;
        mUrlToImage = urlToImage;
        mNumberOfClicks = 0;
    }

    public FeaturedArticle(String articleID, String title, String category)
    {
        mArticleID = articleID;
        mTitle = title;
        mCategory = category;
        mUrlToImage = null;
        mNumberOfClicks = 0;
    }

    public String getArticleID()
    {
        return mArticleID;
    }

    public void setArticleID(String articleID)
    {
        mArticleID = articleID;
    }


    public String getTitle()
    {
        return mTitle;
    }

    public void setTitle(String title)
    {
        mTitle = title;
    }

    public String getCategory()
    {
        return mCategory;
    }

    public void setCategory(String category)
    {
        mCategory = category;
    }

    public String getUrlToImage()
    {
        return mUrlToImage;
    }

    public void setUrlToImage(String urlToImage)
    {
        mUrlToImage = urlToImage;
    }

    public long getNumberOfClicks()
    {
        return mNumberOfClicks;
    }

    public void setNumberOfClicks(long numberOfClicks)
    {
        mNumberOfClicks = numberOfClicks;
    }

    public String getAuthorUID()
    {
        return mAuthorUID;
    }
}
