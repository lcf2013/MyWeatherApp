package com.zhenai.myweatherapp.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhenai.myweatherapp.R;
import com.zhenai.myweatherapp.db.WeatherDB;
import com.zhenai.myweatherapp.model.City;
import com.zhenai.myweatherapp.model.Country;
import com.zhenai.myweatherapp.model.Province;
import com.zhenai.myweatherapp.util.HttpCallbackListener;
import com.zhenai.myweatherapp.util.HttpUtil;
import com.zhenai.myweatherapp.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/11/23.
 */
public class ChooseAreaActivity extends Activity {
    public static final int level_province = 0;
    public static final int level_city = 1;
    public static final int level_country = 2;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private WeatherDB weatherDB;
    private List<String> dataList = new ArrayList<String>();

    private List<Province> provinceList;
    private List<City> cityList;
    private List<Country> countryList;

    private Province selectedProvince;
    private City selectedCity;


    private int currentLevel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        if(prefs.getBoolean("city_selected",false)){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        findView();
        init();
    }

    private void init() {
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        weatherDB = WeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == level_province) {
                    selectedProvince = provinceList.get(i);
                    queryCities();
                } else if (currentLevel == level_city) {
                    selectedCity = cityList.get(i);
                    queryCounties();
                }else if(currentLevel==level_country){
                    String countryCode=countryList.get(i).getCountryCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("country_code",countryCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    private void queryCities() {
        cityList = weatherDB.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = level_city;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryProvinces() {
        provinceList = weatherDB.loadProvinces();
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());

            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = level_province;
        } else {
            queryFromServer(null, "province");
        }

    }

    private void queryCounties() {
        countryList = weatherDB.loadCountries(selectedCity.getId());
        if (countryList.size() > 0) {
            dataList.clear();
            for (Country c : countryList) {
                dataList.add(c.getCountryName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            currentLevel = level_country;
        } else {
            queryFromServer(selectedCity.getCityCode(), "country");
        }

    }

    private void queryFromServer(String code, final String type) {
        String address;
        if (!TextUtils.isEmpty(code)) {
            address = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            address = "http://www.weather.com.cn/data/list3/city.xml";
        }
        Log.d("err",address);
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (type.equals("province")) {
                    result = Utility.handlerProvinceResponse(weatherDB, response);
                } else if (type.equals("city")) {
                    result = Utility.handleCitiesResponse(weatherDB, response, selectedProvince.getId());
                } else if (type.equals("country")) {
                    result = Utility.handeCountriesResponse(weatherDB, response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("country".equals(type)) {
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(final Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Log.d("err",e.toString());
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_LONG).show();;
                    }
                });
            }
        });
    }

    private void closeProgressDialog() {
        if(progressDialog!=null)
            progressDialog.dismiss();
    }

    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void findView() {
        listView = (ListView) findViewById(R.id.list_view);
        titleText = (TextView) findViewById(R.id.title_text);
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==level_country){
            queryCities();
        }else if(currentLevel==level_city){
            queryProvinces();
        }else{
            finish();
        }
    }
}
