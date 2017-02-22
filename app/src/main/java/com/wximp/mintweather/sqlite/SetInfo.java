package com.wximp.mintweather.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by smile on 2016/12/14.
 */

public class SetInfo {

    private SQLiteDatabase db;

    public SetInfo(Context context) {
        MySqliteOpenhelper mySqliteOpenhelper = new MySqliteOpenhelper(context);

        db = mySqliteOpenhelper.getWritableDatabase();
    }

    public int getNum() {
        final int result;
        Cursor cursor = db.query("info", null, null, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            result = cursor.getCount();

        } else
            result = 999;
        return result;

    }

    public boolean DelInfo() {
        int info = db.delete("info", null, null);
        if (info > 0)
            return true;
        else
            return false;
    }

    public boolean setReId(String _id) {
        ContentValues values = new ContentValues();
        values.put("_id", _id);
        long insert = db.insert("info", null, values);
        if (_id != null && insert > 0)
            return true;
        else
            return false;


    }

    public boolean setId(String city) {
        String cityid = null;
        Cursor cursor = db.query("city", new String[]{"_id"}, "cn_name=" + "'" + city + "'", null, null, null, null);
        while (cursor.moveToNext()) {
            cityid = cursor.getString(0);
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("_id", cityid);
        long insert = db.insert("info", null, values);
        if (cityid != null && insert > 0)
            return true;
        else
            return false;
    }

    public String getSetId() {
        Cursor cursor = db.query("info", new String[]{"_id"}, null, null, null, null, null);
        cursor.moveToFirst();
        String string = cursor.getString(0);
        Log.e("ID", string);
        return string;


    }


}
