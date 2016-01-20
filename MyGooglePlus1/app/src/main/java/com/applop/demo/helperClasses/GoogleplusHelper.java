package com.applop.demo.helperClasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;

/**
 * Created by intel on 2/4/2016.
 */
public  class GoogleplusHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "ApplopGplusDb.db";
    String query;
    SQLiteDatabase database;
    public GoogleplusHelper(Context context) {

        super(context, DATABASE_NAME, null, 2);

    }
    @Override
    public void onCreate(SQLiteDatabase database) {
        query = "CREATE TABLE IF NOT EXISTS user(loginType TEXT,name TEXT,email TEXT,imageUrl TEXT)";
        database.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        onCreate(database);
    }

    public void insertUser(String loginType,String name,String email,String imageUrl){
         database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("loginType",loginType);
        values.put("name",name);
        values.put("email",email);
     //   values.put("bitmap",getBytes(bitmap));
        values.put("imageUrl",imageUrl);
        database.insert("user", null, values);
        database.close();
    }
    public User getUser() {
        try {
            String selectQuery = "SELECT * FROM user";
            SQLiteDatabase database = this.getWritableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            User user = new User();
            cursor.moveToNext();
            user.loginType = cursor.getString(0);
            user.name = cursor.getString(1);
            user.email = cursor.getString(2);
            user.imageUrl = cursor.getString(3);
            return user;
        }catch (Exception ex){
            return null;
        }
    }
    public static byte[] getBytes(Uri uri) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
    public void DropTable(String table_Name)
    {
        String query = "DROP TABLE IF EXISTS Bookmarked";
         database = this.getWritableDatabase();

        database.execSQL(query);
    }
    public  void removeUser() {
        database = this.getWritableDatabase();
        String deleteQuery = "DELETE FROM  user";
        database.execSQL(deleteQuery);

    }

}