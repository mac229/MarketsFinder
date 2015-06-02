package com.maciejkozlowski.marketsfinder.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.appcompat.R;
import android.util.Log;

import com.maciejkozlowski.marketsfinder.PlaceDetailsActivity;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_GETTED_NOTIFICATION =
            "com.maciejkozlowski.marketsfinder.ACTION_GETTED_NOTIFICATION";

    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(context, PlaceDetailsActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Notification notification =
                builder.setContentTitle("Znaleziono biedronkę w pobliżu!")
                .setContentText("")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .build();

        notificationManager.notify(0, notification);

    }
}
