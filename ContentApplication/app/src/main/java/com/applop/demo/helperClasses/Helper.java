package com.applop.demo.helperClasses;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Admin on 25-01-2016.
 */
public class Helper {
    public static void setToolbarColor(Context context){
        try {
            Drawable colorDrawable = new ColorDrawable(Color.parseColor(AppConfiguration.getInstance(context).bgcolor));
            Drawable bottomDrawable = new ColorDrawable(context.getResources().getColor(android.R.color.transparent));
            LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
            ((AppCompatActivity) context).getSupportActionBar().setBackgroundDrawable(ld);
        }catch (Exception ex){

        }
    }

    public static String getCachedDataForUrl(String url,Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(NameConstant.HTTP_CLIENT_CACHE_DATABASE, context.MODE_PRIVATE);
        String data = sharedPreferences.getString(String.valueOf(url.hashCode()), "");
        return data;
    }

    public static void saveCacheDataForUrl(String url,String string,Context context){
        SharedPreferences.Editor sharedPrefencesEditor = context.getSharedPreferences(NameConstant.HTTP_CLIENT_CACHE_DATABASE,context.MODE_PRIVATE).edit();
        sharedPrefencesEditor.putString(String.valueOf(url.hashCode()), string);
        sharedPrefencesEditor.apply();
    }


    public static class MobileInfo
    {
        private static final String TAG = "com.readwhere.app.v3.utility.MobileInfo";
        private Context context;
        private String mobileName;
        private String mobileResolution;
        private int width;
        private int hieght;
        private String board;
        private String manufacturer;
        private String device;
        private String buildVersionRelease;
        private int buildVersionSDK;
        private String appversionname;
        private String apppackagename;


        private String imei;
        private String secureid;
        private String display;

        public MobileInfo(Context mContext)
        {
            context=mContext;
            setMobileResolution();
            setMobileName();
            setMobileResolution();
            setManufacturer();
            setBoard();
            setDevice();
            setAppVersionName();
            setApppackagename();
            setSDK();
            setMobileOs();
            setIMEI(mContext);
            setDisplay();
            setSecureID(mContext);


        }

        public int getSDK()
        {
            return buildVersionSDK;
        }

        public String getMobileName() {
            return mobileName;
        }

        public String getMobileResolution() {
            return mobileResolution;
        }

        public String getManufacturer()
        {
            return manufacturer;
        }

        public String getBoard()
        {
            return board;
        }

        public String getDevice()
        {
            return device;
        }

        public String getMobileOs()
        {
            return buildVersionRelease;
        }

        public String getappVersionName()
        {
            return appversionname;
        }
        public String getApppackagename() {
            return apppackagename;
        }

        public String getIMEI()
        {
            return imei;
        }

        public String getSecureID()
        {
            return secureid;
        }

        public String getDisplay()
        {
            return display;
        }

        public int getScreenWidth(){
            return width;
        }

        public void setMobileName() {
            this.mobileName = android.os.Build.MODEL;
        }

        public void setMobileResolution() {
            setScreenDimensions();
            this.mobileResolution = width+"dp X "+hieght+"dp";
        }

        public void setManufacturer()
        {
            this.manufacturer=android.os.Build.MANUFACTURER;
        }

        public void setBoard()
        {
            this.board=android.os.Build.BOARD;
        }

        public void setDevice()
        {
            this.device=android.os.Build.DEVICE;
        }

        private void setMobileOs()
        {
            this.buildVersionRelease=android.os.Build.VERSION.RELEASE;
        }

        private void setSDK()
        {
            this.buildVersionSDK=android.os.Build.VERSION.SDK_INT;
        }

        public void setScreenDimensions()
        {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            hieght = (int)(metrics.heightPixels / (metrics.densityDpi / 160f));
            width = (int)(metrics.widthPixels / (metrics.densityDpi / 160f));
        }

        public void setAppVersionName()
        {
            PackageInfo pInfo;
            try {
                pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
                appversionname = pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                //Log.e(TAG, "setAppVersionName exception " + e.getMessage());
            }

        }

        public void setApppackagename() {
            apppackagename = context.getPackageName();
        }

        public void setDisplay(){
            display=android.os.Build.DISPLAY;
        }

        public void setIMEI(Context Context){
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        }

        public void setSecureID(Context context){
            secureid= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        }


        @Override
        public String toString() {
            return super.toString();
        }

        public String writeJSON() {
            JSONObject object = new JSONObject();
            try {
                object.put("phone_name", getMobileName());
                object.put("phone_resolution",getMobileResolution() );
                object.put("manufacturer", getManufacturer());
                object.put("board",getBoard());
                object.put("device", getDevice());
                object.put("build_version", getMobileOs());
                object.put("sdk_version", getSDK());
                object.put("app vesion", getappVersionName());
                return object.toString();
            } catch (JSONException e) {
                //Log.e(TAG, "writeJSON exception " + e.getMessage());
            }
            return null;
        }

        public String getDeviceInfoJSON() {
            JSONObject object = new JSONObject();
            try {
                object.put("device_id", getSecureID());
                object.put("imei",getIMEI() );
                object.put("model", getMobileName());
                object.put("manufacturer",getManufacturer());
                object.put("firmware", getMobileOs());
                object.put("display", getMobileResolution());
                object.put("app_version", getappVersionName());
                object.put("app_package", getApppackagename());
                return object.toString();
            } catch (JSONException e) {
                //Log.e(TAG, "getDeviceInfoJSON exception " + e.getMessage());
            }
            return null;
        }

        public int getWidthPixels(){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            return metrics.widthPixels;
        }
    }
}
