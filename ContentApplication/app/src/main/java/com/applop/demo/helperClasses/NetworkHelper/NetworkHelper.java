package com.applop.demo.helperClasses.NetworkHelper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by intel on 2/9/2016.
 */
public class NetworkHelper {


    public static boolean isNetworkAvailable(Context mcontext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
              NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                 return activeNetworkInfo != null;
    }
}
