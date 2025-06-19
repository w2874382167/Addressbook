package com.jiong.addressbook;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.jiong.addressbook.activity.AddActivity;
import com.jiong.addressbook.activity.DetailActivity;
import com.jiong.addressbook.activity.UpdateActivity;
import com.jiong.addressbook.adapter.PeoAdapter;
import com.jiong.addressbook.bean.PeoBean;
import com.jiong.addressbook.utils.ApiInfo;
import com.jiong.addressbook.utils.DbUtils;
import com.jiong.addressbook.dao.PeoDao;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private static final org.apache.commons.logging.Log log = LogFactory.getLog(MainActivity.class);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        fetchLocationFromIP(); // 查询IP


        AppCompatImageView ivMiddle = findViewById(R.id.iv_middle);

        // 调用数据库
        // 创建数据库，同时生成表格
        DbUtils dbUtils = new DbUtils(MainActivity.this);
        // 将生成的数据库值赋给db，就可以使用db来调用
        dbUtils.db = dbUtils.getWritableDatabase();

        // 加载列表
        ListView listView = findViewById(R.id.book_list);
        List<PeoBean> result = PeoDao.getAllpeo(); // 查询所有信息
        if(result.isEmpty()){
            // 没有数据什么都不显示
            listView.setAdapter(null);
        }else{
            // 对数据进行字母排序，方便分组
            result.sort(new Comparator<PeoBean>() {
                @Override
                public int compare(PeoBean o1, PeoBean o2) {
                    String beginZ1 = o1.getBeginZ();
                    String beginZ2 = o2.getBeginZ();
                    // 如果两个都是字母或都是#，直接比较
                    if (beginZ1.equals("#") && beginZ2.equals("#")) {
                        return 0;
                    }
                    // #总是排在最后
                    if (beginZ1.equals("#")) {
                        return 1;
                    }
                    if (beginZ2.equals("#")) {
                        return -1;
                    }
                    // 其他情况按字母顺序排序
                    return beginZ1.compareTo(beginZ2);
                }
            });

            // peoAdapter加载一条一条的数据
            PeoAdapter peoAdapter = new PeoAdapter(MainActivity.this, result);
            listView.setAdapter(peoAdapter); // 列表加载数据
        }


        // 设置右边加号图标的点击事件
        ImageView ivAdd = findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_add = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent_add);
            }
        });

        // 搜索框里面有东西就进行搜索
        EditText search = findViewById(R.id.search_id);

        // 监听文本变化
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 实时响应文本变化
                performSearch(listView, s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    // 创建独立的搜索方法
    private void performSearch(ListView listView,String query) {
        listView.setAdapter(null);
        String title = query.trim();
        List<PeoBean> temp;
        if (title.isEmpty()) {
            temp = PeoDao.getAllpeo();
        } else {
            temp = PeoDao.searchPeo(title);
        }

        // 排序逻辑（保持不变）
        temp.sort(new Comparator<PeoBean>() {
            @Override
            public int compare(PeoBean o1, PeoBean o2) {
                String beginZ1 = o1.getBeginZ();
                String beginZ2 = o2.getBeginZ();

                if (beginZ1.equals("#") && beginZ2.equals("#")) {
                    return 0;
                }
                if (beginZ1.equals("#")) {
                    return 1;
                }
                if (beginZ2.equals("#")) {
                    return -1;
                }
                return beginZ1.compareTo(beginZ2);
            }
        });

        // 更新适配器
        PeoAdapter searchAdapter = new PeoAdapter(MainActivity.this, temp);
        listView.setAdapter(searchAdapter);
    }

    // 查询IP
    private String ipLocal = "";
    public void fetchLocationFromIP() {
        OkHttpClient client = new OkHttpClient();

        String url = ApiInfo.urlSet;

        Request request = new Request.Builder()
                .url(url)
                .get() // 某些API需要加UA
                .addHeader("User-Agent", "Mozilla/5.0")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                android.util.Log.e("getIp", "请求失败: " + e.getMessage());
                ipLocal = "南京市";
                return;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    android.util.Log.e("getIp", "响应失败，状态码: " + response.code());
                    ipLocal = "南京市";
                    return;
                }

                String responseBody = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONObject contentObject = jsonObject.optJSONObject("content");
                    JSONObject address_detail = contentObject.optJSONObject("address_detail");
                    String city = address_detail.optString("city", "");

                    ipLocal = city;
                    android.util.Log.d("getIp", "ipLocal = " + ipLocal);
                    queryWeather(ipLocal);

                } catch (Exception e) {
                    ipLocal = "南京市";
                    android.util.Log.e("getIp", "解析 JSON 出错: " + e.getMessage());
                }
            }
        });
    }

    // 查询天气
    private void queryWeather(String cityName) {
        OkHttpClient client = new OkHttpClient();
        String url = ApiInfo.urlWeather;
        Log.e("天气", "cityName: " + cityName);
        // 构造请求体
        RequestBody formBody = new FormBody.Builder()
                .add("city", cityName)
                .add("key", ApiInfo.WeatherKey)  // 替换为你的聚合数据 API Key
                .build();

        // 构造请求
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        // 异步发起请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("天气", "天气请求失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("天气", "天气请求响应失败，状态码: " + response.code());
                    return;
                }

                String responseBody = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(responseBody);
                    JSONObject resultObject = jsonObject.optJSONObject("result");
                    JSONObject realtimeObject = resultObject.optJSONObject("realtime");

                    String info = realtimeObject.optString("info", "");
                    String wid = realtimeObject.optString("wid", "");
                    Log.d("天气", "天气情况: " + info + "，天气编码 wid: " + wid);

                    // 通过 runOnUiThread 切换到主线程更新 UI
                    runOnUiThread(() -> {
                        // 设置天气文字描述
                        TextView tvMiddletext = findViewById(R.id.tv_middle_text);
                        tvMiddletext.setText(info);

                        // 动态加载 GIF：根据 wid 来决定加载哪个资源
                        ImageView ivMiddle = findViewById(R.id.iv_middle); // 假设你这个 ImageView ID 是这样写的
                        // ivMiddle.setImageResource(R.drawable.weather_18);
//                        String gif = "R.id.a"+wid;
                        String gif = "a"+ wid;
                        @SuppressLint("DiscouragedApi")
                        int gifResId = getResources().getIdentifier(gif, "raw", getPackageName());
                        Glide.with(MainActivity.this)
                                .asGif()
                                .load(gifResId)
                                .into(ivMiddle);

                    });
                } catch (Exception e) {
                    Log.e("天气", "天气 JSON 解析失败: " + e.getMessage());
                }
            }
        });
    }

}