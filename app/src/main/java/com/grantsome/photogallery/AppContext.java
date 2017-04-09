package com.grantsome.photogallery;

import android.app.Application;

/**
 * Created by tom on 2017/3/30.
 */

public class AppContext extends Application {
    private static AppContext instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static AppContext getInstance(){
        // 这里不用判断instance是否为空
        return instance;
    }
}