package com.applop.demo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.applop.demo.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
       Thread timerThread=new Thread(){
           public void run(){
               try{
                   sleep(3000);
               }catch (Exception e){

               }finally {
                   Intent i=new Intent(SplashActivity.this,DrawerActivity.class);
                   startActivity(i);
               }
           }
       };
        timerThread.start();
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
