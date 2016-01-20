package com.applop.demo.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

public class Tag implements Parcelable {
    public	String tagId;
    public String name;
    public Tag()
    {
        super();
        this.tagId = "";
        this.name = "";
    }

    public Tag(String id, String name, String type)
    {
        super();
        this.tagId = id;
        this.name = name;
    }

    public Tag(JSONObject tag)
    {
        super();
        try {
            tagId = "";
            name = "";
            this.tagId = tag.getString("id");
            this.name = tag.getString("name");
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
        // TODO Auto-generated method stub
        dest.writeString(tagId);
        dest.writeString(name);
    }


    public Tag(Parcel in) {
        tagId = in.readString();
        name = in.readString();
    }

    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
}
