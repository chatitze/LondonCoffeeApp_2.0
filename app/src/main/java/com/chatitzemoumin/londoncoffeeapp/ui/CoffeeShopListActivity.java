package com.chatitzemoumin.londoncoffeeapp.ui;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatitzemoumin.londoncoffeeapp.R;
import com.chatitzemoumin.londoncoffeeapp.model.CoffeeShop;
import com.chatitzemoumin.londoncoffeeapp.service.MyLocationService;
import com.chatitzemoumin.londoncoffeeapp.tasks.CoffeeShopFetcher;
import com.chatitzemoumin.londoncoffeeapp.util.ImageFetcher;
import com.chatitzemoumin.londoncoffeeapp.util.PlatformUtils;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Chatitze Moumin on 17/11/14.
 */

public class CoffeeShopListActivity extends AppCompatActivity {

    private static final String TAG = "CoffeeShopListActivity";
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private MyLocationService myLocationService;
    private List<CoffeeShop> coffeeShopList;


    private ImageFetcher mImageFetcher;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*if (BuildConfig.DEBUG) {
            PlatformUtils.enableStrictMode();
        }*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coffee_shop_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        // create location service
        myLocationService = new MyLocationService(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        checkPermission();

        // create coffee shop fetcher in order to load the coffee shop list data
        CoffeeShopFetcher fetcher = new CoffeeShopFetcher(this);
        fetcher.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.clear_cache:
                mImageFetcher.clearCache();
                Toast.makeText(getApplicationContext(), R.string.clear_cache_complete_toast,
                        Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void sortByDistance(LatLng latLng){
        if(latLng != null){
            double lat, lng;
            for(int i = 0; i < coffeeShopList.size(); i++){
                lat = coffeeShopList.get(i).getLat();
                lng = coffeeShopList.get(i).getLng();
                coffeeShopList.get(i).setDistance(distanceBetween(latLng, new LatLng(lat,lng)));
            }
            //Sorting
            Collections.sort(coffeeShopList, new Comparator<CoffeeShop>() {
                @Override
                public int compare(CoffeeShop shop1, CoffeeShop shop2) {

                    return (int) (shop1.getDistance() - shop2.getDistance());
                }
            });
        }
    }

    private float distanceBetween(LatLng latLng1, LatLng latLng2){
        float results [] = new float[3];
        Location.distanceBetween(latLng1.latitude, latLng1.longitude, latLng2.latitude, latLng2.longitude, results);
        return results[0];
    }

    private String formatDistance(double distance) {
        String result = "";

        DecimalFormat dF = new DecimalFormat("0");

        if (distance < 1000)
            result = dF.format(distance) + " m";
        else {
            dF.applyPattern("0.#");
            distance = distance / 1000.0;
            result   = dF.format(distance) + " km";
        }

        return result;
    }

    private void checkPermission(){

        // Check if the Location permission is already available.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
                // Location  permission has not been granted.

            /**
             * Requests the Location permission.
             * If the permission has been denied previously, a SnackBar will prompt the user to grant the
             * permission, otherwise it is requested directly.
             */
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                Snackbar.make(findViewById(android.R.id.content), R.string.permission_location_request,
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ActivityCompat.requestPermissions(CoffeeShopListActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                            }
                        })
                        .show();

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public void handleCoffeeShopList(final List<CoffeeShop> coffeeShopList) {
        this.coffeeShopList = coffeeShopList;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (myLocationService.canGetLocation()) {
                    // sort the venues through distance of users location
                    sortByDistance(new LatLng(myLocationService.getLatitude(), myLocationService.getLongitude()));
                }

                // specify the adapter
                mAdapter = new CardViewAdapter(getApplication(), coffeeShopList);
                mRecyclerView.setAdapter(mAdapter);

//                mImageFetcher.setImageSize(mRecyclerView.getWidth(), (int) (mRecyclerView.getWidth() / 2));


                /*

                // set markers on map
                for(int i=0; i<coffeeShopList.size(); i++){

                    String distance = formatDistance(coffeeShopList.get(i).getDistance());
                    String title = (i+1)+". "+coffeeShopList.get(i).getName();

                    mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(coffeeShopList.get(i).getLat(), coffeeShopList.get(i).getLng()))
                            .title(title)
                            .snippet(distance)
                            .alpha(0.7f));


                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {

                        char del = marker.getTitle().charAt(1);
                        int index = -1;
                        if(del == '.'){
                            index = Integer.parseInt(marker.getTitle().substring(0,1))-1;
                        }else{
                            index = Integer.parseInt(marker.getTitle().substring(0,2))-1;
                        }

                        final Intent i = new Intent(getApplicationContext(), CoffeeShopDetailActivity.class);
                        i.putExtra(CoffeeShopDetailActivity.VENUE_INDEX, (int) index);
                        i.putParcelableArrayListExtra(CoffeeShopDetailActivity.COFFEE_SHOP_LIST, (ArrayList<CoffeeShop>)coffeeShopList);

                        startActivity(i);
                    }
                });
                */
            }

        });
    }

    public void failedLoadingPosts() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(CoffeeShopListActivity.this, "Failed to load Coffee Shops. Have a look at LogCat.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
     * CardViewAdapter: a custom adapter extends from RecyclerView.Adapter
     *
     */
    private class CardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        private static final int TYPE_HEADER = 0;
        private static final int TYPE_ITEM = 1;

        private ArrayList<CoffeeShop> mCoffeeShopList;
        private final Context mContext;

        // Allows to remember the last item shown on screen
        private int lastPosition = -1;

        // Provide a suitable constructor (depends on the kind of data set)
        public CardViewAdapter(Context context, List<CoffeeShop> coffeeShopList) {
            mContext = context;
            mCoffeeShopList = (ArrayList<CoffeeShop>) coffeeShopList;


            // -------------------------------- library -----------------------------

            // UNIVERSAL IMAGE LOADER SETUP
           /* DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true).cacheInMemory(true)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .displayer(new FadeInBitmapDisplayer(300)).build();

            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                    getApplicationContext())
                    .defaultDisplayImageOptions(defaultOptions)
                    .memoryCache(new WeakMemoryCache())
                    .discCacheSize(100 * 1024 * 1024).build();

            ImageLoader.getInstance().init(config);
            // END - UNIVERSAL IMAGE LOADER SETUP

            mImageLoader=ImageLoader.getInstance();*/
            // -------------------------------- library -----------------------------

        }

        // Create new views (invoked by the layout manager)
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // create a new view
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view_coffee_shop, parent, false);

            CoffeeShopViewHolder vh = new CoffeeShopViewHolder(itemView);

            return (RecyclerView.ViewHolder)vh;

        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CoffeeShopViewHolder){

                // - get element from your data set at this position
                // - replace the contents of the view with that element
                ((CoffeeShopViewHolder)holder).mCoffeeShopAddress.setText(mCoffeeShopList.get(position).getAddress());
                ((CoffeeShopViewHolder)holder).mCoffeeShopName.setText(mCoffeeShopList.get(position).getName());
                ((CoffeeShopViewHolder)holder).mCoffeeShopDistance.setText(formatDistance(mCoffeeShopList.get(position).getDistance()));
                ((CoffeeShopViewHolder)holder).mCoffeeShopView.setScaleType(ImageView.ScaleType.CENTER_CROP);

//                mImageFetcher.setImageSize((int)(mRecyclerView.getWidth()/2));

                // Finally load the image asynchronously into the ImageView, this also takes care of
                // setting a placeholder image while the background thread runs
                // mImageFetcher.loadImage(mCoffeeShopList.get(position).getVenueUrl(), ((CoffeeShopViewHolder)holder).mCoffeeShopView);


                // -------------------------------- library -----------------------------

                Picasso.with(CoffeeShopListActivity.this)
                        .load(mCoffeeShopList.get(position).getVenueUrl())
                        .placeholder(R.drawable.place_holder_bitmap) // optional
                        .error(R.drawable.place_holder_bitmap)         // optional
                        .into(((CoffeeShopViewHolder)holder).mCoffeeShopView);
                //.resize(250, 200)                        // optional
                //.rotate(90)                             // optional

                // -------------------------------- library -----------------------------

                //mImageLoader.displayImage(mCoffeeShopList.get(position).getVenueUrl(),((CoffeeShopViewHolder)holder).mCoffeeShopView);





                // Here apply the animation when the view is bound
                setAnimation(((CoffeeShopViewHolder)holder).mContainer, position);
            }

        }

        private String formatDistance(double distance) {
            String result = "";

            DecimalFormat dF = new DecimalFormat("0");

            if (distance < 1000)
                result = dF.format(distance) + " m";
            else {
                dF.applyPattern("0.#");
                distance = distance / 1000.0;
                result   = dF.format(distance) + " km";
            }

            return result;
        }

        /**
         * Here is the key method to apply the animation
         */
        private void setAnimation(View viewToAnimate, int position)
        {
            // If the bound view wasn't previously displayed on screen, it's animated
            if (position > lastPosition)
            {
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_in_bottom);
                viewToAnimate.startAnimation(animation);
                lastPosition = position;
            }
        }

        // Return the size of your data set (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mCoffeeShopList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;

            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }

    }

    /*
     * CoffeeShopViewHolder: provides a reference to the views for each data item
     * Complex data items may need more than one view per item, and
     * you provide access to all the views for a data item in a view holder
     */
    private class CoffeeShopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        protected ImageView mCoffeeShopView;
        protected TextView mCoffeeShopName;
        protected TextView mCoffeeShopAddress;
        protected TextView mCoffeeShopDistance;
        protected CardView mContainer;

        public CoffeeShopViewHolder(View v) {
            super(v);

            mCoffeeShopView = (ImageView) v.findViewById(R.id.coffeeshop_image);
            mCoffeeShopName = (TextView) v.findViewById(R.id.coffeeshop_name);
            mCoffeeShopAddress = (TextView) v.findViewById(R.id.coffeeshop_address);
            mCoffeeShopDistance = (TextView) v.findViewById(R.id.coffeeshop_distance);
            mContainer = (CardView) v.findViewById(R.id.card_view);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final Intent i = new Intent(v.getContext(), CoffeeShopDetailActivity.class);
            i.putExtra(CoffeeShopDetailActivity.VENUE_INDEX, getPosition());
            i.putParcelableArrayListExtra(CoffeeShopDetailActivity.COFFEE_SHOP_LIST, (ArrayList<CoffeeShop>) coffeeShopList);

            if (PlatformUtils.hasJellyBean()) {
                ActivityOptions options =
                        ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
                v.getContext().startActivity(i, options.toBundle());
            } else {
                v.getContext().startActivity(i);
            }
        }
    }

}
