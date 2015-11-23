package com.zhenai.myweatherapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zhenai.myweatherapp.model.City;
import com.zhenai.myweatherapp.model.Country;
import com.zhenai.myweatherapp.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/11/23.
 */
public class WeatherDB {
    public static final String db_name = "cool_weather";
    public static final int version = 1;
    private static WeatherDB weatherDB;
    private SQLiteDatabase db;

    private WeatherDB(Context context) {
        WeatherDBHelper dbHelper = new WeatherDBHelper(context, db_name, null, version);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static WeatherDB getInstance(Context context) {
        if (weatherDB == null) {
            weatherDB = new WeatherDB(context);
        }
        return weatherDB;
    }

    public void saveProvinde(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("province", null, values);
        }

    }

    public List<Province> loadProvinces() {
        List<Province> list = new ArrayList<>();
        Cursor cursor = db.query("province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                list.add(province);
            } while (cursor.moveToNext());
        }


        return list;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues value = new ContentValues();
            value.put("city_name", city.getCityName());
            value.put("city_code", city.getCityCode());
            value.put("province_id", city.getProvinceId());
            db.insert("city", null, value);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<>();
        Cursor cursor = db.query("city", null, "province_id=?", new String[]{String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                list.add(city);
            } while (cursor.moveToNext());
        }


        return list;
    }

    public void saveCountry(Country country) {
        if (country != null) {
            ContentValues values = new ContentValues();
            values.put("country_name", country.getCountryName());
            values.put("country_code", country.getCountryCode());
            values.put("city_id", country.getCityId());
            db.insert("country", null, values);

        }
    }

    public List<Country> loadCountries(int cityId){
        List<Country> list=new ArrayList<>();
        Cursor cursor=db.query("country",null,"city_id=?",new String[]{String.valueOf(cityId)},null,null,null);
        if(cursor.moveToFirst()){
            do{
                Country country=new Country();
                country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
                country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
                country.setCityId(cityId);
                list.add(country);

            }while(cursor.moveToNext());
        }

        return list;

    }

}
