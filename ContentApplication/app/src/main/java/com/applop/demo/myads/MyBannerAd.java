package com.applop.demo.myads;
/**
 *
 *@file MyBannerAd.java
Custom View for Banner Add
 *@author Readwhere
 *
 */
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.applop.demo.model.AppConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 Class that handles custom view for banner ad
 *@author Readwhere
 *
 *
 */
public class MyBannerAd {

	private Activity _activity;
	private AdClass _Adclass;
	private AdView adView;/**< ADView */
	private LinearLayout layout;/**< ad type */
	private static final String TAG = "MyBannerAd"; /**< Log Tag */
	private AdRequest adRequest;

	/**
	 Default Constructor, initialize add and display it
	 *@author Readwhere
	 * @param mlayout
	 *@param context, parent context
	 *@param attrs , attribute set from views
	 */
	public MyBannerAd(Activity _mactivity, AdClass _instance, LinearLayout mlayout) {
		_activity = _mactivity;
		_Adclass = _instance;
		layout = mlayout;
		layout.setVisibility(View.GONE);
	}
	/**
	 Generates Banner Ad
	 *@author Readwhere
	 *@param context , parent context
	 *@return void
	 */
	public void getBannerAd(Context context){


		if (!AppConfiguration.getInstance(context).isAdEnable){
			return;
		}
		loadAdMobAds();

		//}
	}

	private void loadFlurryAds(){

	}

	private void loadAdMobAds(){
		adView = new AdView(_activity);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId("ca-app-pub-2649338194133294/5664154967");


		adView.setAdListener(new AdListener() {

			@Override
			public void onAdLoaded() {
				layout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {

				String message = "Google onFailedToReceiveAd (" + errorCode + ")";
				try {
					String category="Ads-Google";
					String action="failed";
					String label=_activity.getLocalClassName();
				} catch (Exception e) {
					e.printStackTrace();
				}
				_Adclass.onAdFailedGoogle();
			}

			@Override
			public void onAdOpened() {
				// Save app state before going to the ad overlay.
			}

			@Override
			public void onAdClosed() {
				// Save app state before going to the ad overlay.
			}

			@Override
			public void onAdLeftApplication() {
				// Save app state before going to the ad overlay.
			}
		});

            /*adRequest = new AdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    .addTestDevice(Secure.getString(_activity.getContentResolver(), Secure.ANDROID_ID))
                    .build();*/

		adRequest = new AdRequest.Builder().build();

		adView.loadAd(adRequest);
		layout.addView(adView);
	}
}
