package com.applop.demo.model;

import android.content.Context;
import android.net.Uri;

import com.applop.demo.helperClasses.DatabaseHelper;

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
        DatabaseHelper feedWLDBHelper = new DatabaseHelper(context);

        me = feedWLDBHelper.getUser();
        return me;
    }

    public static void setUser(Context context,String email,String name,String loginType,String imageUrl){
        DatabaseHelper feedWLDBHelper = new DatabaseHelper(context);
        feedWLDBHelper.insertUser(loginType,name,email,imageUrl);
    }

}