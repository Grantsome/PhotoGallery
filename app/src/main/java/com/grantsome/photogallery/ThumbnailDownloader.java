package com.grantsome.photogallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tom on 2017/4/9.
 */

public class ThumbnailDownloader<T> extends HandlerThread {

    public static final String TAG = "ThumbnailDownloader";

    private static int MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;

    private Handler mResponseHandler;

    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();

    private boolean mHashQuit = false;

    private ThumbnailDownloaderListener<T> mThumbnailDownloader;

    public interface ThumbnailDownloaderListener<T>{
        void onThumbnailDownloaded(T target,Bitmap thumbnail);
    }

    public void setThumbnailDownloadListener(ThumbnailDownloaderListener<T> listener){
        mThumbnailDownloader = listener;
    }

    public ThumbnailDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    @Override
    protected void onLooperPrepared(){
        mRequestHandler = new Handler(){
           @Override
            public void handleMessage(Message msg){
               if(msg.what == MESSAGE_DOWNLOAD){
                   T target = (T) msg.obj;
                   Log.i(TAG,"Got a request for URL: " + mRequestMap.get(target));
                   handleRequest(target);
               }
           }
        };
    }

    private void handleRequest(final T target){
        try{
            final String url = mRequestMap.get(target);
            if(url==null){
                return;
            }
            byte[] bitmapBytes = new HttpUtil().getUrlBytes(url);

            final Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes,0,bitmapBytes.length);
            Log.i(TAG,"Bitmap created");

            mResponseHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mRequestMap.get(target)!=url||mHashQuit){
                        return;
                    }
                    mRequestMap.remove(target);
                    mThumbnailDownloader.onThumbnailDownloaded(target,bitmap);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean quit(){
        mHashQuit = true;
        return super.quit();
    }

    public void queueThumbnail(T target,String url){
        Log.i(TAG,"Got a URL " + url);
        if(url == null){
            mRequestMap.remove(target,url);
        }else {
            mRequestMap.put(target,url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD,target).sendToTarget();
        }
    }

    public void clearQueue(){
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }



}
