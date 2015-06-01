package com.maciejkozlowski.marketsfinder.Localization;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class MyLocation {

    public static double lon;
    public static double lat;
    public static String province;

    public MyLocation(final Context context) {

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                lat = location.getLatitude();
                lon = location.getLongitude();
                Log.i("#hashtag", lat + ", " + lon);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
}
