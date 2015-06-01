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
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

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
            jsonLocationInfo = getLocationInfo();
            writeToFile(getApplicationContext(), jsonLocationInfo.toString(), Place.MARKET);
        }
        else {
            jsonLocationInfo = readJSONObjet(getApplicationContext(), Place.MARKET);
        }

        assert jsonLocationInfo != null;
        Log.i("#hashtag", jsonLocationInfo.toString());
    }

    private boolean isWifiConnected(){
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
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

    private JSONObject readJSONObjet(Context mContext, String name){
        String JSONString;
        JSONObject JSONObject;
        try {
            InputStream inputStream = mContext.openFileInput(name);
            int sizeOfJSONFile = inputStream.available();
            byte[] bytes = new byte[sizeOfJSONFile];
            inputStream.read(bytes);
            inputStream.close();
            JSONString = new String(bytes, "UTF-8");
            JSONObject = new JSONObject(JSONString);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        catch (JSONException x) {
            x.printStackTrace();
            return null;
        }
        return JSONObject;
    }


}
