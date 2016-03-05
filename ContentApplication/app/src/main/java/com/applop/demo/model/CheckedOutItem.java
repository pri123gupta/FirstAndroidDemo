package com.applop.demo.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckedOutItem implements Parcelable {
    public String orderId;
    public int quantity;
    public String totalPrice;
    public String phoneNumber;
    public String address;
    public String timeAgo;
    public String status;
    public ArrayList<Story> items = new ArrayList<>();

    public CheckedOutItem(Context context, JSONObject storyObj){
        try {
            try {
                orderId = storyObj.getString("orderId");
            }catch (Exception ex){
                orderId = "";
            }
            try {
                status = storyObj.getString("status");
            }catch (Exception ex){
                status = "0";
            }
            try {
                totalPrice = storyObj.getString("TotalPrize");
            }catch (Exception ex){
                totalPrice = "0";
            }
            try {
                phoneNumber = storyObj.getString("phone");
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

            try {
                JSONArray allItems = storyObj.getJSONArray("AllItems");
                for (int i=0;i<allItems.length();i++){
                    Story story = new Story(context,allItems.getJSONObject(i));
                    items.add(story);
                }
                quantity = items.size();
            }catch (Exception ex){

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
        dest.writeInt(quantity);
        dest.writeString(totalPrice);
        dest.writeString(phoneNumber);
        dest.writeString(timeAgo);
    }
    public CheckedOutItem(Parcel in) {
        orderId = in.readString();
        quantity = in.readInt();
        totalPrice = in.readString();
        //shareUrl = in.readString();
        phoneNumber = in.readString();
        timeAgo = in.readString();
    }
    public static final Creator<CheckedOutItem> CREATOR = new Creator<CheckedOutItem>() {
        public CheckedOutItem createFromParcel(Parcel in) {
            return new CheckedOutItem(in);
        }

        public CheckedOutItem[] newArray(int size) {
            return new CheckedOutItem[size];
        }
    };

}



