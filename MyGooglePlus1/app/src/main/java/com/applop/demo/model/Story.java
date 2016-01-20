package com.applop.demo.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
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
    String BASE_URL="http://healthxp.net/expert-tutor/";
    public String title;
    public String body;
    public String fullImage;
    final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
    final Charset UTF_8 = Charset.forName("UTF-8");
    //public String shareUrl;
    public String dateString;
    public ArrayList<JsonCategory> categories = new ArrayList<JsonCategory>();
    public ArrayList<Tag> tags = new ArrayList<Tag>();
    public String excerpt;
    public String timeAgo;
    public String categoryNameAndTime;
    public String storyJSONString;
    public ArrayList<Story> relatedStories = new ArrayList<Story>();
    public Story(){
        this.title = "";
        this.body = "";
        this.fullImage = "";
        this.dateString = "";
        this.postId = "";
        this.excerpt = "";
        categoryNameAndTime = "";
        storyJSONString = "";
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
            this.excerpt = "";
            categoryNameAndTime = "";
            timeAgo = "";
            try {
                this.postId = storyObj.getString("id");
            }catch (Exception ex){
                this.postId = "";
            }
            try {
                this.title = storyObj.getString("title");

                byte ptext[] = title.getBytes(ISO_8859_1);
                title = new String(ptext, UTF_8);
            }catch (Exception ex){
                this.title = "";
            }
            try {
                this.body = storyObj.getString("body");
                byte ptext[] = body.getBytes(ISO_8859_1);
                body = new String(ptext, UTF_8);
            }catch (Exception ex){
                this.body = "";
            }
            try {
                this.dateString = storyObj.getString("timestamp");
            }catch (Exception ex){
                this.dateString = "";
            }
            String time = this.dateString;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
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
                System.out.println("Exception "+ex);
            }
            String categoryTime ="";

            try {
                JSONArray JSONcategories = storyObj.getJSONArray("categories");
                for (int i=0;i<JSONcategories.length();i++){
                    this.categories.add(new JsonCategory(JSONcategories.getJSONObject(i)));
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }
            try {
                JSONArray JSONtags = storyObj.getJSONArray("tags");
                for (int i=0;i<JSONtags.length();i++){
                    this.tags.add(new Tag(JSONtags.getJSONObject(i)));
                }
            }catch (Exception ex){
                ex.printStackTrace();
            }

            if(this.categories.size()==0){
                categoryTime ="<font color='#cccccc'>" + time + "</font>";
            }
            else{
                categoryTime += "<font color='#FFA500'>" + categories.get(0).name + "</font>";
                for (int i=1;i<categories.size();i++){
                    categoryTime += "<font color='#FFA500'>" +"," + categories.get(i).name + "</font>";
                }
                categoryTime += "<font color='#cccccc'>" +" | " + time + "</font>";
            }
            this.categoryNameAndTime = categoryTime;
            try {
                this.excerpt = storyObj.getString("description");
                byte ptext[] = excerpt.getBytes(ISO_8859_1);
                excerpt = new String(ptext, UTF_8);
            }catch (Exception ex){
                this.excerpt = "";
            }
            try{
                String postImage = storyObj.getString("imagePath");
                if(postImage!=null&& !postImage.equals(""))
                {
                    this.fullImage = BASE_URL + postImage;
                }else {
                    this.fullImage = "";
                }
            }
            catch (Exception thumbEx)
            {
                this.fullImage = "";
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
        //dest.writeString(shareUrl);
        dest.writeString(dateString);
        dest.writeString(postId);
        dest.writeString(excerpt);
        dest.writeList(categories);
        dest.writeString(categoryNameAndTime);
        dest.writeList(relatedStories);
        dest.writeString(timeAgo);
        dest.writeList(tags);
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
        categories= in.readArrayList(JsonCategory.class.getClassLoader());
        categoryNameAndTime = in.readString();
        relatedStories= in.readArrayList(Story.class.getClassLoader());
        timeAgo = in.readString();
        tags= in.readArrayList(Tag.class.getClassLoader());
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



