package com.grantsome.photogallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by tom on 2017/2/15.
 */

public class PreUtil {

    public static void putStringToDefault(Context context,String key,String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putString(key,value).commit();
    }

    public static String getStringFromDefault(Context context,String key,String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key,value);
    }

}
