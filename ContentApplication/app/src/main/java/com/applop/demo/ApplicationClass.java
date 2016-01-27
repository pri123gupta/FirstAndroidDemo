package com.applop.demo;

import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.applop.demo.activities.SplashActivity;
import com.applop.demo.model.AppConfiguration;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class ApplicationClass extends MultiDexApplication {
public static ApplicationClass ourApplication;
    public static ApplicationClass instance(){
    return ourApplication;
    }
    public ApplicationClass(){
        ourApplication=this;
    }
    @Override
    public void onCreate() {
      //  super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_my_first_running);
        super.onCreate();
        Parse.initialize(this, AppConfiguration.getInstance(getApplicationContext()).parseClientID, AppConfiguration.getInstance(getApplicationContext()).parseApplicationKey);
        PushService.setDefaultPushCallback(this,SplashActivity.class);
        try{
            ParseInstallation.getCurrentInstallation().saveInBackground();
       //     Toast.makeText(getApplicationContext(), "worked", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"no"+e,Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}