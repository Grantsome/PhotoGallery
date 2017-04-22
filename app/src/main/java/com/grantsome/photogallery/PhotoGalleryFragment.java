package com.grantsome.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;


public class PhotoGalleryFragment extends VisibleFragment {

    public static final String TAG = "PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;

    private List<GalleryItem> mItems  = new ArrayList<>();

    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader;

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        updateItems();
        //Intent i = PollService.newIntent(getActivity());
        //getActivity().startService(i);
        //PollService.setServiceAlarm(getActivity(),true);

        Handler handler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(handler);
        mThumbnailDownloader.setThumbnailDownloadListener(new ThumbnailDownloader.ThumbnailDownloaderListener<PhotoHolder>() {
            @Override
            public void onThumbnailDownloaded(PhotoHolder target, Bitmap thumbnail) {
                Drawable drawable = new BitmapDrawable(getResources(),thumbnail);
                target.bindDrawable(drawable);
            }
        });
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG,"Background thread started ");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater){
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery,menu);
        /*
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG,"QueryTextSubmit:"+query);
                QueryPrefences.setStoredQuery(getActivity(),query);
                Log.d(TAG,"QueryTextSubmit is executed");
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG,"QueryTextChange:"+newText);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String query = QueryPrefences.getStoredQuery(getActivity());
                searchView.setQuery(query,false);
            }
        });
        searchView.setSubmitButtonEnabled(true);
        searchView.setIconifiedByDefault(false);
        */
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                QueryPrefences.setStoredQuery(getActivity(), s);
                updateItems();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = QueryPrefences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        } else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem meunItem){
      switch (meunItem.getItemId()){
          case R.id.menu_item_clear:
              QueryPrefences.setStoredQuery(getActivity(),null);
              updateItems();
              return true;
          case R.id.menu_item_toggle_polling:
              boolean shouldStartAlarm =! PollService.isServiceAlarmOn(getActivity());
              PollService.setServiceAlarm(getActivity(),shouldStartAlarm);
              getActivity().invalidateOptionsMenu();
              return true;
          default:
              return super.onOptionsItemSelected(meunItem);
      }
    }

    private void updateItems(){
        String query = QueryPrefences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView) view.findViewById(R.id.fragment_photo_gallery_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        setupAdapter();
        return view;
    }

    private void setupAdapter(){
        if(isAdded()){
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mItems));
        }
    }

   private class FetchItemsTask extends AsyncTask<Void,Void,List<GalleryItem>>{

       private String mQuery;

       public FetchItemsTask(String query){
           mQuery = query;
       }

       @Override
       protected List<GalleryItem> doInBackground(Void... params) {
          // String query = "robot";

           if(mQuery==null){
               return new HttpUtil().fetchRecentPhotos();
           }else {
               return new HttpUtil().searchPhotos(mQuery);
           }
       }

       @Override
       protected void onPostExecute(List<GalleryItem> items){
           mItems = items;
           setupAdapter();
       }
   }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mItemImageView;

        private GalleryItem mGalleryItem;

        public PhotoHolder(View itemView) {
            super(itemView);
            mItemImageView = (ImageView) itemView.findViewById(R.id.fragment_photo_gallery_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable){
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(GalleryItem item){
            mGalleryItem = item;
        }

        @Override
        public void onClick(View v) {
            //Intent intent = new Intent(Intent.ACTION_VIEW,mGalleryItem.getPhotoPageUri());
            Intent intent = PhotoPageActivity.newIntent(getActivity(),mGalleryItem.getPhotoPageUri());
            startActivity(intent);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder>{

        private List<GalleryItem> mGalleryItemList;

        public PhotoAdapter(List<GalleryItem> galleryItemList){
            mGalleryItemList = galleryItemList;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view  = LayoutInflater.from(getActivity()).inflate(R.layout.gallery_item,parent,false);
            return new PhotoHolder(view);
        }


        @Override
        public void onBindViewHolder(PhotoHolder holder, int position) {
            GalleryItem galleryItem = mGalleryItemList.get(position);
            Drawable placeDrawable = ContextCompat.getDrawable(getActivity(),R.drawable.loading);
            holder.bindDrawable(placeDrawable);
            holder.bindGalleryItem(galleryItem);
            mThumbnailDownloader.queueThumbnail(holder,galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItemList.size();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG,"Background thread destroyed");
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        mThumbnailDownloader.clearQueue();
    }

}
