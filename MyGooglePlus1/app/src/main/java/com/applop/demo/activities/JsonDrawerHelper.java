package com.applop.demo.activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.applop.demo.R;

/**
 * Created by intel on 2/9/2016.
 */
public class JsonDrawerHelper {

    public static void setDetailsInDrawerlayout(final RelativeLayout drawerlayout, final Context context){

       // ((TextView) drawerlayout.findViewById(R.id.userName)).setText("My Name");
      //  ((TextView)drawerlayout.findViewById(R.id.email)).setText("My Email");
    }


    public static boolean isNetworkAvailable(Context mcontext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mcontext.getSystemService(Context.CONNECTIVITY_SERVICE);
              NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                 return activeNetworkInfo != null;
    }
}
