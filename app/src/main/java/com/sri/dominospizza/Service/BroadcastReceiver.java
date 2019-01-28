package com.sri.dominospizza.Service;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;


import com.google.firebase.messaging.RemoteMessage;
import com.sri.dominospizza.Common.Common;
import com.sri.dominospizza.OrderStatus;
import com.sri.dominospizza.R;

/**
 * Created by Scarecrow on 2/15/2018.
 */

public class BroadcastReceiver extends android.content.BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        playNotificationSound(context);
    }

    public void playNotificationSound(Context context) {
        try {
            Uri notificationS = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(context, notificationS);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
