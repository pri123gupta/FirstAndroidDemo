package com.applop.demo.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;

import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    Context context;
    AppConfiguration appConfiguration;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_splash);
        startLoadingApplication();
    }

    private void startLoadingApplication(){
        AppConfiguration.setMe(null);
        appConfiguration = AppConfiguration.getInstance(this);

        if(appConfiguration.websiteKey=="")
        {
            loadConfiguration();
        }
        else
        {
           /* if (!getPackageName().equalsIgnoreCase("com.dc.dcapp.view")){
                findViewById(R.id.background).setBackgroundColor(Color.parseColor(AppConfiguration.getInstance(context).bgcolor));
            }*/
                mHandler.postDelayed(new LoadActivitiesThread(), 2000);

        }
    }

    public class LoadActivitiesThread implements Runnable {
        public void run()
        {
            Intent intent = new Intent(context,MainActivity.class);
            startActivity(intent);
        }
    }

    private void loadConfiguration(){

        String url= NameConstant.BASE_API_URL_V1+"getConfigByPackageName.php?packageName="+getPackageName();
        MyRequestQueue.Instance(context).cancelPendingRequests("config");
        new VolleyData(context){
            @Override
            protected void VPreExecute() {

            }

            @Override
            protected void VResponse(JSONObject response, String tag) {
                try {

                    JSONObject json = response;
                        JSONObject data = json.getJSONObject("data");
                        appConfiguration.setAppConfigurationData(data.toString());
                        SharedPreferences.Editor editor = context.getSharedPreferences(NameConstant.APP_CACHE_CONFIG_TABLE_NAME, context.MODE_PRIVATE).edit();
                        editor.putString(NameConstant.APP_CACHE_CONFIG_TABLE_NAME, data.toString());
                        editor.commit();
                        try {
                            findViewById(R.id.background).setBackgroundColor(Color.parseColor(AppConfiguration.getInstance(context).bgcolor));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        //initializeParse();
                        mHandler.postDelayed(new LoadActivitiesThread(), 2000);

                } catch (Exception e) {
                    //Toast.makeText(AppConfigSplashScreen.this,"Problem occurred in connecting to server.",Toast.LENGTH_SHORT).show();
                    showRetryDialog();
                }
            }

            @Override
            protected void VError(VolleyError error, String tag) {
                showRetryDialog();
            }
        }.getJsonObject(url, true, "config",this);

    }

    private void showRetryDialog(){
        AlertDialog alertDialog = new AlertDialog.Builder(context).setTitle("Network Error.")
                .setMessage("Check your internet connection and try again.")
                .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadConfiguration();
                    }
                })
                .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .create();
        alertDialog.show();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        finish();
    }

}
