package com.maciejkozlowski.marketsfinder.Services;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.maciejkozlowski.marketsfinder.Data.Place;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class PlacesDownloader extends IntentService {

    public static final String PLACE_DOWNLOADER_SERVICE_NAME =
            "com.maciejkozlowski.marketsfinder.PLACE_DOWNLOADER_SERVICE_NAME";

    public PlacesDownloader() {
        super(PLACE_DOWNLOADER_SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (isWifiConnected()){
            JSONObject jsonLocationInfo = getLocationInfo();
            Log.i("#hashtag", jsonLocationInfo.toString());

        }
        else {
            Log.i("#hashtag", "No WIFI connection");

        }


    }

    private boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    private JSONObject getLocationInfo(){
        StringBuilder stringBuilder = new StringBuilder();
        try {

            HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + Place.market + "&sensor=false");
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonObject;
    }
}
