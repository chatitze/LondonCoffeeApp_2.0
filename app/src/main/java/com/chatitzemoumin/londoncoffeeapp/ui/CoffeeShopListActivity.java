package com.chatitzemoumin.londoncoffeeapp.ui;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatitzemoumin.londoncoffeeapp.BuildConfig;
import com.chatitzemoumin.londoncoffeeapp.R;
import com.chatitzemoumin.londoncoffeeapp.model.CoffeeShop;
import com.chatitzemoumin.londoncoffeeapp.service.MyLocationService;
import com.chatitzemoumin.londoncoffeeapp.tasks.CoffeeShopFetcher;
import com.chatitzemoumin.londoncoffeeapp.util.ImageCache;
import com.chatitzemoumin.londoncoffeeapp.util.ImageFetcher;
import com.chatitzemoumin.londoncoffeeapp.util.PlatformUtils;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Chatitze Moumin on 17/11/14.
 */

public class CoffeeShopListActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "CoffeeShopListActivity";
    private static final String IMAGE_CACHE_DIR = "CoffeeVenues";

    private MyLocationService myLocationService;
    private List<CoffeeShop> coffeeShopList;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private int mVenueImageSize;
    private ImageFetcher mImageFetcher;

    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private GoogleApiClient mGoogleApiClient;

    private MapFragment mMapFragment;
    private GoogleMap mMap;

    private static final LatLng HOLBORN = new LatLng(51.517580, -0.120450);

    private static final int REQUEST_INVITE = 1;

    // ------- library -------
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            PlatformUtils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);

        // ------------------ Sliding Up Panel ---------------
        setContentView(R.layout.slidingup_panel_view);
        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        mSlidingUpPanelLayout.setPanelSlideListener(new PanelSlideListener() {
            TextView t = (TextView) findViewById(R.id.expand_collapse_text);
            ImageView imageView = (ImageView) findViewById(R.id.expand_collapse_icon);

            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");
                t.setText(Html.fromHtml(getString(R.string.action_collapse)));
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_collapse));

            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
                t.setText(Html.fromHtml(getString(R.string.action_expand)));
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand));

            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
                t.setText(Html.fromHtml(getString(R.string.action_collapse)));
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_collapse));
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });


        TextView t = (TextView) findViewById(R.id.expand_collapse_text);
        t.setText(Html.fromHtml(getString(R.string.action_expand)));

        ImageView imageView = (ImageView) findViewById(R.id.expand_collapse_icon);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_expand));

        // ------------------ Sliding Up Panel END ---------------

        // create location service
        myLocationService = new MyLocationService(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getApplicationContext());

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // create memory cache parameters
        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getApplicationContext(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getApplicationContext(), mVenueImageSize);
        mImageFetcher.setLoadingImage(R.drawable.place_holder_bitmap);
        mImageFetcher.addImageCache(getSupportFragmentManager(), cacheParams);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        //mMapFragment = SupportMapFragment.newInstance();
        GoogleMap map = mMapFragment.getMap();
        mMap = map;
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(HOLBORN).
                zoom(11).tilt(30).build();

        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(true);

        // create coffee shop fetcher in order to load the coffee shop list data
        CoffeeShopFetcher fetcher = new CoffeeShopFetcher(this);
        fetcher.execute();


        // ---------------- App Invitation -------------------

        // Create an auto-managed GoogleApiClient with acccess to App Invites.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        boolean autoLaunchDeepLink = true;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                // Because autoLaunchDeepLink = true we don't have to do anything
                                // here, but we could set that to false and manually choose
                                // an Activity to launch to handle the deep link here.
                            }
                        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if(null != mImageFetcher){
            mImageFetcher.setExitTasksEarly(false);

        }
        if(null != mAdapter){
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        MenuItem item = menu.findItem(R.id.action_toggle);
        if (mSlidingUpPanelLayout != null) {
            if (mSlidingUpPanelLayout.getPanelState() == PanelState.HIDDEN) {
                item.setTitle(R.string.action_show);
            } else {
                item.setTitle(R.string.action_hide);
            }
        }
        return true;
    }

    /**
     * User has clicked the 'Invite a friend' option from menu, launch the invitation UI with the proper
     * title, message, and deep link
     */
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                //.setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.send_invitation:
                onInviteClicked();
                return true;
            case R.id.clear_cache:
                mImageFetcher.clearCache();
                Toast.makeText(getApplicationContext(), R.string.clear_cache_complete_toast,
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_map_list:
                final Intent intent = new Intent(getApplicationContext(), CoffeeShopMapActivity.class);
                intent.putParcelableArrayListExtra(CoffeeShopDetailActivity.COFFEE_SHOP_LIST, (ArrayList<CoffeeShop>)coffeeShopList);

                if (PlatformUtils.hasJellyBean()) {
                    View currentView = this.findViewById(android.R.id.content);
                    ActivityOptions options =
                            ActivityOptions.makeScaleUpAnimation(currentView, 0, 0, currentView.getWidth(), currentView.getHeight());
                    this.startActivity(intent, options.toBundle());
                } else {
                    startActivity(intent);
                }
                break;

            case R.id.action_toggle:
                if (mSlidingUpPanelLayout != null) {
                    if (mSlidingUpPanelLayout.getPanelState() != PanelState.HIDDEN) {
                        mSlidingUpPanelLayout.setPanelState(PanelState.HIDDEN);
                        item.setTitle(R.string.action_show);
                    } else {
                        mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_hide);
                    }
                }
                break;

            case R.id.action_anchor:
                if (mSlidingUpPanelLayout != null) {
                    if (mSlidingUpPanelLayout.getAnchorPoint() == 1.0f) {
                        mSlidingUpPanelLayout.setAnchorPoint(0.6f);
                        mSlidingUpPanelLayout.setPanelState(PanelState.ANCHORED);
                        item.setTitle(R.string.action_anchor_disable);
                    } else {
                        mSlidingUpPanelLayout.setAnchorPoint(1.0f);
                        mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
                        item.setTitle(R.string.action_anchor_enable);
                    }
                }
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        if (mSlidingUpPanelLayout != null &&
                (mSlidingUpPanelLayout.getPanelState() == PanelState.EXPANDED || mSlidingUpPanelLayout.getPanelState() == PanelState.ANCHORED)) {
            mSlidingUpPanelLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log a message
                // The ids array contains the unique invitation ids for each invitation sent
                // (one for each contact select by the user). You can use these for analytics
                // as the ID will be consistent on the sending and receiving devices.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, getString(R.string.sent_invitations_fmt, ids.length));
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Toast.makeText(CoffeeShopListActivity.this, getString(R.string.send_failed), Toast.LENGTH_SHORT).show();
            }
        }
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

                mImageFetcher.setImageSize(mRecyclerView.getWidth(), (int) (mRecyclerView.getWidth() / 2));


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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(CoffeeShopListActivity.this, getString(R.string.google_play_services_error), Toast.LENGTH_SHORT).show();
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
                    .inflate(R.layout.coffeeshop_card_view, parent, false);

            CoffeeShopViewHolder vh = new CoffeeShopViewHolder(itemView);

            return (RecyclerView.ViewHolder)vh;

        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof CoffeeShopViewHolder){

                // - get element from your data set at this position
                // - replace the contents of the view with that element

                double distanceFrom = mCoffeeShopList.get(position).getDistance();
                if(distanceFrom > 0)
                    ((CoffeeShopViewHolder)holder).mCoffeeShopDistance.setText(formatDistance(distanceFrom));
                else
                    ((CoffeeShopViewHolder)holder).mCoffeeShopDistance.setVisibility(View.GONE);

                // --- Hide the HEART image for now as iy's not used ---
                ((CoffeeShopViewHolder)holder).mHeartImageView.setVisibility(View.GONE);

                ((CoffeeShopViewHolder)holder).mCoffeeShopAddress.setText(mCoffeeShopList.get(position).getAddress());
                ((CoffeeShopViewHolder)holder).mCoffeeShopName.setText(mCoffeeShopList.get(position).getName());
                ((CoffeeShopViewHolder)holder).mCoffeeShopView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                mImageFetcher.setImageSize((int)(mRecyclerView.getWidth()/2));

                // Finally load the image asynchronously into the ImageView, this also takes care of
                // setting a placeholder image while the background thread runs
                //mImageFetcher.loadImage(mCoffeeShopList.get(position).getVenueUrl(), ((CoffeeShopViewHolder)holder).mCoffeeShopView);


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
        protected ImageView mHeartImageView;

        public CoffeeShopViewHolder(View v) {
            super(v);

            mCoffeeShopView = (ImageView) v.findViewById(R.id.coffeeshop_image);
            mCoffeeShopName = (TextView) v.findViewById(R.id.coffeeshop_name);
            mCoffeeShopAddress = (TextView) v.findViewById(R.id.coffeeshop_address);
            mCoffeeShopDistance = (TextView) v.findViewById(R.id.coffeeshop_distance);
            mContainer = (CardView) v.findViewById(R.id.card_view);
            mHeartImageView = (ImageView) v.findViewById(R.id.heart_image);

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