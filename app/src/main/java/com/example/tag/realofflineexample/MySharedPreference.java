package com.example.tag.realofflineexample;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreference {

    SharedPreferences preferences;

    public MySharedPreference(Context context) {
        preferences = context.getSharedPreferences("aklaty", 0);
    }


    public void SetData(String key, String value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();

    }



    public void SetData(String key, int value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();

    }

    public void SetData(String key, float value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(key, value);
        editor.commit();

    }

    public void SetData(String key, boolean value) {

        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();

    }

    public String getDataString(String key) {
        return preferences.getString(key, null);
    }

    public int getDataInt(String key) {
        return preferences.getInt(key, 0);
    }

    public float getDataFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public boolean getDataBool(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean getDataBool(String key, boolean The_default) {
        return preferences.getBoolean(key, The_default);
    }

}
