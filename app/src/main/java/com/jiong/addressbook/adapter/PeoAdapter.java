package com.jiong.addressbook.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.jiong.addressbook.MainActivity;
import com.jiong.addressbook.R;
import com.jiong.addressbook.activity.DetailActivity;
import com.jiong.addressbook.bean.PeoBean;

import java.util.List;

/**
 * 喧嚷数据到列表里面
 * 负责一条数据的处理
 */
public class PeoAdapter extends ArrayAdapter<PeoBean> {

    List<PeoBean> items;
    public PeoAdapter(Context context, List<PeoBean> items) {
        super(context, R.layout.book_view, items); // 和单个界面进行绑定
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null){
            // 格式化
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.book_view, parent, false);
        }
        ImageView  imageView = convertView.findViewById(R.id.tp); // 图片
        TextView name = convertView.findViewById(R.id.name); // 名字
        TextView zm = convertView.findViewById(R.id.zm); //字母

        PeoBean peo = items.get(position);
        name.setText(peo.getName()); // 获取名字
        // 设置图片
        if(peo.getSex().equals("女")){
            // 设置女生
            imageView.setImageResource(R.drawable.wuman);
        }else{
            imageView.setImageResource(R.drawable.man);
        }
        // 首字母相同，一块显示
        // 存进来的数据是经过首字母排序的
        if (position == 0){
            zm.setText(peo.getBeginZ());  // 设置字母
        }else{
            // 上一条数据
            PeoBean temp = items.get(position - 1);
            if(!temp.getBeginZ().equals(peo.getBeginZ())){
                // 本条数据和上一条数据首字母不相同
                zm.setText(peo.getBeginZ()); // 设置字母
            } else{
                // 字母相同，把字母的位置高度销毁
                zm.setHeight(0);
            }
        }

        // === 点击跳转 ===
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击跳转详情页
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra("id", peo.getId());// 传给详情页的数据
                getContext().startActivity(intent); // 跳转
            }
        });

        return convertView;
    }


}
