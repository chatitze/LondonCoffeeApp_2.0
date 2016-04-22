package com.chatitzemoumin.londoncoffeeapp.ui;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import com.chatitzemoumin.londoncoffeeapp.BuildConfig;
import com.chatitzemoumin.londoncoffeeapp.R;
import com.chatitzemoumin.londoncoffeeapp.model.CoffeeShop;
import com.chatitzemoumin.londoncoffeeapp.util.PlatformUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Chatitze Moumin on 24/01/15.
 */
public class CoffeeShopMapActivity extends Activity implements
        com.google.android.gms.location.LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "CoffeeShopMapActivity";
    public static final String COFFEE_SHOP_LIST = "coffee_shop_list";

    private ArrayList<CoffeeShop> mCoffeeShopList;

    private static final LatLng HOLBORN = new LatLng(51.517580, -0.120450);

    private static final float TWENTYFIVE_METERS = 25f;
    private static final long FIVE_MIUTES = 5 * 60 * 1000;

    private MapFragment mMapFragment;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;

    LocationRequest mLocationRequest;

    public CoffeeShopMapActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            PlatformUtils.enableStrictMode();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.coffeeshop_map_fragment);

        mCoffeeShopList = getIntent().getParcelableArrayListExtra(COFFEE_SHOP_LIST);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("myCoffeeShopList", this.mCoffeeShopList);

        mMapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        GoogleMap map = mMapFragment.getMap();
        mMap = map;
        //map.setTrafficEnabled(true);
        CameraPosition cameraPosition = new CameraPosition.Builder().
                target(HOLBORN).
                zoom(11).
                //bearing(90).
                tilt(30).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        map.setIndoorEnabled(true);

        for(int i=0; i<mCoffeeShopList.size(); i++){

            String distance = formatDistance(mCoffeeShopList.get(i).getDistance());
            String title = (i+1)+". "+mCoffeeShopList.get(i).getName();

            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(mCoffeeShopList.get(i).getLat(), mCoffeeShopList.get(i).getLng()))
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
                i.putParcelableArrayListExtra(CoffeeShopDetailActivity.COFFEE_SHOP_LIST, (ArrayList<CoffeeShop>)mCoffeeShopList);

                startActivity(i);
                /*
                if (PlatformUtils.hasJellyBean()) {
                    ActivityOptions options =
                            ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getWidth(), v.getHeight());
                    startActivity(i, options.toBundle());
                } else {
                    startActivity(i);
                }
                */
            }
        });

        //mLocationCallbacks = new MyLocationCallbacks();

        mLocationRequest = LocationRequest.create();
        //mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //mLocationRequest.setInterval(POLLING_FREQ);
        //mLocationRequest.setFastestInterval(FASTEST_UPDATE_FREQ);

        mLocationRequest.setSmallestDisplacement(TWENTYFIVE_METERS);
        mLocationRequest.setExpirationDuration(FIVE_MIUTES);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
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
    @Override
    protected void onStart() {
        super.onResume();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onPause();

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mGoogleApiClient.isConnected()) {
           LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),
                location.getLongitude());
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)
                .zoom(13)
                //.bearing(90)
                //.tilt(30)
                .build();
        mMap.animateCamera(CameraUpdateFactory.
                newCameraPosition(cameraPosition));
    }

    @Override
    public void onConnected(Bundle dataBundle) {

        // Get first reading. Get additional location updates if necessary
        if (servicesAvailable()) {

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private Location bestLastKnownLocation(float minAccuracy, long minTime) {
        Location bestResult = null;
        float bestAccuracy = Float.MAX_VALUE;
        long bestTime = Long.MIN_VALUE;

        // Get the best most recent location currently available
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mCurrentLocation != null) {
            float accuracy = mCurrentLocation.getAccuracy();
            long time = mCurrentLocation.getTime();

            if (accuracy < bestAccuracy) {
                bestResult = mCurrentLocation;
                bestAccuracy = accuracy;
                bestTime = time;
            }
        }

        // Return best reading or null
        if (bestAccuracy > minAccuracy || bestTime < minTime) {
            return null;
        }
        else {
            return bestResult;
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private boolean servicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
        }
        else {
            GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0).show();
            return false;
        }
    }

}
