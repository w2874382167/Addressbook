package com.jiong.addressbook.dao;

import com.jiong.addressbook.utils.ApiInfo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.util.Log;

// 用于获取手机号码的归属地
public class Phone {
    private static final OkHttpClient client = new OkHttpClient();

    /**
     * 定义回调接口，用于处理异步请求结果
     */
    public interface PhoneCallback {
        void onSuccess(String result);

        void onFailure(String errorMsg);
    }

    /**
     * 根据手机号码/手机号码前7位查询号码归属地（异步方式）
     *
     * @param mobile   手机号码
     * @param callback 回调接口，用于处理请求结果
     */
    public static void queryMobileLocation(String mobile, final PhoneCallback callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("phone", mobile);
        params.put("key", ApiInfo.Apikey_phone);
        String queryParams = urlencode(params);

        doGetAsync(ApiInfo.urlPhone, queryParams, callback);
    }

    /**
     * 异步GET请求
     */
    private static void doGetAsync(String httpUrl, String queryParams, final PhoneCallback callback) {
        try {
            // 创建URL并添加查询参数
            java.net.URL url = new java.net.URL(new StringBuffer(httpUrl).append("?").append(queryParams).toString());

            // 创建请求
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            // 异步执行请求
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (callback != null) {
                        callback.onFailure("请求失败：" + e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    System.out.println("成功请求到api");
                    if (!response.isSuccessful() || response.body() == null) {
                        if (callback != null) {
                            callback.onFailure("请求失败，状态码：" + response.code());
                        }
                        return;
                    }

                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = JSONObject.fromObject(responseData);
                        int error_code = jsonObject.getInt("error_code");
                        if (error_code == 0) {
                            JSONObject result = jsonObject.getJSONObject("result");

                            // 拼接多个数据
                            String resultStr = result.getString("province") +
                                    result.getString("city") + "-" +
                                    result.getString("company");

                            if (callback != null) {
                                callback.onSuccess(resultStr);
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailure("调用接口失败：" + jsonObject.getString("reason"));
                            }
                        }
                    } catch (Exception e) {
                        if (callback != null) {
                            callback.onFailure("解析数据失败：" + e.getMessage());
                        }
                    }
                }
            });
        } catch (Exception e) {
            if (callback != null) {
                callback.onFailure("构建请求失败：" + e.getMessage());
            }
        }
    }

    /**
     * 将map型转为请求参数型
     */
    public static String urlencode(Map<String, ?> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, ?> i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String result = sb.toString();
        result = result.substring(0, result.lastIndexOf("&"));
        return result;
    }
}
