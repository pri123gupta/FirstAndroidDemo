package com.applop.demo.parse;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.applop.demo.R;

import org.json.JSONObject;
public class GCMNotificationBuilder {

	private static final String TAG = "GCMNotificationBuilder";
	
	private static final String PARSE_DATA_KEY = "com.parse.Data";

	public static final String TITLE_PROMOTION	 		=	"TITLE_PROMOTION";
	public static final String WEB_PROMOTION 		=	"WEB_PROMOTION";
	public static final String OPEN_APP 			=	"OPEN_APP";

	public static final int NOTIFICATION_ID = 1;
	private int _id;
	private String _type;
	private String _title;
	private String _message;
	private String _content;
	private String _image;

	private Context _context;


	private GCMNotificationBuilder(Context context, Bundle extras) {
		_context = context;

		if(extras.containsKey(PARSE_DATA_KEY)) {
			try {
				JSONObject dataJSON = new JSONObject(extras.getString(PARSE_DATA_KEY));
				//JSONObject dataJSON = Parsejson.getJSONObject("data");
				_id = (int) (long) System.currentTimeMillis();
				_type = ((String) dataJSON.getString("notification_type")).trim();
				_title = ((String) dataJSON.getString("title")).trim();
				_message = ((String) dataJSON.getString("alert")).trim();
				_content = ((String) dataJSON.getString("content")).trim();
				//_image = ((String) dataJSON.getString("image")).trim();				
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {

			return;
		}

		
		//WLLog.d(TAG, " Constructor : _image = " + _image );

		if(_title == null || _message == null) {

			return;
		}

		if(_type.equalsIgnoreCase(OPEN_APP)) {
			openApp(_context);
		}  else if(_type.equalsIgnoreCase(WEB_PROMOTION)) {
			gotoUrl(_context,_content);
		} else if(_type.equalsIgnoreCase(TITLE_PROMOTION)) {
			gotoTitle(_context,_content);
		} else {
			openApp(_context);
		}

	}


	public static void notify(Context context, Bundle extras) {

		try {
			if(extras.containsKey(PARSE_DATA_KEY)) {			      
				new GCMNotificationBuilder(context, extras);
			} else {

			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}
	
	private void push(PendingIntent contentIntent) {


		int notifyID = _id;
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                _context.getResources(),
                R.drawable.ic_launcher);
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(_context)
		.setContentTitle(_title)
		.setTicker(_title)
		.setSmallIcon(R.drawable.ic_launcher)
                        .setLargeIcon(notificationLargeIconBitmap)
		.setContentText(_message)
		.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(_message));

		mBuilder.setAutoCancel(true);

		mBuilder.setContentIntent(contentIntent);

		NotificationManager mNotificationManager = (NotificationManager)
				_context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(notifyID, mBuilder.build());
	}

	private void gotoTitle(Context context, String title_id) {
		/*TitleDetailAsync  title_detail_async = new TitleDetailAsync(context,title_id);
		title_detail_async.execute();*/
	}

	private void gotoUrl(Context context,String web_url) {
		
		/*Intent intent = new Intent(_context, WebViewActivity.class);
		intent.putExtra("url", web_url);
		PendingIntent contentIntent = PendingIntent.getActivity(_context, _id, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		push(contentIntent);*/
	}

	private void openApp(Context context) {
		/*Intent intent = new Intent(_context, SplashActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(_context, _id, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		push(contentIntent);*/
	}


}