package com.maciejkozlowski.marketsfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maciejkozlowski.marketsfinder.Data.Place;
import com.maciejkozlowski.marketsfinder.Helpers.PlacesReader;
import com.maciejkozlowski.marketsfinder.Localization.MyLocation;
import com.maciejkozlowski.marketsfinder.Services.NotificationService;
import com.maciejkozlowski.marketsfinder.Services.PlacesDownloader;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsActivity extends FragmentActivity {

    public static final String ACTION_GETTED_DATA =
            "com.maciejkozlowski.MarketsFinder.ACTION_GETTED_NOTIFICATION";
    private GoogleMap map;
    private ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startPlacesDownloader();
        MyLocation.setMyLocation(getApplicationContext());

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            places = PlacesReader.getPlaces(context);
            Toast.makeText(context, "Wczytano: " + places.size(), Toast.LENGTH_SHORT).show();
            setMarkers();
            startNotificationSystem();
        }
    };

    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setMyLocationEnabled(true);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(52, 19), 5.5f));

        }
    }

    private void setMarkers() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyLocation.lat, MyLocation.lon), 12.0f));
        final HashMap<String, Place> hashMap = new HashMap <String, Place>();
        if (map != null) {
            for (int i = 0; i < places.size(); i++) {
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(places.get(i).lat, places.get(i).lon));
                Marker m = map.addMarker(marker);
                hashMap.put(m.getId(), places.get(i));
            }

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                    intent.putExtra(PlaceDetailsActivity.PLACE_EXTRA, hashMap.get(marker.getId()));
                    startActivity(intent);
                    return false;
                }
            });
        }
    }

    private void startPlacesDownloader(){
        Intent intentService = new Intent(getApplicationContext(), PlacesDownloader.class);
        startService(intentService);
    }

    private void startNotificationSystem(){
        Intent intentService = new Intent(getApplicationContext(), NotificationService.class);
        startService(intentService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        IntentFilter intentFilter = new IntentFilter(ACTION_GETTED_DATA);
        getApplicationContext().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(broadcastReceiver);
    }
}
