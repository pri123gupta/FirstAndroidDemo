package com.applop.demo.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by intel on 2/7/2016.
 */
public class Category implements Parcelable{
    public	String categoryId;
    public String name;
    public String type;
    public String link;
    public ArrayList<Category> subCategories = new ArrayList<Category>();
    protected Category(Parcel in) {
        categoryId = in.readString();
        name = in.readString();
        type = in.readString();
        subCategories = in.readArrayList(Category.class.getClassLoader());
        String booleanString = in.readString();
    }
    Category(){
        super();
        categoryId="";
        name="";
        type="";
    }
    public Category(String categoryId, String name, String type){
        super();
        this.categoryId=categoryId;
        this.name=name;
        this.type=type;
    }
    public Category(JSONObject jsonObject){
        super();
        try{
            categoryId="";
            name="";
            type="";
            this.categoryId=jsonObject.getString("id");
            this.name=jsonObject.getString("name");
            this.type=jsonObject.getString("type");
            this.link=jsonObject.getString("link");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }
        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(categoryId);
        dest.writeString(name);
        dest.writeString(type);

    }
}
