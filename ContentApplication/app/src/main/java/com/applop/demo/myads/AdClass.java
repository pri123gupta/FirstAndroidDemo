package com.applop.demo.myads;

import android.app.Activity;
import android.widget.LinearLayout;

public class AdClass {
	
	private Activity _activity;
	private AdClass _instance;
	private LinearLayout layout;
	private static final String TAG = "AdClass";
	
	/*public AdClass(Context context,AttributeSet attrs) {
		super(context,attrs);
		_activity = (Activity)context;
		_instance = this;
		getAd();
	}*/
	
	public AdClass(LinearLayout mlayout, Activity context) {
		_activity = (Activity)context;
		_instance = this;
		layout = mlayout;
		getAd();
	}

	public void getAd() {
		//loadAmazon();
		loadGoogle();
	}

	private void loadAmazon() {
		//AmazonAds ads = new AmazonAds(_activity, _instance,layout);
		//ads.getBannerAd();
	}
	
	public void onAdFailedAmazon(Boolean isEpaperAd) {
		loadGoogle();
	}
	
	private void loadGoogle() {
        /*if (AppConfiguration.getInstance(_activity).adsEnabled) {
            if (AppConfiguration.getInstance(_activity).adunitAndroid != "") {
        */
		MyBannerAd ads = new MyBannerAd(_activity, _instance, layout);
                ads.getBannerAd(_activity);
        /*    }
        }*/
	}

	public void onAdFailedGoogle() {
		//TODO 
	}

	
}