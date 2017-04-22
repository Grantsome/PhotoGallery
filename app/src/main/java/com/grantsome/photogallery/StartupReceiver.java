package com.grantsome.photogallery;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by tom on 2017/4/22.
 */

public class StartupReceiver extends BroadcastReceiver {

    private static final String TAG = "StartReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG,"Received broadcase intent : " + intent.getAction());
        boolean isOn = QueryPrefences.isAlarmOn(context);
        PollService.setServiceAlarm(context,isOn);
    }
}
