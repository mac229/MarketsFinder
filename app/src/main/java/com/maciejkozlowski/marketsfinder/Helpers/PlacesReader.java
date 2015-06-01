package com.maciejkozlowski.marketsfinder.Helpers;

import android.content.Context;
import android.util.Log;

import com.maciejkozlowski.marketsfinder.Data.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PlacesReader {

    public static ArrayList<Place> getPlaces(Context context){

        ArrayList<Place> list;
        JSONObject jsonObjectPlaces = readJSONObject(context, Place.MARKET);
        list = getList(jsonObjectPlaces);

        return list;
    }

    private static JSONObject readJSONObject(Context mContext, String name){

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
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return null;
        }
        return JSONObject;
    }

    private static ArrayList<Place> getList(JSONObject jsonObject) {
        ArrayList<Place> list = new ArrayList<>();
        try {
            JSONArray jsonArrayPlaces = jsonObject.getJSONArray("Items");
            Log.i("#hashtag", String.valueOf(jsonArrayPlaces.length()));
            for (int i = 0; i < jsonArrayPlaces.length(); i++){
                JSONObject jsonPlace = jsonArrayPlaces.getJSONObject(i);
                Place place = new Place();
                place.address = jsonPlace.getString("address");
                place.hours = jsonPlace.getString("hours");
                place.lat = jsonPlace.getDouble("lat");
                place.lon = jsonPlace.getDouble("lon");

                list.add(place);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

}
