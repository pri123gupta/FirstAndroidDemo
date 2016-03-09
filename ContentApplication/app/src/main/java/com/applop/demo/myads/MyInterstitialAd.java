package com.applop.demo.myads;
/**
 * 
 *@file MyInterstitialAd.java
Handles and generates interstitial ad
 *@author Readwhere
 *
 */
import android.app.Activity;
import android.content.Context;

import com.applop.demo.model.AppConfiguration;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

/**
Handles and generates interstitial ad
 *@author Readwhere
 *
 *
 */
public class MyInterstitialAd {
	private InterstitialAd interstitial;
	private Context mContext;
	private static final String TAG = "MyInterstitialAd";
	private AdRequest adRequest;


	public MyInterstitialAd(Context context,Boolean isEpaperAd) {
		mContext=context;
		getInterstitialAd(context,isEpaperAd);

	}
	/**
	Generates Interstitial Ad
	 *@author Readwhere
	 *@param context , parent context
	 *@return void
	 */
	public void getInterstitialAd(Context context,Boolean isEpaperAd){

		
		interstitial = new InterstitialAd((Activity) context);
		/*	adRequest = new AdRequest.Builder()
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice("EA0E8A3AC5AA4731A8C4E5A4836CFB2D")
					.addTestDevice(Secure.getString(context.getContentResolver(), Secure.ANDROID_ID))
					.build();
		*/
		adRequest = new AdRequest.Builder().build();
		interstitial.setAdUnitId("ca-app-pub-2649338194133294/7140888169");
		if (AppConfiguration.getInstance(context).isAdEnable)
				interstitial.loadAd(adRequest);

		
		interstitial.setAdListener(new AdListener() {
			@Override
			  public void onAdLoaded() {

				if (interstitial.isLoaded()) {
			      interstitial.show();
			    }
			  }

			  @Override
			  public void onAdFailedToLoad(int errorCode) {
				  String message = "onFailedToReceiveAd (" + errorCode + ")";

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
	}
	
	public void StoploadingAd() {

	}
}
