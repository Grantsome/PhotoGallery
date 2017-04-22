package com.grantsome.photogallery;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by tom on 2017/4/8.
 */

public class HttpUtil {

    public static final String TAG = "HttpUtil";

    public static final String API_KEY = "89b13c41adafc85b80edbadc4a655b60";

    public static final String FECTH_RECENTS_METHOD = "flickr.photos.getRecent";

    public static final String SEARCH_METHOD = "flickr.photos.search";

    private static final Uri ENDPOINT = Uri.parse("https://api.flickr.com/services/rest/").buildUpon().appendQueryParameter("api_key",API_KEY).appendQueryParameter("format","json").appendQueryParameter("nojsoncallback","1").appendQueryParameter("extras","url_s").build();

    public byte[] getUrlBytes(String urlSpec) throws IOException, NoSuchAlgorithmException, KeyManagementException {
        URL url = new URL(urlSpec);
        HttpsURLConnection connection = (HttpsURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException, KeyManagementException, NoSuchAlgorithmException {
        return new String(getUrlBytes(urlSpec));
    }

    public List<GalleryItem> fetchRecentPhotos(){
        String url = buildUrl(FECTH_RECENTS_METHOD,null);
        return downloadGalleryItems(url);
    }

    public List<GalleryItem> searchPhotos(String query){
        String url = buildUrl(SEARCH_METHOD,query);
        return downloadGalleryItems(url);
    }

    private String buildUrl(String method,String query) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon().appendQueryParameter("method", method);
        if (method.equals(SEARCH_METHOD)) {
            uriBuilder.appendQueryParameter("text", query);
        }

        return uriBuilder.build().toString();
    }

    private List<GalleryItem> downloadGalleryItems(String url){

        List<GalleryItem> galleryItemList = new ArrayList<>();

        try {
            //PreUtil.getStringFromDefault(AppContext.getInstance(), TAG, "") == null

                //String url = Uri.parse("https://api.flickr.com/services/rest/").buildUpon().appendQueryParameter("method", "flickr.photos.getRecent").appendQueryParameter("api_key", API_KEY).appendQueryParameter("format", "json").appendQueryParameter("nojsoncallback", "1").appendQueryParameter("extras", "url_s").build().toString();
                Log.i(TAG, "HttpUrl: " + url);
                String jsonString = getUrlString(url);
                PreUtil.putStringToDefault(AppContext.getInstance(), TAG, jsonString);
                Log.i(TAG, "Receive Json From Internet " + jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                parseItems(galleryItemList,jsonObject);

                /*
                String jsonString = PreUtil.getStringFromDefault(AppContext.getInstance(), TAG, "");
                Log.i(TAG, "Receive Json From Shared in else" + jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                parseItems(galleryItemList,jsonObject);
                */
        }catch (Exception e){
            e.printStackTrace();
        }
        return galleryItemList;
    }

    public void parseItems(List<GalleryItem> items,JSONObject jsonObject){
        try{
            JSONObject photosJsonObject = jsonObject.getJSONObject("photos");
            JSONArray photosJsonArray = photosJsonObject.getJSONArray("photo");
            for(int i=0;i<photosJsonArray.length();i++){
                JSONObject photoJsonObject = photosJsonArray.getJSONObject(i);
                GalleryItem item = new GalleryItem();
                item.setId(photoJsonObject.getString("id"));
                item.setCaption(photoJsonObject.getString("title"));
                if(!photoJsonObject.has("url_s")){
                    continue;
                }
                item.setUrl(photoJsonObject.getString("url_s"));
                item.setOwner(photoJsonObject.getString("owner"));
                items.add(item);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isNetWorkConntected(Context context){
        if(context!=null){
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null){
                return networkInfo.isAvailable();
            }
        }
        return false;
    }
}
