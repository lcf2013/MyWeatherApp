package com.zhenai.myweatherapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zhenai.myweatherapp.R;
import com.zhenai.myweatherapp.util.HttpCallbackListener;
import com.zhenai.myweatherapp.util.HttpUtil;
import com.zhenai.myweatherapp.util.Utility;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WeatherActivity extends AppCompatActivity {
    @Bind(R.id.switch_city)
    Button switchCity;
    @Bind(R.id.city_name)
    TextView cityName;
    @Bind(R.id.refresh_weather)
    Button refreshWeather;
    @Bind(R.id.publish_text)
    TextView publishText;
    @Bind(R.id.current_data)
    TextView currentData;
    @Bind(R.id.weather_desp)
    TextView weatherDesp;
    @Bind(R.id.temp1)
    TextView temp1;
    @Bind(R.id.temp2)
    TextView temp2;
    @Bind(R.id.weather_info_layout)
    LinearLayout weatherInfoLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        String countryCode=getIntent().getStringExtra("country_code");
        if(!TextUtils.isEmpty(countryCode)){
            publishText.setText("同步中...");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityName.setVisibility(View.INVISIBLE);
            queryWeatherCode(countryCode);
        }else{
            showWeather();
        }

    }

    @OnClick(R.id.switch_city)
    public void switchCity(){
        Intent intent = new Intent(this,ChooseAreaActivity.class);
        intent.putExtra("from_weather_activity", true);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.refresh_weather)
    public void refreshWeather(){
        publishText.setText("同步中...");
        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        String weathercode=prefs.getString("weather_code","");
        if(!TextUtils.isEmpty(weathercode)){
            queryWeatherCode(weathercode);
        }
    }

    private void showWeather() {
        SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
        cityName.setText(prefs.getString("city_name",""));
        temp1.setText(prefs.getString("temp1",""));
        temp2.setText(prefs.getString("temp2",""));
        weatherDesp.setText(prefs.getString("weather_desp",""));
        publishText.setText("今天"+prefs.getString("publish_time",""));
        currentData.setText(prefs.getString("currente_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityName.setVisibility(View.VISIBLE);
    }

    private void queryWeatherCode(String countryCode) {
        String address="http://www.weather.com.cn/data/list3/city"+countryCode+".xml";
        Log.d("err",address);
        queryFromServer(address,"countryCode");
    }

    private void queryFromServer(String address, final String type) {
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                if ("countryCode".equals(type)) {
                    if (!TextUtils.isEmpty(response)) {
                        String[] array = response.split("\\|");
                        if (array != null && array.length == 2) {
                            String weatherCode = array[1];
                            queryWeatherInfo(weatherCode);
                        }
                    }


                } else if ("weatherCode".equals(type)) {
                    Utility.handleWeatherResponse(WeatherActivity.this,response);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                Log.d("err",e.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    private void queryWeatherInfo(String weatherCode) {
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
        queryFromServer(address,"weatherCode");
    }


}
