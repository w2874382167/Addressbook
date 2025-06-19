package com.jiong.addressbook.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hankcs.hanlp.HanLP;
import com.jiong.addressbook.bean.PeoBean;
import com.jiong.addressbook.utils.DbUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 得到首字母
 * 获取所有的数据
 * 以list的方式存储返回
 */
public class PeoDao {
    public static SQLiteDatabase db = DbUtils.db; // 用来操作数据库

    /**
     * 拿到所有的数据
     *
     * @return 列表（包含所有的数据）
     */
    public static List<PeoBean> getAllpeo() {

        List<PeoBean> list = new ArrayList<>();

        Cursor dataList = db.rawQuery("select * from d_peo", null);
        while (dataList.moveToNext()) {
            PeoBean peoBean = new PeoBean(
                    String.valueOf(dataList.getInt(0)),
                    dataList.getString(1),
                    dataList.getString(2),
                    dataList.getString(3),
                    dataList.getString(4)
            );
            /* 获取第一个首字母 */
            String name = dataList.getString(1);
            String firstStr = name.substring(0, 1); // 提取弟弟一个字符
            boolean res = firstStr.matches("^[a-zA-Z]*");
            if (res) {
                // 是英文存储大写首字母
                peoBean.setBeginZ(firstStr.toUpperCase());
            } else {
                // 中文
                String regEx = "[\\u4e00-\\u9fa5]+";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(firstStr);
                if (m.find()) {
                    // 将中文转化为拼音
                    String pinyin = HanLP.convertToPinyinString(firstStr, " ", false);
                    String Xing = pinyin.substring(0, 1);
                    peoBean.setBeginZ(Xing.toUpperCase()); // 存储汉字的大写首字母
                } else {
                    peoBean.setBeginZ("#"); // 其他东西统一#号
                }

            }

            list.add(peoBean); // 每一条数据放入list
        }
        dataList.close(); // 记得关闭游标
        return list;
    }

    // 删除功能操作dao
    /**
     * 删除信息
     *
     * @param idStr
     * @return
     */
    public static void deletePeo(String idStr) {
        int id = Integer.parseInt(idStr);
        db.execSQL("delete from d_peo where s_id = ?", new Object[]{id});
    }

    // 更新消息获取数据库，不调网络
    public static PeoBean getPeo(String idStr) {
        Cursor res = db.rawQuery("select * from d_peo where s_id = ?", new String[]{idStr});
        PeoBean peoBean = null;
        if (res.moveToNext()) {
            peoBean = new PeoBean(
                    String.valueOf(res.getInt(0)),
                    res.getString(1),
                    res.getString(2),
                    res.getString(3),
                    res.getString(4)
            );
        }
        res.close();
        return peoBean;
    }
//    public static PeoBean getPro(String id){
//        Cursor res = db.rawQuery("select * from d_peo where s_id = ?", new String[]{id});
//        PeoBean peoBean = null;
//        if(res.moveToNext()){
//             peoBean  = new PeoBean(
//                    res.getString(0),
//                    res.getString(1),
//                    res.getString(2),
//                    res.getString(3),
//                    res.getString(4)
//            );
//
//            // 保存当前处理的peoBean引用
//            final PeoBean currentBean = peoBean;
//            // 调用异步方法查询归属地
//            Phone.queryMobileLocation(peoBean.getNum(), new Phone.PhoneCallback() {
//                @Override
//                public void onSuccess(String result) {
//                    // 在成功回调中设置归属地信息
//                    System.out.println("打印成功一个人回调");
//                    System.out.println(result);
//                    currentBean.setnumberLocated(result);
//                }
//
//                @Override
//                public void onFailure(String errorMsg) {
//                    // 处理失败情况
//                    Log.e("PhoneInfo", "获取归属地失败: " + errorMsg);
//
//                    // 可以设置默认值或进行其他处理
//                    currentBean.setnumberLocated("归属地未知");
//                }
//            });
//
//            return currentBean;
//        }
//        return peoBean;
//    }

    // 获取一个人的信息，包括运营商
    public static LiveData<PeoBean> getPeolive(String idStr) {
        MutableLiveData<PeoBean> liveData = new MutableLiveData<>();

        new Thread(() -> {
            Cursor res = db.rawQuery("select * from d_peo where s_id = ?", new String[]{idStr});
            Log.e("detail", String.valueOf(res));
            PeoBean peoBean = null;
            if (res.moveToNext()) {
                peoBean = new PeoBean(
                        String.valueOf(res.getInt(0)),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4)
                );
                Log.e("detailres", String.valueOf(res.getInt(0)));
                Log.e("detail", res.getString(1));
                Log.e("detail", res.getString(2));
                Log.e("detail", res.getString(3));
                Log.e("detail", res.getString(4));
                final PeoBean currentBean = peoBean;

                Phone.queryMobileLocation(peoBean.getNum(), new Phone.PhoneCallback() {
                    @Override
                    public void onSuccess(String result) {
                        currentBean.setnumberLocated(result);
                        liveData.postValue(currentBean);
                    }

                    @Override
                    public void onFailure(String errorMsg) {
                        currentBean.setnumberLocated("归属地未知");
                        liveData.postValue(currentBean);
                    }
                });
            } else {
                liveData.postValue(null);
            }
            res.close();
        }).start();

        return liveData;
    }

    /**
     * 修改一条数据
     *
     * @param peoBean 包含更新信息的PeoBean对象
     * @return 更新是否成功
     */
    public static boolean updatePeo(PeoBean peoBean) {
        ContentValues values = new ContentValues();

        // 设置要更新的字段和值
        values.put("s_name", peoBean.getName());
        values.put("s_phone", peoBean.getNum());
        values.put("s_sex", peoBean.getSex());
        values.put("s_remark", peoBean.getRemark());

        // 定义更新条件（根据ID查找记录）

        String whereClause = "s_id = ?";
        String[] whereArgs = new String[]{peoBean.getId()};

        // 执行更新操作
        int count = db.update(
                "d_peo",         // 表名
                values,          // 要更新的数据
                whereClause,     // 更新条件
                whereArgs        // 条件参数
        );

        // 返回更新是否成功（更新的记录数大于0表示成功）
        return count > 0;
    }

    // 添加人员
    public static boolean addPeo(String name, String phone, String sex, String remark) {
        ContentValues values = new ContentValues();
        values.put("s_name", name);
        values.put("s_phone", phone);
        values.put("s_sex", sex);
        values.put("s_remark", remark);

        long result = db.insert("d_peo", null, values);
        return result != -1;
    }

    // 模糊查找
    public static List<PeoBean> searchPeo(String id) {

        List<PeoBean> list = new ArrayList<>();
        String query = "SELECT * FROM d_peo WHERE s_phone LIKE ? OR s_name LIKE ?";
        String likeId = "%" + id + "%"; // 预处理通配符
        Cursor dataList = db.rawQuery(query, new String[]{likeId, likeId});
        while (dataList.moveToNext()) {
            PeoBean peoBean = new PeoBean(
                    String.valueOf(dataList.getInt(0)),
                    dataList.getString(1),
                    dataList.getString(2),
                    dataList.getString(3),
                    dataList.getString(4)
            );
            /* 获取第一个首字母 */
            String name = dataList.getString(1);
            String firstStr = name.substring(0, 1); // 提取弟弟一个字符
            boolean res = firstStr.matches("^[a-zA-Z]*");
            if (res) {
                // 是英文存储大写首字母
                peoBean.setBeginZ(firstStr.toUpperCase());
            } else {
                // 中文
                String regEx = "[\\u4e00-\\u9fa5]+";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(firstStr);
                if (m.find()) {
                    // 将中文转化为拼音
                    String pinyin = HanLP.convertToPinyinString(firstStr, " ", false);
                    String Xing = pinyin.substring(0, 1);
                    peoBean.setBeginZ(Xing.toUpperCase()); // 存储汉字的大写首字母
                } else {
                    peoBean.setBeginZ("#"); // 其他东西统一#号
                }

            }

            list.add(peoBean); // 每一条数据放入list
        }
        dataList.close();
        return list;
    }
}
