package com.dst.ayyapatelugu.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedPreferenceManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_TEMPLE_DATA = "temple_data";
    private static final String KEY_ZOOM_LEVEL = "zoom_level";

    public static void saveTempleData(Context context, List<TempleMapDataResponse.Result> temples) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(temples);
        editor.putString(KEY_TEMPLE_DATA, json);
        editor.apply();
    }

    public static List<TempleMapDataResponse.Result> getTempleData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_TEMPLE_DATA, "");
        Type type = new TypeToken<List<TempleMapDataResponse.Result>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static void setZoomLevel(Context context, float zoomLevel) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putFloat(KEY_ZOOM_LEVEL, zoomLevel);
        editor.apply();
    }

    public static float getZoomLevel(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getFloat(KEY_ZOOM_LEVEL, 15.0f);
    }
}
