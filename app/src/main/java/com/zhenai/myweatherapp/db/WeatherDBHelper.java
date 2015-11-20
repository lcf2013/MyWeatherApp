package com.zhenai.myweatherapp.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by admin on 2015/11/20.
 */
public class WeatherDBHelper extends SQLiteOpenHelper {

    private final String PROVINCE_TABLE="create table province(id integer primary key autoincrement," +
            "province_name text," +
            "province_code text)";
    private final String CITY_TABLE="create table city(id integer primary key autoincrement," +
            "city_name text," +
            "city_code text," +
            "province_id integer)";
    private final String COUNTRY_TABLE="create table country(id integer primary key autoincrement," +
            "country_name text," +
            "country_code text," +
            "city_id integer)";

    public WeatherDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    public WeatherDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PROVINCE_TABLE);
        sqLiteDatabase.execSQL(CITY_TABLE);
        sqLiteDatabase.execSQL(COUNTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
