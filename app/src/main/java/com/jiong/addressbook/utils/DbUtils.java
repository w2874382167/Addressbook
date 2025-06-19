package com.jiong.addressbook.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Stack;

public class DbUtils extends SQLiteOpenHelper {

    private  static final String DB_NAME = "db.addressBook.db"; // 数据库名字

    private static final int VERSION = 7; // 数据库版本

    public static SQLiteDatabase db  = null; // 用来操作数据库

    public DbUtils(Context context) {
        // 创建数据库
        super(context, DB_NAME, null, VERSION, null);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 检查表是否存在，如果不存在则创建
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='d_peo'", null);
        if (cursor.getCount() == 0) {
            // 表不存在，创建表
            db.execSQL("CREATE TABLE " +
                    "d_peo(s_id INTEGER primary key AUTOINCREMENT," +
                    "s_name varchar(20)," +
                    "s_phone varchar(20)," +
                    "s_sex varchar(20)," +
                    "s_remark varchar(20))"
            );
        }
        cursor.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }
}
