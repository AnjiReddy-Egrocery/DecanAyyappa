package com.dst.ayyapatelugu.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedManager {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_AYYAPPA_TEMPLE_DATA = "ayyappa_temple_data";
    private static final String KEY_ZOOM_LEVEL = "zoom_level";

    // Other methods...

    public static void saveZoomLevel(Context context, float zoomLevel) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(KEY_ZOOM_LEVEL, zoomLevel);
        editor.apply();
    }

    public static float getZoomLevel(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(KEY_ZOOM_LEVEL, 15.0f);
    }

    // Make sure to replace the class type with the actual class type representing your temple data
    public static void saveTempleData(Context context, List<AyyappaTempleMapDataResponse.Result> templeList) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(templeList);

        editor.putString(KEY_AYYAPPA_TEMPLE_DATA, json);
        editor.apply();
    }

    // Make sure to replace the class type with the actual class type representing your temple data
    public static List<AyyappaTempleMapDataResponse.Result> getTempleData(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString(KEY_AYYAPPA_TEMPLE_DATA, "");

        if (!json.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<AyyappaTempleMapDataResponse.Result>>() {}.getType();
            return gson.fromJson(json, type);
        }

        return null;
    }
}
