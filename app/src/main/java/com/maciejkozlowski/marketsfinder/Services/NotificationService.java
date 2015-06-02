package com.maciejkozlowski.marketsfinder.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.maciejkozlowski.marketsfinder.Data.Place;
import com.maciejkozlowski.marketsfinder.Helpers.PlacesReader;
import com.maciejkozlowski.marketsfinder.Localization.MyLocation;
import com.maciejkozlowski.marketsfinder.MapsActivity;
import com.maciejkozlowski.marketsfinder.PlaceDetailsActivity;
import com.maciejkozlowski.marketsfinder.Receivers.NotificationReceiver;

import java.util.ArrayList;

public class NotificationService extends IntentService {

    public static final String NOTIFICATION_SERVICE_NAME =
            "com.maciejkozlowski.marketsfinder.NOTIFICATION_SERVICE_NAME";

    private static double MAX_DISTANCE = 0.03;   // 1° = 111,112km

    public NotificationService() {
        super(NOTIFICATION_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        boolean work = true;
        ArrayList<Place> places = PlacesReader.getPlaces(getApplicationContext());

        while (work) {
            for (int i = 0; i < places.size(); i++)
                if (isNearby(places.get(i))) {
                    sendInformation(places.get(i));
                    wait(300000);
                }
            wait(60000);
        }
    }

    private boolean isNearby(Place place){
        double lat = Math.abs(MyLocation.lat - place.lat);
        double lon = Math.abs(MyLocation.lon - place.lon);

        return (lat < MAX_DISTANCE && lon < MAX_DISTANCE);
    }

    private void sendInformation(Place place){
        Intent intent = new Intent(NotificationReceiver.ACTION_GETTED_NOTIFICATION);
        intent.putExtra(PlaceDetailsActivity.PLACE_EXTRA, place);
        sendBroadcast(intent);
    }

    private void wait(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
