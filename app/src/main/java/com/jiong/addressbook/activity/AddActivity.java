package com.jiong.addressbook.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.jiong.addressbook.MainActivity;
import com.jiong.addressbook.R;
import com.jiong.addressbook.bean.PeoBean;
import com.jiong.addressbook.dao.PeoDao;

public class AddActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_add), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView name = findViewById(R.id.add_name);
        TextView phone = findViewById(R.id.add_phone);
        TextView sex = findViewById(R.id.add_sex);
        TextView remark = findViewById(R.id.add_remark);

        // 实现点击保存
        Button addButton = findViewById(R.id.btn_save_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameT = name.getText().toString().trim();
                String phoneT = phone.getText().toString().trim();
                String remarkT = remark.getText().toString().trim();
                String sexT = sex.getText().toString().trim();

                // 姓名非空验证
                if (TextUtils.isEmpty(nameT)) {
                    Toast.makeText(AddActivity.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 电话非空验证
                if (TextUtils.isEmpty(phoneT)) {
                    Toast.makeText(AddActivity.this, "电话号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 性别化处理
                if ("男人".equals(sexT)) {
                    sexT = "男";
                } else if ("女人".equals(sexT)) {
                    sexT = "女";
                } else if (!"男".equals(sexT) && !"女".equals(sexT)) {
                    sexT = "男"; // 其他情况默认设为男
                }

                // 执行数据保存
                boolean success = PeoDao.addPeo(nameT, phoneT, sexT, remarkT);
                if (success) {
                    Toast.makeText(AddActivity.this, "添加成功", Toast.LENGTH_SHORT).show();

                    // 点击跳转详情页
                    Intent intent = new Intent(AddActivity.this, MainActivity.class);
                    startActivity(intent); // 跳转
                    finish(); // 关闭当前页面，防止返回时重复操作
                } else {
                    Toast.makeText(AddActivity.this, "添加失败，请重试", Toast.LENGTH_SHORT).show();
                    // 重新加载
                    @SuppressLint("UnsafeIntentLaunch")
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }
            }
        });

        // =========== 取消修改 ===========
        Button backButton = findViewById(R.id.btn_cancel_add);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(AddActivity.this, MainActivity.class);
                startActivity(backIntent);
                finish();
            }
        });
    }
}