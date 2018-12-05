package com.mavelinetworks.mavelideals.Services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.mavelinetworks.mavelideals.R;
import com.mavelinetworks.mavelideals.activities.MainActivity;


public class ReminderService  extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */



    private static final int NOTIF_ID = 1;
    public ReminderService() {
        super("ReminderService");

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent i = new Intent(this,MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Event Notification ")
                .setContentText("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        manager.notify(0,builder.build());

    }
}
