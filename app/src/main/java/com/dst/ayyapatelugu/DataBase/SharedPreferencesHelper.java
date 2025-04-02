package com.dst.ayyapatelugu.DataBase;

import android.content.Context;
import android.content.SharedPreferences;

import com.dst.ayyapatelugu.Model.GuruSwamiModelList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class SharedPreferencesHelper {

    private static final String PREF_NAME = "guru_swami_prefs";
    private static final String KEY_GURU_SWAMI_LIST = "guru_swami_list";

    private final SharedPreferences preferences;


    public SharedPreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveGuruSwamiList(List<GuruSwamiModelList> guruSwamiList) {
        Gson gson = new Gson();
        String json = gson.toJson(guruSwamiList);
        preferences.edit().putString(KEY_GURU_SWAMI_LIST, json).apply();
    }

    public List<GuruSwamiModelList> getGuruSwamiList() {
        Gson gson = new Gson();
        String json = preferences.getString(KEY_GURU_SWAMI_LIST, null);
        Type type = new TypeToken<List<GuruSwamiModelList>>() {}.getType();
        return gson.fromJson(json, type);
    }


}
