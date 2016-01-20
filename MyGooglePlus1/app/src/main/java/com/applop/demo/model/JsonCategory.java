package com.applop.demo.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by intel on 2/7/2016.
 */
public class JsonCategory implements Parcelable{
    public	String categoryId;
    public String name;
    public String type;

    public ArrayList<JsonCategory> subCategories = new ArrayList<JsonCategory>();
    protected JsonCategory(Parcel in) {
        categoryId = in.readString();
        name = in.readString();
        type = in.readString();

        subCategories = in.readArrayList(JsonCategory.class.getClassLoader());
        String booleanString = in.readString();

    }

    JsonCategory(){
        super();
        categoryId="";
        name="";
        type="";

    }
    public JsonCategory(String categoryId, String name, String type){
        super();
        this.categoryId=categoryId;
        this.name=name;
        this.type=type;

    }
    public JsonCategory(JSONObject jsonObject){
        super();
        try{
            categoryId="";
            name="";
            type="";
        this.categoryId=jsonObject.getString("id");
            this.name=jsonObject.getString("name");
            this.type="current affair";

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }


    public static final Creator<JsonCategory> CREATOR = new Creator<JsonCategory>() {
        @Override
        public JsonCategory createFromParcel(Parcel in) {
            return new JsonCategory(in);
        }

        @Override
        public JsonCategory[] newArray(int size) {
            return new JsonCategory[size];
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
