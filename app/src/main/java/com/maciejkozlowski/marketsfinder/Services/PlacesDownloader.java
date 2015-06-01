package com.maciejkozlowski.marketsfinder.Services;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.maciejkozlowski.marketsfinder.Data.Place;
import com.maciejkozlowski.marketsfinder.MapsActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PlacesDownloader extends IntentService {

    public static final String PLACE_DOWNLOADER_SERVICE_NAME =
            "com.maciejkozlowski.marketsfinder.PLACE_DOWNLOADER_SERVICE_NAME";

    public PlacesDownloader() {
        super(PLACE_DOWNLOADER_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        JSONObject jsonLocationInfo;
        if (isWifiConnected()){
            getMarketsList();
            //jsonLocationInfo = getLocationInfo();
            //writeToFile(getApplicationContext(), jsonLocationInfo.toString(), Place.MARKET);
        }

        sendInformation();
    }

    private void sendInformation(){
        Intent intent = new Intent(MapsActivity.ACTION_GETTED_DATA);
        sendBroadcast(intent);
    }

    private boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    private ArrayList<Place> getMarketsList(){
        ArrayList<Place> list = new ArrayList<>();
        for (int i = 1; i < 122; i++) {
            try {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 3000); // 3s max for connection
                HttpConnectionParams.setSoTimeout(httpParameters, 4000); // 4s max to get data
                HttpClient client = new DefaultHttpClient(httpParameters);
                HttpGet httpget = new HttpGet("http://www.promoceny.pl/sklepy/szukaj/biedronka/p/1");
                HttpResponse response = client.execute(httpget); // Executeit
                HttpEntity entity = response.getEntity();
                InputStream is = entity.getContent(); // Create an InputStream with the response
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                String line;
                //String pattern = "<td><a href=.(.*?).>Biedronka";
                String pattern = "<.a><.td><td>(.*?)<.td><td>(.*?)<.td><td>(.*?)<.td><td><a href";
                while ((line = reader.readLine()) != null) { // Read line by line
                    Scanner fScan = new Scanner(line);
                    while (fScan.findWithinHorizon(pattern, 0) != null) {
                        Place place = new Place();
                        place.address = fScan.match().group(1);
                        place.hours = fScan.match().group(3);
                        list.add(place);
                        Log.i("#hashtag", place.address);
                    }
                }
                is.close(); // Close the stream
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.i("#hashtag", String.valueOf(list.size()));
        return list;
    }


    private ArrayList<Double> getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        ArrayList<Double> p1 = new ArrayList<>();

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1.add(location.getLatitude() * 1E6);
            p1.add(location.getLongitude() * 1E6);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return p1;
    }

    private JSONObject getLocationInfo(){
        StringBuilder stringBuilder = new StringBuilder();
        try {

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + Place.MARKET + "&sensor=false");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();

            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    public void writeToFile(Context mContext, String data, String name) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(mContext.openFileOutput(name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }
}
