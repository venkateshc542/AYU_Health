package com.health.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CustomSharedPreferences {
    public static final String share_db_preferenced_db = "ayu_shared_db";
    public final static String SIMPLE_NULL = "";

    public enum SP_KEY {
        PATIENT_NAME,
        AGE,
        GENDER
    }


    public static void saveStringData(Context context, String data, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preferenced_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(spKey.name(), data);
        editor.commit();
    }

    public static String getStringData(Context context, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preferenced_db, Context.MODE_PRIVATE);
        String sourceId = sharedPreferences.getString(spKey.name(), SIMPLE_NULL);
        return sourceId;
    }

    public static void clear(Context context, SP_KEY spKey) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(share_db_preferenced_db, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(spKey.name());
        editor.commit();

    }
}