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

public class Story implements Parcelable {
    public String postId;
    public String cartId;
    public int quantity;
    String BASE_URL="http://healthxp.net/expert-tutor/";
    public String title;
    public String body;
    public String fullImage;
    public String totalPrice;
    final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    final Charset UTF_8 = Charset.forName("UTF-8");
    //public String shareUrl;
    public String dateString;
    public String categoryId;
    public String youtubeURL;
    public String excerpt;
    public String price;
    public String timeAgo;
    public String categoryNameAndTime;
    public String storyJSONString;
    public Story(){
        this.title = "";
        this.body = "";
        this.fullImage = "";
        this.dateString = "";
        this.postId = "";
        this.excerpt = "";
        categoryNameAndTime = "";
        storyJSONString = "";
        price = "";
        timeAgo = "";
    }
    public Story(Context context, JSONObject storyObj){
        try {
            storyJSONString = storyObj.toString();
            this.title = "";
            this.body = "";
            this.fullImage = "";
            //this.shareUrl = "";
            this.dateString = "";
            this.postId = "";
            this.youtubeURL = "";
            this.excerpt = "";
            price = "";
            categoryNameAndTime = "";
            timeAgo = "";
            try {
                this.postId = storyObj.getString("id");
            }catch (Exception ex){
                try {
                    this.postId = storyObj.getString("storyId");
                }catch (Exception e){
                    this.postId = "";
                }
            }
            try {
                this.price = storyObj.getString("price");
            }catch (Exception ex){
                this.price = "";
            }
            try {
                this.totalPrice = storyObj.getString("totalRs");
            }catch (Exception ex){
                this.totalPrice = "0";
            }
            try {
                this.cartId = storyObj.getString("cartId");
            }catch (Exception ex){
                this.cartId = "";
            }
            try {
                this.cartId = storyObj.getString("cartId");
            }catch (Exception ex){
                this.cartId = "";
            }
            try {
                this.quantity = Integer.parseInt(storyObj.getString("quantity"));
            }catch (Exception ex){
                this.quantity = 0;
            }
            try {
                this.title = Html.fromHtml(storyObj.getString("title")).toString();
            }catch (Exception ex){
                this.title = "";
            }
            try {
                this.body = storyObj.getString("body");
            }catch (Exception ex){
                this.body = "";
            }
            try {
                this.dateString = storyObj.getString("timeStamp");
            }catch (Exception ex){
                this.dateString = "";
            }
            String time = this.dateString;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
            Date date =null;
            try
            {
                date = simpleDateFormat.parse(time);
                long milliseconds = date.getTime();
                time = (String) DateUtils.getRelativeDateTimeString(context, milliseconds, 0, DateUtils.WEEK_IN_MILLIS, 1);
                time = time.split(",")[0];
                timeAgo = time;
            }
            catch (ParseException ex)
            {
                System.out.println("Exception " + ex);
            }


            try {
                categoryId = storyObj.getString("categoryId");

            }catch (Exception ex){
                ex.printStackTrace();
            }

            this.categoryNameAndTime = "";
            try {
                this.excerpt = Html.fromHtml(storyObj.getString("description")).toString();
            }catch (Exception ex){
                this.excerpt = "";
            }
            try{
                String postImage = storyObj.getString("imagePath");
                if(postImage!=null&& !postImage.equals(""))
                {
                    this.fullImage = postImage;
                }else {
                    this.fullImage = "";
                }
            }
            catch (Exception thumbEx)
            {
                this.fullImage = "";
            }
            try {
                this.youtubeURL = Html.fromHtml(storyObj.getString("videoUrl")).toString();
                int startIndex = this.youtubeURL.indexOf("?v=") + 3;
                String videoID = this.youtubeURL.substring(startIndex, startIndex + 11);
                this.fullImage="http://img.youtube.com/vi/"+videoID+"/0.jpg";
            }catch (Exception ex){
                this.youtubeURL = "";
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(body);
        dest.writeString(fullImage);
        dest.writeString(dateString);
        dest.writeString(postId);
        dest.writeString(excerpt);
        dest.writeString(categoryId);
        dest.writeString(categoryNameAndTime);
        dest.writeString(timeAgo);
        dest.writeString(storyJSONString);
    }
    public Story(Parcel in) {
        title = in.readString();
        body = in.readString();
        fullImage = in.readString();
        //shareUrl = in.readString();
        dateString = in.readString();
        postId = in.readString();
        excerpt = in.readString();
        categoryId= in.readString();
        categoryNameAndTime = in.readString();
        timeAgo = in.readString();
        storyJSONString = in.readString();
    }
    public static final Creator<Story> CREATOR = new Creator<Story>() {
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

}



