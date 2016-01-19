package com.chatitzemoumin.londoncoffeeapp.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.chatitzemoumin.londoncoffeeapp.model.CoffeeShop;
import com.chatitzemoumin.londoncoffeeapp.ui.CoffeeShopListActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Chatitze Moumin on 17/12/14.
 */

public class CoffeeShopFetcher extends AsyncTask<Void,Void,String> {

    private static final String TAG = "CoffeeShopFetcher";
    public static final String SERVER_URL = "http://londoncoffeeapp.com/rest/coffeeshoplist";

    private CoffeeShopListActivity myActivity;

    //Whether to load the data from server or from a static file
    private boolean loadFromNetwork = false;

    public CoffeeShopFetcher(CoffeeShopListActivity activity){
        myActivity = activity;
    }

    @Override
    protected String doInBackground(Void... params) {

        InputStream content = null;
        try{
            if(loadFromNetwork){
                //Create an HTTP client
                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(SERVER_URL);

                //Perform the request and check the status code
                HttpResponse response = client.execute(post);
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == 200){
                    HttpEntity entity = response.getEntity();
                    content = entity.getContent();
                } else {
                    Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                    myActivity.failedLoadingPosts();
                }

            } else{
                content = myActivity.getAssets().open("MyFavouriteCoffeeShops.txt");
            }

            try {
                Reader reader = new InputStreamReader(content);

                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                Gson gson = gsonBuilder.create();
                List<CoffeeShop> coffeeShops = new ArrayList<CoffeeShop>(Arrays.asList(gson.fromJson(reader, CoffeeShop[].class)));
                content.close();

                myActivity.handleCoffeeShopList(coffeeShops);
            } catch (Exception ex) {
                Log.e(TAG, "Failed to parse JSON due to: " + ex);
                myActivity.failedLoadingPosts();
            }

        }catch(Exception ex) {
            Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
            myActivity.failedLoadingPosts();
        }

        return null;
    }

    private void loadCoffeeShopsFromServer(){

        try{
            //Create an HTTP client
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(SERVER_URL);

            //Perform the request and check the status code
            HttpResponse response = client.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == 200){
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();

                try {
                    //Read the server response and attempt to parse it as JSON
                    Reader reader = new InputStreamReader(content);

                    GsonBuilder gsonBuilder = new GsonBuilder();
                    gsonBuilder.setDateFormat("M/d/yy hh:mm a");
                    Gson gson = gsonBuilder.create();
                    List<CoffeeShop> coffeeShopList = new ArrayList<CoffeeShop>();
                    coffeeShopList = Arrays.asList(gson.fromJson(reader, CoffeeShop[].class));
                    content.close();

                    myActivity.handleCoffeeShopList(coffeeShopList);
                } catch (Exception ex) {
                    Log.e(TAG, "Failed to parse JSON due to: " + ex);
                    myActivity.failedLoadingPosts();
                }
            } else {
                Log.e(TAG, "Server responded with status code: " + statusLine.getStatusCode());
                myActivity.failedLoadingPosts();
            }
        }catch(Exception ex) {
            Log.e(TAG, "Failed to send HTTP POST request due to: " + ex);
            myActivity.failedLoadingPosts();
        }
    }
}
