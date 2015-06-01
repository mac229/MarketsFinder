package com.maciejkozlowski.marketsfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.maciejkozlowski.marketsfinder.Services.PlacesDownloader;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    public static final String ACTION_GETTED_DATA = "com.maciejkozlowski.kfdpl.ACTION_GETTED_DATA";
    private GoogleMap map;
    private ArrayList<Place> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        startPlacesDownloader();
        MyLocation myLocation = new MyLocation(getApplicationContext());

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Zapisano dane", Toast.LENGTH_SHORT).show();
            places = PlacesReader.getPlaces(context);
            Toast.makeText(context, "Wczytano: " + places.size(), Toast.LENGTH_SHORT).show();
            setMarkers();
        }
    };


    private void setUpMapIfNeeded() {
        if (map == null) {
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            map.setMyLocationEnabled(true);
        }
    }

    private void setMarkers() {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(MyLocation.lat, MyLocation.lon), 12.0f));

        if (map != null) {
            for (int i = 0; i < places.size(); i++) {
                MarkerOptions marker = new MarkerOptions().position(
                        new LatLng(places.get(i).lat, places.get(i).lon));
                map.addMarker(marker);
            }

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Intent intent = new Intent(getApplicationContext(), PlaceDetailsActivity.class);
                    intent.putExtra("name", marker.getTitle());
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
