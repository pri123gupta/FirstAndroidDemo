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
    public String appDescription="";
    public String bgcolor="";
    public String websiteKey="";
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
                email = data.getString("email");
            }
            catch (Exception ex)
            {
                email = "";
            }
            try{
                appDescription = data.getString("AppDescription");
            }
            catch (Exception ex)
            {
                appDescription = "";
            }

            try{
                bgcolor = data.getString("toolbarColor");
            }
            catch (Exception ex)
            {
                bgcolor = "";
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