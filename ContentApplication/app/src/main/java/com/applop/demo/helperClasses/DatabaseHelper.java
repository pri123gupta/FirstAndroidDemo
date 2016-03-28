package com.applop.demo.helperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import com.applop.demo.model.Story;
import com.applop.demo.model.User;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by intel on 2/4/2016.
 */
public  class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ApplopGplusDb.db";
    String query;
    SQLiteDatabase database;
    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, 4);

    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        query = "CREATE TABLE IF NOT EXISTS Bookmarked(postId TEXT,storyJSONString TEXT,date TEXT)";
        database.execSQL(query);
        query = "CREATE TABLE IF NOT EXISTS user(loginType TEXT,name TEXT,email TEXT,bitmap BLOB,imageUrl TEXT,address TEXT,phoneNo TEXT,city TEXT,country TEXT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        query = "ALTER TABLE user ADD city TEXT";
        database.execSQL(query);
        query = "ALTER TABLE user ADD country TEXT";
        database.execSQL(query);
        onCreate(database);
    }public void removeUser() {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM  user";
        database.execSQL(deleteQuery);
    }

    public void insertUser(String loginType,String name,String email,Bitmap bitmap,String imageUrl,String address, String phoneNo,String city,String country){
        removeUser();
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("loginType",loginType);
        values.put("name",name);
        values.put("email",email);
        values.put("bitmap",getBytes(bitmap));
        values.put("imageUrl",imageUrl);
        values.put("address",address);
        values.put("phoneNo", phoneNo);
        values.put("city", city);
        values.put("country", country);
        database.insert("user", null, values);
        database.close();
    }

    public User getUser(){
        try {
            String selectQuery = "SELECT * FROM user";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            User user = new User();
            cursor.moveToNext();
            user.loginType = cursor.getString(0);
            user.name = cursor.getString(1);
            user.email = cursor.getString(2);
            byte[] image = cursor.getBlob(3);
            user.bitmap = getImage(image);
            user.imageUrl = cursor.getString(4);
            user.address = cursor.getString(5);
            user.phoneNumber = cursor.getString(6);
            user.city = cursor.getString(7);
            user.country = cursor.getString(8);
            return user;
        }catch (Exception ex){
            return new User();
        }
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        if (bitmap==null){
            return new byte[0];
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void DropTable(String table_Name)
    {
        String query = "DROP TABLE IF EXISTS Bookmarked";
        SQLiteDatabase database = this.getWritableDatabase();

        database.execSQL(query);
    }
    public ArrayList<Story> getAllPostsBookmarked(Context context) {
        ArrayList<Story> bookmarkedStories = new ArrayList<Story>();
        String selectQuery = "SELECT  * FROM Bookmarked ORDER BY date DESC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String storyJSONString = cursor.getString(1);
                Story story=null;
                try {
                    story = new Story(context,new JSONObject(storyJSONString));
                }catch (Exception ex){
                    ex.printStackTrace();
                }
                if(story!=null)
                    bookmarkedStories.add(story);
            }while (cursor.moveToNext());
        }
        return bookmarkedStories;
    }


    public void removeFromBookmarked(String postId) {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM  Bookmarked where postId='"+ postId +"'";
        database.execSQL(deleteQuery);
    }

    public void removeFromBookmarked() {
        SQLiteDatabase database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM  Bookmarked";
        database.execSQL(deleteQuery);
    }

    public Boolean checkIfBookmarked(String postId) {
        String selectQuery = "SELECT  * FROM Bookmarked where postId='"+ postId +"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                return true;
            } while (cursor.moveToNext());
        }

        // return contact list
        return false;
    }

    public void addToBookmarked(Story story) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("postId",story.postId);
        values.put("date",story.dateString);
        values.put("storyJSONString",story.storyJSONString);
        database.insert("Bookmarked", null, values);
        database.close();
    }

}