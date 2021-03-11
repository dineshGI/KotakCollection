package com.kotakcollection.FCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.kotakcollection.R;
import com.kotakcollection.activity.MainActivity;
import com.kotakcollection.utils.Util;

/**
 * Created by L on 09/05/2017.
 * Copyright (c) 2017 Centroida. All rights reserved.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        // Log.e("from",  remoteMessage.getData());
        Util.Logcat.e("data" + remoteMessage.getData());
        Util.Logcat.e("body" + "Notification Message Body: " + remoteMessage.getNotification().getBody());
        Util.Logcat.e("Title" + "Notification Message Title: " + remoteMessage.getNotification().getTitle());

        //Calling method to generate notification
        sendNotification(remoteMessage.getNotification().getBody());
        //  MyNotificationManager.getInstance(this).displayNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri defaultSoundUri = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.beep);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(getResources().getColor(R.color.white))
                .setContentTitle(getString(R.string.app_name))
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setContentIntent(pendingIntent);

      /*  if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notification.setSmallIcon(R.drawable.icon_transperent);
            notification.setColor(getResources().getColor(R.color.notification_color));
        } else {
            notification.setSmallIcon(R.drawable.icon);
        }*/


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
    }
}