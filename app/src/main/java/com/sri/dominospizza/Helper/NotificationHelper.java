package com.sri.dominospizza.Helper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

import com.sri.dominospizza.R;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public class NotificationHelper extends ContextWrapper {

    private static final String DOMINOS_CHANNEL_ID = "com.sri.dominospizza.DominosPizza";
    private static final String DOMINOS_CHANNEL_NAME = "Domino's Pizza";

    private NotificationManager manager;

    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            createChannel();
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationChannel dominosChannel = new NotificationChannel(DOMINOS_CHANNEL_ID,
                DOMINOS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT);
        dominosChannel.enableLights(false);
        dominosChannel.enableVibration(true);
        dominosChannel.setLockscreenVisibility(android.app.Notification.VISIBILITY_PRIVATE);


        getManager().createNotificationChannel(dominosChannel);
    }

    public NotificationManager getManager() {
        if (manager == null)
            manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        return manager;
    }

    @TargetApi(Build.VERSION_CODES.O)
    public Notification.Builder getDominosChannelNotification(String title, String body, PendingIntent contentIntent,
                                                           Uri soundUri){
        return new Notification.Builder(getApplicationContext(), DOMINOS_CHANNEL_ID)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.ic_notification_domino)
                .setSound(soundUri)
                .setAutoCancel(false);
    }
}
