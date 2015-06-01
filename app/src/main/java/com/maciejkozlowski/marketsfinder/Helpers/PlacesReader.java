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

        ArrayList<Place> list = new ArrayList<>();
        JSONObject jsonObjectPlaces = readJSONObjet(context, Place.MARKET);
        list = getList(jsonObjectPlaces);

        return list;
    }

    private static ArrayList<Place> getList(JSONObject jsonObject) {
        // TODO: implement method
        ArrayList<Place> list = new ArrayList<>();
        try {
            JSONArray jsonArrayPlaces = (JSONArray) jsonObject.get("results");
            Log.i("#hashtag", String.valueOf(jsonArrayPlaces.length()));
            Place place = new Place();
            String longitute = jsonArrayPlaces.getJSONObject(0)
                    .getString("place_id");

            /*latitude = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");
*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static JSONObject readJSONObjet(Context mContext, String name){

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
