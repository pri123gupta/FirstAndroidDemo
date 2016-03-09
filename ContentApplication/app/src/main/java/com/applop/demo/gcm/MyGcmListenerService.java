
/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.applop.demo.gcm;
        import android.app.Notification;
        import android.app.NotificationManager;
        import android.app.PendingIntent;
        import android.content.Context;
        import android.content.Intent;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.media.RingtoneManager;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.NotificationCompat;
        import android.text.Html;
        import android.util.Log;

        import com.applop.demo.R;
        import com.applop.demo.activities.MainActivity;
        import com.applop.demo.activities.SplashActivity;
        import com.applop.demo.activities.StoryDetailActivity;
        import com.applop.demo.helperClasses.AnalyticsHelper;
        import com.applop.demo.helperClasses.Helper;
        import com.applop.demo.model.NameConstant;
        import com.applop.demo.model.Story;
        import com.google.android.gms.gcm.GcmListenerService;

        import java.nio.charset.Charset;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    private String notification_type;
    private int _id;
    private String _title;
    private String _message;
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle dataJSON) {
        try {

            _id = (int) (long) System.currentTimeMillis();
            _title = ((String) dataJSON.getString("title")).trim();
            _title = Html.fromHtml(_title).toString();
            _message = ((String) dataJSON.getString("alert")).trim();
            _message = Html.fromHtml(_message).toString();
            handleNofificationNavigation(this,dataJSON);
        } catch (Exception e) {

            e.printStackTrace();
        }

        // [START_EXCLUDE]
        /**
         * Production applications would usually process the message here.
         * Eg: - Syncing with server.
         *     - Store message in local database.
         *     - Update UI.
         */

        /**
         * In some cases it may be useful to show a notification indicating to the user
         * that a message was received.
         */
        // [END_EXCLUDE]
    }

    private void handleNofificationNavigation(Context context,Bundle notif_obj) {
        try {

            notification_type = notif_obj.getString("notification_type","");
            if(!notification_type.equalsIgnoreCase("")) {
                if (notification_type.equalsIgnoreCase("OPEN_APP")) {
                    openApp(context,"");
                }else if (notification_type.equalsIgnoreCase("post_promotion")){
                    String alertData;
                    alertData = notif_obj.getString("alert","");

                    String post_id = notif_obj.getString("post_id","");
                    if (!post_id.equalsIgnoreCase(""))
                        gotoPost(context,post_id,alertData);

                }else if (notification_type.equalsIgnoreCase("general")){
                    String alertData;
                    alertData = notif_obj.getString("alert","");
                    openApp(context, alertData);
                }
            }
        } catch (Exception e) {

        }
    }

    private void push(Context _context,PendingIntent contentIntent) {
        int notifyID = _id;
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                _context.getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(_context)
                        .setContentTitle(_title)
                        .setTicker(null)
                        .setContentText(_message)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(notificationLargeIconBitmap)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(_title));
        mBuilder.setTicker(null);
        mBuilder.setPriority(Notification.PRIORITY_HIGH);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        /*mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);*/
        NotificationManager mNotificationManager = (NotificationManager)
                _context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(notifyID, mBuilder.build());
    }

    private void openApp(Context context,String alertMessage) {
        Intent intent;
        intent = new Intent(context, MainActivity.class);
        intent.putExtra(NameConstant.ALERT_NOTIFICATION_INTENT_EXTRA_NAME,alertMessage);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, _id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        push(context, contentIntent);
    }

    private void gotoPost(final Context context,String post_id, final String alertMessage){
        Intent intent = new Intent(context, StoryDetailActivity.class);
        intent.putExtra("notification", true);
        intent.putExtra("position", "0");
        Story story = new Story();
        story.postId = post_id;
        intent.putExtra("story",story);
        intent.putExtra(NameConstant.ALERT_NOTIFICATION_INTENT_EXTRA_NAME, alertMessage);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent contentIntent = PendingIntent.getActivity(context, _id,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        push(context,contentIntent);
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}