package com.jiong.addressbook.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.jiong.addressbook.MainActivity;
import com.jiong.addressbook.R;
import com.jiong.addressbook.bean.PeoBean;
import com.jiong.addressbook.dao.PeoDao;

import android.util.Log;


public class DetailActivity extends AppCompatActivity {


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 获取id
        String id = getIntent().getStringExtra("id");

        // 获取LiveData对象
        LiveData<PeoBean> peoLiveData = PeoDao.getPeolive(id);

        // 添加观察者
        peoLiveData.observe(this, new Observer<PeoBean>() {
            @Override
            public void onChanged(PeoBean peoBean) {
                Log.e("detail", String.valueOf(peoBean));
                if (peoBean != null) {
                    // 数据已更新，此处peoBean包含完整的归属地信息
                    // 信息加载到界面
                    String sex = peoBean.getSex();
                    if (sex.equals("女")){
                        final LinearLayout detailBg = findViewById(R.id.detail_bg);
                        ImageView imageView = findViewById(R.id.detail_img);
                        // 背景颜色
                        detailBg.setBackgroundColor(Color.argb(255, 254, (int)240.3, (int)240.3));
                        imageView.setImageResource(R.drawable.wuman);// 头像
                    }
                    TextView nameView = findViewById(R.id.detail_name);
                    nameView.setText(peoBean.getName());// 名字
                    TextView phone_num = findViewById(R.id.detail_phone);
                    phone_num.setText(peoBean.getNum());// 电话号码
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView phoneLocated = findViewById(R.id.phone_Located);
                    phoneLocated.setText(peoBean.getnumberLocated()); // 号码归属地
                    TextView detail_sex = findViewById(R.id.detail_sex);
                    detail_sex.setText("性别：" + sex); // 性别
                    TextView detail_position = findViewById(R.id.detail_position);
                    detail_position.setText("备注： "+peoBean.getRemark()); // 备注
                }
            }
        });

        // ========= 打电话功能 ========
        ImageView callButton = findViewById(R.id.detail_call);
        // 监听
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断是否有打电话的权限
                // 获取电话权限的ID
                int id = ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.CALL_PHONE);
                if(id != PackageManager.PERMISSION_GRANTED){
                    // 没有权限，去请求
                    ActivityCompat.requestPermissions(DetailActivity.this, new String[]{Manifest.permission.CALL_PHONE},1);

                }
                // 打电话
                TextView phone_num = findViewById(R.id.detail_phone);
                makePhoneCall(phone_num.getText().toString().trim());
            }
        });

        // =========== 发短信 ===========
        ImageView messageButton = findViewById(R.id.detail_message);
        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(DetailActivity.this, Manifest.permission.SEND_SMS)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(DetailActivity.this,
                            new String[]{Manifest.permission.SEND_SMS},
                            1);
                }
                TextView phone_num = findViewById(R.id.detail_phone);
                // 获取电话号码
                String phoneNumber = phone_num.getText().toString().trim();

                // 检查电话号码是否为空
                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(DetailActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 创建短信意图
                Intent messageIntent = new Intent(Intent.ACTION_SENDTO);
                // 设置短信接收者
                messageIntent.setData(Uri.parse("smsto:" + phoneNumber));

                // 检查是否有应用可以处理此意图
                if (messageIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(messageIntent);
                } else {
                    Toast.makeText(DetailActivity.this, "没有找到可以发送短信的应用", Toast.LENGTH_SHORT).show();
                }

            }
        });

        // =========== 视频通话 ===========
        ImageView videoButton = findViewById(R.id.detail_video);
        videoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailActivity.this, "该功能即将上线", Toast.LENGTH_SHORT).show();
            }
        });

        // =========== 返回 ===========
        Button backButton = findViewById(R.id.btn_back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        // =========== 删除 ===========
        Button deleteButton = findViewById(R.id.btn_delete);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PeoDao.deletePeo(id);
                Toast.makeText(DetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                Intent backIntent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(backIntent);
                finish();
            }
        });

        // =========== 编辑 ===========
        Button editButton = findViewById(R.id.btn_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editIntent = new Intent(DetailActivity.this, UpdateActivity.class);
                editIntent.putExtra("id", id);
                startActivity(editIntent);
            }
        });
    }

    /**
     * 打电话 电话号码num
     * @param num
     */
    private void makePhoneCall(String num){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + num));
        startActivity(callIntent);
    }



}