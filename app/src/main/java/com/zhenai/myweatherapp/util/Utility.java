package com.zhenai.myweatherapp.util;

import android.text.TextUtils;

import com.zhenai.myweatherapp.db.WeatherDB;
import com.zhenai.myweatherapp.model.City;
import com.zhenai.myweatherapp.model.Country;
import com.zhenai.myweatherapp.model.Province;

/**
 * Created by admin on 2015/11/23.
 */
public class Utility {
    public synchronized static boolean handlerProvinceResponse(WeatherDB db, String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allProvinces = response.split(",");
            if (allProvinces != null && allProvinces.length > 0) {
                for (String p : allProvinces) {
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    db.saveProvinde(province);
                }
            }
            return true;
        }

        return false;
    }

    public synchronized static boolean handleCitiesResponse(WeatherDB db, String response, int provinceId) {

        if (!TextUtils.isEmpty(response)) {
            String[] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    db.saveCity(city);
                }
            }
            return true;
        }

        return false;
    }

    public synchronized static boolean handeCountriesResponse(WeatherDB db,
                                                              String response, int cityId) {

        if (!TextUtils.isEmpty(response)) {
            String[] countries = response.split(",");
            if (countries != null && countries.length > 0) {
                for (String c : countries) {
                    String[] array = c.split("\\|");
                    Country country=new Country();
                    country.setCountryCode(array[0]);
                    country.setCountryName(array[1]);
                    country.setCityId(cityId);
                    db.saveCountry(country);
                }
            }
            return true;
        }

        return false;
    }
}
