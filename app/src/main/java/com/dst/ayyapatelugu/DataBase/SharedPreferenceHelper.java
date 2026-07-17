package com.dst.ayyapatelugu.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

import com.dst.ayyapatelugu.Model.AyyappaTempleMapDataResponse;
import com.dst.ayyapatelugu.Model.MapDataResponse;

import com.dst.ayyapatelugu.Model.TempleMapDataResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SharedPreferenceHelper {

    private static final String PREF_NAME = "YourAppPreferences";
    private static final String KEY_DATA = "map_data";
    private static final String KEY_NEARBY_ANNADANAM = "nearby_annadanam";

    private static final String KEY_NEARBY_TEMPLE = "nearby_temple";
    private static final String KEY_NEARBY_AYYPPATEMPLE = "nearby_ayyappatemple";
    private static final String KEY_ZOOM_LEVEL = "zoom_level";

    public static void saveTempleData(Context context, List<MapDataResponse.Result> temples) {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(temples);
        editor.putString(KEY_DATA, json);
        editor.apply();
    }

    public static List<MapDataResponse.Result> getTempleData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_DATA, "");
        Type type = new TypeToken<List<MapDataResponse.Result>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static void saveNearbyAnnadanam(
            Context context,
            ArrayList<MapDataResponse.Result> list) {


        SharedPreferences.Editor editor =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                ).edit();


        Gson gson = new Gson();


        String json =
                gson.toJson(list);



        editor.putString(
                KEY_NEARBY_ANNADANAM,
                json
        );


        editor.apply();

    }

    public static void saveNearbyTemple(
            Context context,
            ArrayList<TempleMapDataResponse.Result> list) {


        SharedPreferences.Editor editor =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                ).edit();


        Gson gson = new Gson();


        String json =
                gson.toJson(list);



        editor.putString(
                KEY_NEARBY_TEMPLE,
                json
        );


        editor.apply();

    }

    public static void saveNearbyAyyappaTemple(
            Context context,
            ArrayList<AyyappaTempleMapDataResponse.Result> list) {


        SharedPreferences.Editor editor =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                ).edit();


        Gson gson = new Gson();


        String json =
                gson.toJson(list);



        editor.putString(
                KEY_NEARBY_AYYPPATEMPLE,
                json
        );


        editor.apply();

    }




    // =====================================================
    // GET NEARBY ANNADANAM LIST
    // =====================================================

    public static ArrayList<MapDataResponse.Result> getNearbyAnnadanam(
            Context context) {


        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );


        String json =
                prefs.getString(
                        KEY_NEARBY_ANNADANAM,
                        ""
                );



        if(json.isEmpty()) {

            return new ArrayList<>();

        }



        Gson gson = new Gson();


        Type type =
                new TypeToken<ArrayList<MapDataResponse.Result>>() {
                }.getType();



        return gson.fromJson(
                json,
                type
        );

    }


    public static ArrayList<TempleMapDataResponse.Result> getNearbyTemple(
            Context context) {


        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );


        String json =
                prefs.getString(
                        KEY_NEARBY_TEMPLE,
                        ""
                );



        if(json.isEmpty()) {

            return new ArrayList<>();

        }



        Gson gson = new Gson();


        Type type =
                new TypeToken<ArrayList<TempleMapDataResponse.Result>>() {
                }.getType();



        return gson.fromJson(
                json,
                type
        );

    }


    public static ArrayList<AyyappaTempleMapDataResponse.Result> getNearbyAyyappaTemple(
            Context context) {


        SharedPreferences prefs =
                context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE
                );


        String json =
                prefs.getString(
                        KEY_NEARBY_AYYPPATEMPLE,
                        ""
                );



        if(json.isEmpty()) {

            return new ArrayList<>();

        }



        Gson gson = new Gson();


        Type type =
                new TypeToken<ArrayList<AyyappaTempleMapDataResponse.Result>>() {
                }.getType();



        return gson.fromJson(
                json,
                type
        );

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
