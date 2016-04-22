package com.chatitzemoumin.londoncoffeeapp.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatitzemoumin.londoncoffeeapp.R;
import com.chatitzemoumin.londoncoffeeapp.model.CoffeeShop;
import com.chatitzemoumin.londoncoffeeapp.util.ImageFetcher;
import com.chatitzemoumin.londoncoffeeapp.util.ImageWorker;
import com.chatitzemoumin.londoncoffeeapp.util.PlatformUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

/**
 * Created by Chatitze Moumin on 17/11/14.
 */

/**
 * This fragment will populate the children of the ViewPager from {@link CoffeeShopDetailActivity}.
 */
public class CoffeeShopDetailFragment extends Fragment{
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    public static final String COFFEE_SHOP = "coffee_shop";

    //private String mImageUrl;
    private CoffeeShop mCoffeeShop;

    private ImageView mImageView;
    private ImageView mTwitterImageView;
    private ImageView mInstagramImageView;
    private ImageView mFacebookImageView;

    private ImageView mHeartImageView;

    private ImageFetcher mImageFetcher;

    private TextView mCoffeeShopName;
    private TextView mCoffeeShopAddress;
    private TextView mCoffeeShopRoasters;
    private TextView mCoffeeShopRating;
    private TextView mCoffeeShopMachine;
    private TextView mCoffeeShopGrinder;
    private TextView mCoffeeShopWebAddress;
    private TextView mCoffeeShopOpeningHours;
    private TextView mCoffeeShopContacts;
    private TextView mCoffeeShopComments;
    private TextView mCoffeeShopPhone;

    private ImageLoader mImageLoader;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param coffeeShop The coffee shop list
     * @return A new instance of CoffeeShopDetailFragment with coffeeShopList extras
     */
    /*public static CoffeeShopDetailFragment newInstance(String imageUrl) {
        final CoffeeShopDetailFragment f = new CoffeeShopDetailFragment();

        final Bundle args = new Bundle();
        args.putString(IMAGE_DATA_EXTRA, imageUrl);
        f.setArguments(args);

        return f;
    }*/
    public static CoffeeShopDetailFragment newInstance(CoffeeShop coffeeShop) {
        final CoffeeShopDetailFragment f = new CoffeeShopDetailFragment();

        final Bundle args = new Bundle();
        //args.putString(IMAGE_DATA_EXTRA, imageUrl);
        args.putParcelable(COFFEE_SHOP, coffeeShop);
        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public CoffeeShopDetailFragment() {}

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link CoffeeShopDetailFragment#newInstance(com.chatitzemoumin.londoncoffeeapp.model.CoffeeShop)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mImageUrl = getArguments() != null ? getArguments().getString(IMAGE_DATA_EXTRA) : null;

        Bundle bundle = this.getArguments();
        mCoffeeShop = bundle.getParcelable(COFFEE_SHOP);


        // UNIVERSAL IMAGE LOADER SETUP
        /*DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheOnDisc(true).cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getActivity().getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024).build();

        ImageLoader.getInstance().init(config);
        // END - UNIVERSAL IMAGE LOADER SETUP

        mImageLoader=ImageLoader.getInstance();*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.coffeeshop_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        //mImageView.setMaxHeight((int)(v.getWidth()/2));
        //mImageView.setElevation((float)4.0);
        //mImageView.setTranslationZ((float) 20);

        mTwitterImageView = (ImageView) v.findViewById(R.id.twitter_image);
        mFacebookImageView = (ImageView) v.findViewById(R.id.fbook_image);
        mInstagramImageView = (ImageView) v.findViewById(R.id.instagram_image);

        mHeartImageView = (ImageView) v.findViewById(R.id.heart_image);

        mCoffeeShopName             = (TextView) v.findViewById(R.id.coffeeshop_name);
        mCoffeeShopAddress          = (TextView) v.findViewById(R.id.coffeeshop_address);
//        mCoffeeShopRoasters         = (TextView) v.findViewById(R.id.);
//        mCoffeeShopRating         = (TextView) v.findViewById(R.id);
//        mCoffeeShopMachine        = (TextView) v.findViewById(R.id);
//        mCoffeeShopGrinder        = (TextView) v.findViewById(R.id);
        mCoffeeShopWebAddress     = (TextView) v.findViewById(R.id.coffeeshop_web);
        mCoffeeShopOpeningHours   = (TextView) v.findViewById(R.id.coffeeshop_openingHours);
//        mCoffeeShopContacts       = (TextView) v.findViewById(R.id);
        mCoffeeShopComments       = (TextView) v.findViewById(R.id.coffeeshop_comment);
        mCoffeeShopPhone            = (TextView) v.findViewById(R.id.coffeeshop_phone);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String phone, webAddress, linkText, twitterAddress, facebookAddress, instagramAddress;

        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (CoffeeShopDetailActivity.class.isInstance(getActivity())) {
            //mImageFetcher = ((CoffeeShopDetailActivity) getActivity()).getImageFetcher();
            //mImageFetcher.setImageSize(getView().getWidth(), (int)(getView().getWidth()/2));
            //mImageFetcher.loadImage(mCoffeeShop.getCoffeeUrl(), mImageView);

            Picasso.with(getActivity())
                    .load(mCoffeeShop.getCoffeeUrl())
                    .placeholder(R.drawable.place_holder_bitmap) // optional
                    .error(R.drawable.place_holder_bitmap)         // optional
                    .into(mImageView);

            //mImageLoader.displayImage(mCoffeeShop.getCoffeeUrl(),mImageView);

            // --- Hide the HEART image for now as iy's not used ---
            mHeartImageView.setVisibility(View.GONE);

            mCoffeeShopName.setText(mCoffeeShop.getName());
            mCoffeeShopAddress.setText(mCoffeeShop.getAddress());
            mCoffeeShopComments.setText(mCoffeeShop.getCommentList().get(0).getContent()
                                        +" "+ mCoffeeShop.getCommentList().get(0).getSource());
            mCoffeeShopOpeningHours.setText(mCoffeeShop.getOpeningHours());


            webAddress = mCoffeeShop.getWebAddress();
            phone =  mCoffeeShop.getContact().getFormattedPhone();
            if(webAddress != null && !webAddress.isEmpty()){
                linkText = "Web      : <a href=\"http://" + webAddress + "\">" + webAddress + "</a>";
                mCoffeeShopWebAddress.setText(Html.fromHtml(linkText));
                mCoffeeShopWebAddress.setMovementMethod(LinkMovementMethod.getInstance());
            }else
                mCoffeeShopWebAddress.setVisibility(View.GONE);
            if(phone != null && !phone.isEmpty()){
                mCoffeeShopPhone.setText("Phone   : " + mCoffeeShop.getContact().getFormattedPhone());
            } else
                mCoffeeShopPhone.setVisibility(View.GONE);



            twitterAddress = mCoffeeShop.getContact().getTwitter();
            facebookAddress = mCoffeeShop.getContact().getFacebook();
            instagramAddress = mCoffeeShop.getContact().getInstagram();

            if(twitterAddress != null && !twitterAddress.isEmpty()){
                mTwitterImageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        try{
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" +mCoffeeShop.getContact().getTwitter()));
                            startActivity(intent);

                        }catch(Exception e){
                            Log.e("CoffeeShopDetailFragm..", "Error in loading Twitter page");
                        }
                    }
                });
            }else mTwitterImageView.setVisibility(View.GONE);

            if(instagramAddress != null && !instagramAddress.isEmpty()){
                mInstagramImageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        try{
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" +mCoffeeShop.getContact().getInstagram()));
                            startActivity(intent);

                        }catch(Exception e){
                            Log.e("CoffeeShopDetailFragm..", "Error in loading Instagram page");
                        }
                    }
                });
            }else mInstagramImageView.setVisibility(View.GONE);

            if(facebookAddress != null && !facebookAddress.isEmpty()){
                mFacebookImageView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        try{
                            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://" + mCoffeeShop.getContact().getFacebook().substring(17)));
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + mCoffeeShop.getContact().getFacebook()));
                            startActivity(intent);

                        }catch(Exception e){
                            Log.e("CoffeeShopDetailFragm..", "Error in loading Facebook page");
                            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://" + mCoffeeShop.getContact().getFacebook())));
                        }
                    }
                });
            }else mFacebookImageView.setVisibility(View.GONE);

        }

        // Pass clicks on the ImageView to the parent activity to handle
        if (OnClickListener.class.isInstance(getActivity()) && PlatformUtils.hasHoneycomb()) {
            mImageView.setOnClickListener((OnClickListener) getActivity());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            ImageWorker.cancelWork(mImageView);
            mImageView.setImageDrawable(null);
        }
    }
}
