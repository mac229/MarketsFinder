package com.maciejkozlowski.marketsfinder.Services;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.maciejkozlowski.marketsfinder.Data.Place;
import com.maciejkozlowski.marketsfinder.Localization.MyLocation;
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
import org.json.JSONArray;
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

        while (MyLocation.lat == 0);
        MyLocation.province = getProvince();
        Log.i("#hashtag", MyLocation.province);

        if (isWifiConnected()){
            getMarketsList();
        }

        sendInformation();
    }

    private void sendInformation(){
        Intent intent = new Intent(MapsActivity.ACTION_GETTED_DATA);
        sendBroadcast(intent);
    }

    private String getProvince(){
        String result = "";

        JSONObject jsonObject = getLocationInfo();
        result = getProvinceFromJSON(jsonObject);

        return result;
    }

    private JSONObject getLocationInfo(){
        StringBuilder stringBuilder = new StringBuilder();
        try {
            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, 3000); // 3s max for connection
            HttpConnectionParams.setSoTimeout(httpParameters, 4000); // 4s max to get data
            HttpClient client = new DefaultHttpClient(httpParameters);
            HttpGet httpget = new HttpGet("http://maps.google.com/maps/api/geocode/json?latlng="
                    + MyLocation.lat + "," + MyLocation.lon + "&sensor=false");
            HttpResponse response = client.execute(httpget); // Executeit
            HttpEntity entity = response.getEntity();
            InputStream is = entity.getContent(); // Create an InputStream with the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            String line;
            while ((line = reader.readLine()) != null) { // Read line by line
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {
            Log.i("#hashtag", e.toString());
            e.printStackTrace();
        }
        return jsonObject;
    }

    private String getProvinceFromJSON(JSONObject jsonObject) {
        String result = "";
        try {
            result = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(5)
                    .getString("long_name");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }

    private boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    private ArrayList<Place> getMarketsList(){
        ArrayList<Place> list = new ArrayList<>();
        JSONObject JSONobject = new JSONObject();
        JSONArray array = new JSONArray();
        try {
            for (int i = 1; i < 14; i++) {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 3000); // 3s max for connection
                HttpConnectionParams.setSoTimeout(httpParameters, 4000); // 4s max to get data
                HttpClient client = new DefaultHttpClient(httpParameters);
                HttpGet httpget = new HttpGet("http://www.promoceny.pl/sklepy/szukaj/biedronka/" +
                        MyLocation.province + "/p/" + i);
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
                        LatLng latLng = getLocationFromAddress(fScan.match().group(1));
                        JSONObject object = new JSONObject();
                        object.put("address", fScan.match().group(1));
                        object.put("hours", fScan.match().group(3));
                        object.put("lat", latLng.latitude);
                        object.put("lon", latLng.longitude);
                        array.put(object);
                        Log.i("#hashtag", fScan.match().group(1) + " = " + latLng.longitude + ", " + latLng.latitude);
                    }
                }
                is.close(); // Close the stream
            }
            JSONobject.put("Items", array);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        writeToFile(getApplicationContext(), JSONobject.toString(), Place.MARKET);
        return list;
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(getApplicationContext());
        List<Address> address;
        LatLng p1;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (Exception ex) {
            p1 = new LatLng(0.0, 0.0);
            ex.printStackTrace();
        }

        return p1;
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
