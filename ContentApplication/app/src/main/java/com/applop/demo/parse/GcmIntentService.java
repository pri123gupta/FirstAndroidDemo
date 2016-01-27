package com.applop.demo.parse;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.applop.demo.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;


public class GcmIntentService extends IntentService {
	private static final String TAG = "GcmIntentService";
	
	private int NOTIFICATION_ID = 1;
	private NotificationManager mNotificationManager;
	NotificationCompat.Builder builder;

	public GcmIntentService() {
		super("GcmIntentService");

	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Bundle extras = intent.getExtras();
		//WLLog.d(TAG, " onHandleIntent start extras "+extras.toString());
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
		// The getMessageType() intent parameter must be the intent you received
		// in your BroadcastReceiver.
		String messageType = gcm.getMessageType(intent);


		if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
			/*
			 * Filter messages based on message type. Since it is likely that GCM
			 * will be extended in the future with new message types, just ignore
			 * any message types you're not interested in, or that you don't
			 * recognize.
			 */
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				//sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				//sendNotification("Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				sendNotification(extras);
			}
		}
		// Release the wake lock provided by the WakefulBroadcastReceiver.
		GcmBroadcastReceiver.completeWakefulIntent(intent);

	}

	// Put the message into a notification and post it.
	// This is just one simple example of what you might choose to do with
	// a GCM message.
	private void sendNotification(Bundle extras) {

		mNotificationManager = (NotificationManager)
				this.getSystemService(Context.NOTIFICATION_SERVICE);
		Bitmap icon = BitmapFactory.decodeResource(this.getResources(),
                R.drawable.ic_launcher);
		if(extras.getString("id") != null) {
			NOTIFICATION_ID = Integer.parseInt(extras.getString("id"));
		}
		/*PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, SplashActivity.class), 0);*/
		
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
		.setContentTitle(extras.getString("title"))
		.setSmallIcon(R.drawable.ic_launcher)
		.setTicker(null)
		.setLargeIcon(icon)
		.setContentText(extras.getString("message"))
		.setStyle(new NotificationCompat.BigTextStyle()
		.bigText(extras.getString("message")));
		
		
		mBuilder.setAutoCancel(true);

		
		/*mBuilder.setContentIntent(contentIntent);*/
		mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());

	}
}
