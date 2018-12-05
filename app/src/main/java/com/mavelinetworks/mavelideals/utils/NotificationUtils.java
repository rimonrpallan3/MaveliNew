package com.mavelinetworks.mavelideals.utils;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.RemoteViews;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.mavelinetworks.mavelideals.AppController;
import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.appconfig.AppConfig;
import com.mavelinetworks.mavelideals.network.VolleySingleton;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Droideve on 6/26/2016.
 */
public class NotificationUtils {

    public static void sendNotification(Context context,String title,String messageBody, Bitmap icon, Class classToOpen,Map<String,String> dataToSend){
        sendNotification(context,title,messageBody,icon,null,classToOpen,dataToSend);
    }

    public static void sendNotification(final Context context, final String title, String messageBody, Bitmap icon, final String bigImageUrl, Class classToOpen, Map<String,String> dataToSend) {

        Intent intent = new Intent(context, classToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        for (String key : dataToSend.keySet()){
            intent.putExtra(key, dataToSend.get(key));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        final NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final NotificationCompat.Builder notificationBuilder ;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String id ="campaign_notif";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
            notificationBuilder = new NotificationCompat.Builder(context, id);

        }else {
            notificationBuilder =  new NotificationCompat.Builder(context);
        }

        notificationBuilder.setLargeIcon(icon);/*Notification icon image*/
        notificationBuilder.setSmallIcon(R.drawable.ic_small_white);
        notificationBuilder.setContentTitle(title);

        if(messageBody!=null)
            notificationBuilder.setContentText(messageBody);


        if (AppConfig.NOTIFICATION_SOUND){
            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notificationBuilder.setSound(defaultSoundUri);
            notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        }

        notificationBuilder.setContentIntent(pendingIntent);

        if(bigImageUrl!=null){

            ImageRequest imageRequest = new ImageRequest(bigImageUrl, new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {

                    if(AppConfig.APP_DEBUG){
                        // notificationBuilder.setContentTitle(title+" (with banner) "+response.getByteCount());
                        Log.e("_ImageRequest__","ImageRequest "+bigImageUrl);
                    }
                    NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(response);
                    s.setSummaryText("");
                    notificationBuilder.setStyle(s);


                    notificationManager.notify(101 /* ID of notification */, notificationBuilder.build());


                }
            }, 0, 0, null, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            VolleySingleton.getInstance(context).getRequestQueue().add(imageRequest);

            return;
        }

        notificationManager.notify(101 /* ID of notification */, notificationBuilder.build());

    }



    private Context mContext;

    public static  int NOTIFY_ID = 0;

    public NotificationUtils() {
    }

    public NotificationUtils(Context mContext) {
        this.mContext = mContext;
        this.multi=false;
    }

    private boolean multi=false;
    public NotificationUtils(Context mContext, boolean multi) {

        this.mContext = mContext;
        this.multi = multi;
    }




    public void showNotificationMessage(final String title, final String message, final String timeStamp,
                                        Intent intent, String imageUrl, String iconUrl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // notification icon
        final int icon = R.drawable.ic_launcher;

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        final PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        mContext,
                        0,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );


        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                mContext);

        final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/chord0.mp3");


        if (imageUrl != null && !TextUtils.isEmpty(imageUrl)) {

            if (imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {

                showBigNotification(imageUrl,iconUrl, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);

            }else{

                showSmallNotification(iconUrl, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);
            }
        } else {

            showSmallNotification(iconUrl,mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound);

        }
    }


    private void showSmallNotification(String iconUrl, NotificationCompat.Builder mBuilder, int icon,
                                       String title, String message, String timeStamp,
                                       PendingIntent resultPendingIntent, Uri alarmSound) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        if (AppConfig.GCMConfig.appendNotificationMessages) {
            // store the notification in shared pref first
            AppController.getInstance().getPrefManager().addNotification(message);

            // get the notifications from shared preferences
            String oldNotification = AppController.getInstance().getPrefManager().getNotifications();

            List<String> messages = Arrays.asList(oldNotification.split("\\|"));

            for (int i = messages.size() - 1; i >= 0; i--) {
                inboxStyle.addLine(messages.get(i));
            }
        } else {
            inboxStyle.addLine(message);
        }



        Notification notification;
        notification = mBuilder.setTicker(title).setWhen(0)
                .setAutoCancel(true)
               // .setLargeIcon(iconBitmap)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                //.setSound(alarmSound)
                .setStyle(inboxStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_small_white)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);


        final RemoteViews contentView = notification.contentView;
        final int iconId = android.R.id.icon;

        if(iconUrl!=null && !iconUrl.isEmpty()) {
            Log.e("images","addToNotification");
            Picasso.with(mContext)
                    .load(iconUrl).into(contentView, iconId, NOTIFY_ID, notification);
        }

        if(multi==true) {
            notificationManager.notify(NOTIFY_ID, notification);
        }else{
            notificationManager.notify(NOTIFY_ID, notification);
        }
    }


    private void showBigNotification(String bigImageUrl, String iconUrl,
                                     NotificationCompat.Builder mBuilder, int icon, String title,
                                     String message, String timeStamp, PendingIntent resultPendingIntent,
                                     Uri alarmSound) {

        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        //bigPictureStyle.bigPicture(bitmap);

        Notification notification;
        notification = mBuilder.setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                //.setSound(alarmSound)
                //.setLargeIcon(iconBitmap)
                .setStyle(bigPictureStyle)
                .setWhen(getTimeMilliSec(timeStamp))
                .setSmallIcon(R.drawable.ic_small_white)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))
                .setContentText(message)
                .build();

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion >= Build.VERSION_CODES.JELLY_BEAN) {

            final RemoteViews bigContentView;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

                bigContentView = notification.bigContentView;
                int bigIconId = mContext.getResources().getIdentifier("android:id/big_picture", null, null);
                Picasso.with(mContext)
                        .load(bigImageUrl).into(bigContentView, bigIconId, NOTIFY_ID, notification);
            }

        }

        final RemoteViews contentView = notification.contentView;
        final int iconId = android.R.id.icon;

        if(iconUrl!=null && !iconUrl.isEmpty())
            Picasso.with(mContext)
                    .load(iconUrl).into(contentView, iconId, NOTIFY_ID, notification);



        if(multi==true) {
            notificationManager.notify(NOTIFY_ID, notification);
        }else{
            notificationManager.notify(NOTIFY_ID, notification);
        }


    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public static void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + AppController.getInstance().getApplicationContext().getPackageName() + "/raw/chord0");
            Ringtone r = RingtoneManager.getRingtone(AppController.getInstance().getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playMessageSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + AppController.getInstance().getApplicationContext().getPackageName() + "/raw/all_eyes_on_me");
            Ringtone r = RingtoneManager.getRingtone(AppController.getInstance().getApplicationContext(), alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void playNotificationSoundSuccess() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + AppController.getInstance().getApplicationContext().getPackageName() + "/raw/snippy");
            Ringtone r = RingtoneManager.getRingtone(AppController.getInstance().getApplicationContext(), alarmSound);

            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    public static Boolean isActivityRunning(Class activityClass, Context context)
    {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (activityClass.getCanonicalName().equalsIgnoreCase(task.baseActivity.getClassName()))
                return true;
        }

        return false;
    }

    // Clears notification tray messages
    public static void clearNotifications() {
        NotificationManager notificationManager = (NotificationManager) AppController.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}