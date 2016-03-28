package com.applop.demo.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.Toast;

import com.applop.demo.helperClasses.DatabaseHelper;

/**
 * Created by intel on 2/4/2016.
 */
public  class User {
    public String name = "";
    public String email = "";
    public String imageUrl="";
    public String city="";
    public String country="";
    public Bitmap bitmap;
    public String loginType = "";
    public String phoneNumber = "";
    public String address = "";
    public String quantity="";
    static User me;

    public User(){

    }

    public static User getInstance(Context context){
        DatabaseHelper feedWLDBHelper = new DatabaseHelper(context);
        me = feedWLDBHelper.getUser();
        try {
            if (me.phoneNumber.length()>10) {
                me.phoneNumber = me.phoneNumber.substring(3, me.phoneNumber.length());
            }
        }catch (Exception ex){

        }
        return me;
    }

    public static void setUser(Context context,String email,String name,String loginType,Bitmap bitmap,String imageUrl,String address,String phoneNumber,String city,String country){
        DatabaseHelper feedWLDBHelper = new DatabaseHelper(context);

        feedWLDBHelper.insertUser(loginType,name,email,bitmap,imageUrl,address,phoneNumber,city,country);
    }
}
