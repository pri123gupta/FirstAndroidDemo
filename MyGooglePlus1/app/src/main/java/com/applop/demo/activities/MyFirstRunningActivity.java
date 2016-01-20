package com.applop.demo.activities;

import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.applop.demo.R;
import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.PushService;

public class MyFirstRunningActivity extends MultiDexApplication {
public static MyFirstRunningActivity ourApplication;
    String  parseApplicationId="40BFoVj07WFZM213nSJqVkIHI9dErmiEY9gezCbn";

    String parseKey="tuXwLzADUhwg7LBqd7zgosHmqQY8BDTCyXyZ7yHh";
    public static MyFirstRunningActivity  instance(){
    return ourApplication;
    }
    public MyFirstRunningActivity(){
        ourApplication=this;
    }
    @Override
    public void onCreate() {
      //  super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_my_first_running);
        super.onCreate();

        Parse.initialize(this, parseApplicationId, parseKey);
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