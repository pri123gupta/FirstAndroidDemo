package com.applop.demo.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookedItem implements Parcelable {
    public String orderId;
    public String totalPrice;
    public String phoneNumber;
    public String address;
    public String timeAgo;
    public String productTitle;
    public String status;
    public ArrayList<Story> items = new ArrayList<>();

    public BookedItem(Context context, JSONObject storyObj){
        try {
            try {
                orderId = storyObj.getString("id");
            }catch (Exception ex){
                orderId = "";
            }
            try {
                productTitle = storyObj.getString("product");
            }catch (Exception ex){
                productTitle = "";
            }
            try {
                status = storyObj.getString("status");
            }catch (Exception ex){
                status = "0";
            }
            try {
                totalPrice = storyObj.getString("prize");
            }catch (Exception ex){
                totalPrice = "0";
            }
            try {
                phoneNumber = storyObj.getString("phoneNumber");
            }catch (Exception ex){
                phoneNumber = "";
            }
            try {
                address = storyObj.getString("address");
            }catch (Exception ex){
                address = "";
            }
            try {
                String time = storyObj.getString("time");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
                Date date =null;
                try
                {
                    date = simpleDateFormat.parse(time);
                    long milliseconds = date.getTime();
                    time = (String) DateUtils.getRelativeDateTimeString(context, milliseconds, 0, DateUtils.WEEK_IN_MILLIS, 1);
                    time = time.split(",")[0];
                    timeAgo = time;
                }catch (Exception e){
                    timeAgo = "";
                }
            }catch (Exception ex){
                timeAgo = "";
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(orderId);
        dest.writeString(totalPrice);
        dest.writeString(phoneNumber);
        dest.writeString(timeAgo);
    }
    public BookedItem(Parcel in) {
        orderId = in.readString();
        totalPrice = in.readString();
        //shareUrl = in.readString();
        phoneNumber = in.readString();
        timeAgo = in.readString();
    }
    public static final Creator<BookedItem> CREATOR = new Creator<BookedItem>() {
        public BookedItem createFromParcel(Parcel in) {
            return new BookedItem(in);
        }

        public BookedItem[] newArray(int size) {
            return new BookedItem[size];
        }
    };

}



