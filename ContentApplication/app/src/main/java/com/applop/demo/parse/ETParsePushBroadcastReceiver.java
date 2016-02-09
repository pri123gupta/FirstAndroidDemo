package com.applop.demo.parse;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.widget.Toast;

import com.applop.demo.R;
import com.applop.demo.activities.MainActivity;
import com.applop.demo.activities.StoryDetailActivity;
import com.applop.demo.model.User;
import com.applop.demo.model.Story;
import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

import org.json.JSONObject;

import java.nio.charset.Charset;

public class ETParsePushBroadcastReceiver extends ParsePushBroadcastReceiver {
    private static final String TAG = "RWParsePushBroadcastReceiver";
    private static final String PARSE_DATA_KEY = "com.parse.Data";
    private String notification_type;
    private int _id;
    private String _title;
    private String _message;

    @Override
    public void onPushOpen(Context context, Intent intent) {
        ParseAnalytics.trackAppOpenedInBackground(intent);
        Bundle extras = intent.getExtras();
        if (extras.containsKey(PARSE_DATA_KEY)) {
            try {
                JSONObject dataJSON = new JSONObject(extras.getString(PARSE_DATA_KEY));
                handleNofificationNavigation(context, dataJSON);
                Toast.makeText(context, "success", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "my error" + e, Toast.LENGTH_LONG).show();
            }
        } else {
            openApp(context, "");
        }
    }
    private void handleNofificationNavigation(Context context, JSONObject notif_obj) {
        try {
            if (notif_obj.has("notification_type")) {
                notification_type = notif_obj.getString("notification_type");
                //open app
//                if (notification_type.equalsIgnoreCase("OPEN_APP")) {
                    openApp(context, "");
//                } else
                    //promotion
                    if (notification_type.equalsIgnoreCase("post_promotion")) {
                        String alertData;
                        if (notif_obj.has("alert")) {
                            alertData = notif_obj.getString("alert");
                        } else {
                            alertData = "";
                        }
                        if (notif_obj.has("post_id")) {
                            String post_id = notif_obj.getString("post_id");
                            if (!post_id.equalsIgnoreCase("")) {
                                gotoPost(context, post_id, alertData);
                            }
                        }
                    }
                    //general
                    else if (notification_type.equalsIgnoreCase("general")) {
                        String alertData;
                        if (notif_obj.has("alert")) {
                            alertData = notif_obj.getString("alert");
                        } else {
                            alertData = null;
                            openApp(context, alertData);
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openApp(Context context, String alertMessage) {
        Intent intent;
        intent = new Intent(context, MainActivity.class);
        intent.putExtra("alert_message", alertMessage);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        push(context, contentIntent);
    }

    private void push(Context _context, PendingIntent _contentIntent) {
        int notify_ID = _id;
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(_context.getResources(), R.drawable.ic_launcher);
        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new android.support.v4.app.NotificationCompat.Builder(_context)
                        .setContentTitle(_title)
                        .setTicker(_title)
                        .setContentText(_message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(notificationLargeIconBitmap)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle()
                                .bigText(_message));
        mBuilder.setTicker(null);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(_contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        NotificationManager mNotificationManager = (NotificationManager) _context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notify_ID, mBuilder.build());
    }

    private void openComment(Context context, String post_id, String email) {
        User user = User.getInstance(context);
//        if (email.equalsIgnoreCase(user.email)) {
        //}
        Intent intent = new Intent(context, StoryDetailActivity.class);
        intent.putExtra("notification", true);
        intent.putExtra("comment", true);
        Story story = new Story();
        story.postId = post_id;
        intent.putExtra("story", story);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        push(context, contentIntent);
        return;

    }

    private void gotoPost(Context context, String post_id, String alertMessage) {
        Intent intent;
        intent = new Intent(context, StoryDetailActivity.class);
        intent.putExtra("notification", true);
        intent.putExtra("position", "0");
        Story story = new Story();
        story.postId = post_id;
        intent.putExtra("story", story);
        intent.putExtra("alert_message", alertMessage);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, _id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        push(context, contentIntent);
    }

    @Override
    public void onPushReceive(Context context, Intent intent) {
        if (intent.getExtras().containsKey(PARSE_DATA_KEY)) {
            try {
                JSONObject dataJSON = new JSONObject(intent.getExtras().getString(PARSE_DATA_KEY));
                _id = (int) (long) System.currentTimeMillis();
                _title = ((String) dataJSON.getString("title")).trim();
                _message = ((String) dataJSON.getString("alert")).trim();
                final Charset ISO_8859_1 = Charset.forName("ISO-8859-1");
                final Charset UTF_8 = Charset.forName("UTF-8");
                byte ptext[] = _message.getBytes(ISO_8859_1);
                _message = new String(ptext, UTF_8);
                handleNofificationNavigation(context, dataJSON);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
