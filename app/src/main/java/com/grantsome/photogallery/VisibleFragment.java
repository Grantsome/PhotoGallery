package com.grantsome.photogallery;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by tom on 2017/4/22.
 */

public class VisibleFragment extends Fragment {

    private static final String TAG = "VisibleFragment";

    @Override
    public void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(PollService.ACTION_SHOW_NOTIFICATION);
        getActivity().registerReceiver(mBroadcastReceiver,filter,PollService.PREM_PRIVATE,null);
    }

    @Override
    public void onPause(){
        super.onPause();
        getActivity().unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Toast.makeText(context, "Got a Broadcast: " + intent.getAction(), Toast.LENGTH_SHORT).show();
            Log.i(TAG,"canceling notification");
            setResultCode(Activity.RESULT_CANCELED);
        }
    };
}
