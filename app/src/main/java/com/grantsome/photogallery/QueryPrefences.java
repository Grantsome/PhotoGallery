package com.grantsome.photogallery;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by tom on 2017/4/20.
 */

public class QueryPrefences {

    private static final String PREF_SEARCH_QUERY = "searchQuery";

    private static final String PREF_LAST_RESULT_ID = "lastResultId";

    private static final String PREF_IS_ALARM_ON = "isAlarm";

    public static String getStoredQuery(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_SEARCH_QUERY,null);
    }

    public static void setStoredQuery(Context context,String query){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_SEARCH_QUERY,query).apply();
    }

    public static String getPrefLastResultId(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_LAST_RESULT_ID,null);
    }

    public static void setPrefLastResultId(Context context,String lastResultId) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(PREF_LAST_RESULT_ID,lastResultId).apply();
    }

    public static boolean isAlarmOn(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PREF_IS_ALARM_ON,false);
    }

    public static void seAlarmOn(Context context,boolean isOn){
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(PREF_IS_ALARM_ON,isOn).apply();
    }
}
