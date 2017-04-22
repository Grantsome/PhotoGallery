package com.grantsome.photogallery;

import android.net.Uri;

/**
 * Created by tom on 2017/4/8.
 */

public class GalleryItem {

    private String mCaption;

    private String mId;

    private String mUrl;

    private String mOwner;

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public Uri getPhotoPageUri(){
        return Uri.parse("http://www.flickr.com/photos/").buildUpon().appendPath(mOwner).appendPath(mId).build();
    }

    public String toString() {
        return mCaption;
    }

    public String getId() {
        return mId;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setUrl(String url) {
        mUrl = url;
    }

}

