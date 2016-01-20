package com.applop.demo.helperClasses;

import android.content.Context;
import android.net.Uri;

/**
 * Created by intel on 2/4/2016.
 */
public  class User {
    public String name = "";
    public String email = "";
    public String imageUrl ="";
    public static Uri uri;
    public String loginType = "";
    static User me;

    public User(){
    }

    public static User getInstance(Context context){
        GoogleplusHelper feedWLDBHelper = new GoogleplusHelper(context);

        me = feedWLDBHelper.getUser();
        return me;
    }

    public static void setUser(Context context,String email,String name,String loginType,String imageUrl){
        GoogleplusHelper feedWLDBHelper = new GoogleplusHelper(context);
        feedWLDBHelper.insertUser(loginType,name,email,imageUrl);
    }

}
