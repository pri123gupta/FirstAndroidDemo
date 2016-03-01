package com.applop.demo.model;

import android.content.Context;
import android.content.SharedPreferences;
import com.android.volley.VolleyError;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;

import org.json.JSONObject;


/**
 * Created by narayan on 1/8/15.
 */
public class AppConfiguration {
    public String appName="";
    public String email="";
    public String currencyName="";
    public String currencySymbol="";
    public String appDescription="";
    public String appContactLine1="";
    public String appContactLine2="";
    public String appContactLine3="";
    public boolean isShareEnable=true;
    public boolean isEnquryEnable=true;
    public boolean isBookingEnable=false;
    public boolean isCartEnable=false;
    public String appContactLine4="";
    public String bgcolor="#000000";
    public String websiteKey="";
    public String iconTheme="";
    public String parseApplicationKey="";
    public String parseClientID="";
    public boolean notificationsEnabled;
    public static String CACHE_DIR_PATH;
    public static  String FB_DIR;

    private Context context;
    private static AppConfiguration me;

    public static final String NOTIFICATION_ENABLE_DATABASE_NAME = "notification_enable_database";

    public static void setMe(AppConfiguration me) {
        AppConfiguration.me = me;
    }

    protected AppConfiguration(){
        me = this;
    }
    public static AppConfiguration getInstance(){

        if (me!=null)
            return me;
        else
        {
            return new AppConfiguration();
        }
    }

    public static AppConfiguration getInstance(Context mContext){


        if (me!=null)
            return me;
        else{
            AppConfiguration appConfig = new AppConfiguration();
            appConfig.context = mContext;
           SharedPreferences prefs = mContext.getSharedPreferences(NameConstant.APP_CACHE_CONFIG_TABLE_NAME, mContext.MODE_PRIVATE);
            String configData = prefs.getString(NameConstant.APP_CACHE_CONFIG_TABLE_NAME,null);
            if(configData!=null)
            {
                appConfig.setAppConfigurationData(configData);
                appConfig.refreshConfig();
            }
            else
            {

            }
            return appConfig;
        }
    }

    public Boolean isPushNotificationEnabled(){
        SharedPreferences sharedPreferences = me.context.getSharedPreferences(NOTIFICATION_ENABLE_DATABASE_NAME,me.context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(NameConstant.IS_PUSH_NOTIFICATION_ENABLED_KEY,true);
    }

    private void refreshConfig(){
        String url= NameConstant.BASE_API_URL_V1+"getConfigByPackageName.php?packageName="+context.getPackageName();
        System.out.println(url);
        //MyRequestQueue.Instance(context).cancelPendingRequests("appconfig");
        MyRequestQueue.Instance(context).cancelPendingRequests("config");
        new VolleyData(context){
            @Override
            protected void VPreExecute() {

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                try {
                    JSONObject json = response;
                    Boolean statusValue = json.getBoolean("status");
                    if (statusValue) {
                        JSONObject data = json.getJSONObject("data");
                        //Toast.makeText(context, "app Config Updated", Toast.LENGTH_LONG).show();
                        setAppConfigurationData(data.toString());
                        SharedPreferences.Editor editor = context.getSharedPreferences(NameConstant.APP_CACHE_CONFIG_TABLE_NAME,context.MODE_PRIVATE).edit();
                        editor.putString(NameConstant.APP_CACHE_CONFIG_TABLE_NAME, data.toString());
                        editor.commit();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            protected void VError(VolleyError error, String tag) {

            }
        }.getJsonObject(url, true, "config",context);
    }



    public void setAppConfigurationData(String configStr)
    {
        try {
            JSONObject data = new JSONObject(configStr);
            try{
                appName = data.getString("appName");
            }
            catch (Exception ex)
            {
                appName = "";
            }
            try{
                isShareEnable = data.getBoolean("isShareEnable");
            }
            catch (Exception ex)
            {
                isShareEnable = true;
            }
            try{
                isBookingEnable = data.getBoolean("isBookingEnable");
            }
            catch (Exception ex)
            {
                isBookingEnable = false;
            }
            try{
                isCartEnable = data.getBoolean("isCartEnable");
            }
            catch (Exception ex)
            {
                isCartEnable = false;
            }
            try{
                isEnquryEnable = data.getBoolean("isEnquiryEnable");
            }
            catch (Exception ex)
            {
                isEnquryEnable = false;
            }
            try{
                iconTheme = data.getString("iconTheme");
            }
            catch (Exception ex)
            {
                iconTheme = "light";
            }
            try{
                currencyName = data.getString("currencyName");
            }
            catch (Exception ex)
            {
                currencyName = "Indian";
            }
            try{
                currencySymbol = data.getString("currencySymbols");
            }
            catch (Exception ex)
            {
                currencySymbol = "INR";
            }
            try{
                email = data.getString("email");
            }
            catch (Exception ex)
            {
                email = "";
            }
            try{
                appDescription = data.getString("appDescription");
            }
            catch (Exception ex)
            {
                appDescription = "Make application without coding : Applop helps in expanding and managing your business, a single tool in form of mobile app that serves as an integrated solution to retain old customers and attract new. With click of a button\n" +
                        "    you updates will be notified through push notification to all your users.";
            }
            try{
                appContactLine1 = data.getString("contactLine1");
            }
            catch (Exception ex)
            {
                appContactLine1 = "Applop Mobile Solutions Pvt. Ltd.";
            }
            try{
                appContactLine2 = data.getString("contactLine2");
            }
            catch (Exception ex)
            {
                appContactLine2 = "737B, JMD Megapolis, Setor 48";
            }
            try{
                appContactLine3 = data.getString("contactLine3");
            }
            catch (Exception ex)
            {
                appContactLine3 = "Sohna Road, Gurgaon - 122001";
            }
            try{
                appContactLine4 = data.getString("contactLine4");
            }
            catch (Exception ex)
            {
                appContactLine4 = "Phone number : 01244939799";
            }


            try{
                bgcolor = data.getString("toolbarColor");
            }
            catch (Exception ex)
            {
                bgcolor = "#000000";
            }
            try{
                websiteKey = data.getString("APIKey");
            }
            catch (Exception ex)
            {
                websiteKey = "";
            }
            //feedUrl = data.getString("feed_url");

                try {
                    parseApplicationKey = data.getString("parseAPIKey");
                } catch (Exception ex) {
                    parseApplicationKey = "";
                }
                try {
                    parseClientID = data.getString("parseClientKey");
                } catch (Exception ex) {
                    parseClientID = "";
                }

            try {
                FB_DIR = context.getPackageName().substring(context.getPackageName().lastIndexOf(".") + 1, context.getPackageName().length() - 1);
            }catch (Exception ex){
                FB_DIR = "";
            }
            try {
                CACHE_DIR_PATH = "/" + FB_DIR+"/";
            }catch (Exception ex){
                CACHE_DIR_PATH = "";
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
