package com.zhenai.myweatherapp.util;

/**
 * Created by admin on 2015/11/23.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
