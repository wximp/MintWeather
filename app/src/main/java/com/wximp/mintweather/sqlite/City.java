package com.wximp.mintweather.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by smile on 2016/12/15.
 */

public class City {

    private MySqliteOpenhelper mySqliteOpenhelper;
    private SQLiteDatabase db;

    public City(Context context) {
        mySqliteOpenhelper = new MySqliteOpenhelper(context);
        db = mySqliteOpenhelper.getWritableDatabase();

    }

    public String getCityId(String cityname) {
        Cursor cursor = db.query("city", new String[]{"_id"}, "cn_name=" + "'" + cityname + "'", null, null, null, null);
        cursor.moveToFirst();
        String cityid = cursor.getString(0);
        return cityid;

    }

    public ArrayList<Map<String, Object>> search(String father) {
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Cursor cursor = db.query("city", new String[]{"_id", "cn_name"}, "father=" + "'" + father + "'", null, null, null, null);
        while (cursor.moveToNext()) {

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("_id", cursor.getString(0));
            map.put("cn_name", cursor.getString(1));
            Log.e("name", cursor.getString(1));
            list.add(map);
        }

        return list;


    }

}
