package com.grantsome.photogallery;

import android.support.v4.app.Fragment;

public class PhotoGalleryActivity extends SuperFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
