package com.maciejkozlowski.marketsfinder;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.maciejkozlowski.marketsfinder.Data.Place;


public class PlaceDetailsActivity extends Activity {

    public static String PLACE_EXTRA = "place";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        final Place place = getIntent().getParcelableExtra(PLACE_EXTRA);

        TextView textAddress = (TextView) findViewById(R.id.text_address);
        place.address = place.address.replace(", ", "\n");
        textAddress.setText(place.address);

        TextView textHours = (TextView) findViewById(R.id.text_hours);
        place.hours = place.hours.replace(", ", "\n");
        textHours.setText(place.hours);

        Button button = (Button) findViewById(R.id.button_navigate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + place.lat + "," + place.lon);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_place_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
