package com.applop.demo.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.applop.demo.R;
import com.applop.demo.fragments.ApplopPagerFragment;
import com.applop.demo.gcm.RegistrationIntentService;
import com.applop.demo.helperClasses.NetworkHelper.MyRequestQueue;
import com.applop.demo.helperClasses.NetworkHelper.VolleyData;
import com.applop.demo.model.AppConfiguration;
import com.applop.demo.model.NameConstant;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import java.io.IOException;

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
        registerInBackground();
    }

    private void registerInBackground() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        startService(intent);
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {
                String msg = "";
                try {


                    String regid = GoogleCloudMessaging.getInstance(context).register(getResources().getString(R.string.gcm_default_sender_id));
                    msg = "Device registered, registration ID=" + regid;
                    Intent intent = new Intent(context, RegistrationIntentService.class);
                    startService(intent);
                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.


                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    //storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }


        }.execute(null, null, null);

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
                mHandler.postDelayed(new LoadActivitiesThread(), 5000);

        }
    }

    public class LoadActivitiesThread implements Runnable {
        public void run()
        {
            if (getPackageName().equalsIgnoreCase("com.applop")){
                Intent intent = new Intent(context,ApplopPagerActivity.class);
                startActivity(intent);
            }else {
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
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

                        //initializeParse();
                    if (getPackageName().equalsIgnoreCase("com.applop"))
                        mHandler.postDelayed(new LoadActivitiesThread(), 5000);
                    else
                        mHandler.postDelayed(new LoadActivitiesThread(), 200);

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
