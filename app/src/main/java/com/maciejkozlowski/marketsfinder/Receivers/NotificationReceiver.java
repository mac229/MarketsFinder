package com.maciejkozlowski.marketsfinder.Receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.maciejkozlowski.marketsfinder.Data.Place;
import com.maciejkozlowski.marketsfinder.PlaceDetailsActivity;
import com.maciejkozlowski.marketsfinder.R;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String ACTION_GETTED_NOTIFICATION =
            "com.maciejkozlowski.marketsfinder.ACTION_GETTED_NOTIFICATION";

    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {

        final Place place = intent.getParcelableExtra(PlaceDetailsActivity.PLACE_EXTRA);

        notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent activityIntent = new Intent(context, PlaceDetailsActivity.class);
        activityIntent.putExtra(PlaceDetailsActivity.PLACE_EXTRA, place);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, activityIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        final Notification notification =
                builder.setContentTitle("Znaleziono biedronkę w pobliżu!")
                .setContentText(place.address)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.small)
                .build();

        notificationManager.notify(0, notification);

    }
}
