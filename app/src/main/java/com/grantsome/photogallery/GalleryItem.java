package com.grantsome.photogallery;

/**
 * Created by tom on 2017/4/8.
 */

public class GalleryItem {

    private String mCaption;

    private String mId;

    private String mUrl;

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

