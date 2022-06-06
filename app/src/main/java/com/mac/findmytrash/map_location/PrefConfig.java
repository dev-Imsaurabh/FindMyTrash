package com.mac.findmytrash.map_location;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefConfig {

    public static void SetPref(Context context, String pref_name, String key, String value) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();


    }

    public static String GetPref(Context context, String pref_name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        String value = sharedPreferences.getString(key, "error");

        return value;

    }
}
