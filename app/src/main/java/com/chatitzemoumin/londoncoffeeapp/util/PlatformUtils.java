package com.chatitzemoumin.londoncoffeeapp.util;


import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.os.Build.VERSION_CODES;

import com.chatitzemoumin.londoncoffeeapp.ui.CoffeeShopListActivity;
import com.chatitzemoumin.londoncoffeeapp.ui.CoffeeShopDetailActivity;


/**
 * Created by Chatitze Moumin on 05/12/14.
 */
public class PlatformUtils {

    @TargetApi(VERSION_CODES.HONEYCOMB)
    public static void enableStrictMode() {
        if (PlatformUtils.hasGingerbread()) {
            StrictMode.ThreadPolicy.Builder threadPolicyBuilder =
                    new StrictMode.ThreadPolicy.Builder()
                            .detectAll()
                            .penaltyLog();
            StrictMode.VmPolicy.Builder vmPolicyBuilder =
                    new StrictMode.VmPolicy.Builder()
                            .detectAll()
                            .penaltyLog();

            if (PlatformUtils.hasHoneycomb()) {
                threadPolicyBuilder.penaltyFlashScreen();
                vmPolicyBuilder
                        .setClassInstanceLimit(CoffeeShopListActivity.class, 1)
                        .setClassInstanceLimit(CoffeeShopDetailActivity.class, 1);
            }
            StrictMode.setThreadPolicy(threadPolicyBuilder.build());
            StrictMode.setVmPolicy(vmPolicyBuilder.build());
        }
    }



    /**
     Version History by API Level

     2.8 Android 2.2-2.2.3 Froyo (API level 8)
     2.9 Android 2.3-2.3.2 Gingerbread (API level 9)
     2.10 Android 2.3.3-2.3.7 Gingerbread (API level 10)
     2.11 Android 3.0 Honeycomb (API level 11)
     2.12 Android 3.1 Honeycomb (API level 12)
     2.13 Android 3.2-3.2.6 Honeycomb (API level 13)
     2.14 Android 4.0-4.0.2 Ice Cream Sandwich (API level 14)
     2.15 Android 4.0.3-4.0.4 Ice Cream Sandwich (API level 15)
     2.16 Android 4.1 Jelly Bean (API level 16)
     2.17 Android 4.2 Jelly Bean (API level 17)
     2.18 Android 4.3 Jelly Bean (API level 18)
     2.19 Android 4.4 KitKat (API level 19)
     2.20 Android 4.4 KitKat with wearable extensions (API level 20)
     2.21 Android 5.0 Lollipop (API level 21)
     2.22 Android 5.1-5.1.1 Lollipop (API level 22)
     2.23 Android 6.0-6.0.1 Marshmallow (API level 23)

     **/

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

}
