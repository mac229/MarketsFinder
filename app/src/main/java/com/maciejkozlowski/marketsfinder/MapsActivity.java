package com.maciejkozlowski.marketsfinder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maciejkozlowski.marketsfinder.Data.Place;
import com.maciejkozlowski.marketsfinder.Services.PlacesDownloader;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    public static final String ACTION_GETTED_DATA = "com.maciejkozlowski.kfdpl.ACTION_GETTED_DATA";
    private GoogleMap mMap;
    private ArrayList<Place> places = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intentService = new Intent(getApplicationContext(), PlacesDownloader.class);
        startService(intentService);

        setUpMapIfNeeded();
       // Toast.makeText(getApplicationContext(), "Pobrano dane", Toast.LENGTH_SHORT).show();

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "Pobrano dane", Toast.LENGTH_SHORT).show();
        }
    };

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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        for (int i = 0; i < 2; i++) {
            final String name = "title " + i;
            MarkerOptions marker = new MarkerOptions().position(new LatLng(10*i, 10*i)).title(name);
            mMap.addMarker(marker);


        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
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
