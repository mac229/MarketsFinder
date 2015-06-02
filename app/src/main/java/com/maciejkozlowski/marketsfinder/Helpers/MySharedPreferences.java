package com.maciejkozlowski.marketsfinder.Helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.maciejkozlowski.marketsfinder.Localization.MyLocation;

import junit.framework.Assert;


public class MySharedPreferences {

    public static final String MY_PREFS = "MY_PREFS";
    private static final String first_run = "first_run";
    private static final String province = "province";

    public static boolean isFirstRun(Context context){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(first_run, true);
    }

    public static void disableFirstRun(Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE).edit();
        editor.putBoolean(first_run, false);
        editor.apply();
    }

    public static void checkProvince(Context context) {

        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE);

        if (MyLocation.province == null) {
            MyLocation.province = prefs.getString(province, "");
        } else {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(province, MyLocation.province);
            editor.apply();
        }
        Log.i("#hashtag", MyLocation.province);
    }
}

