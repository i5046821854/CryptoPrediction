package com.capstone.crypto.view.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("zz");
        String sql = "DROP TABLE if exists USERS";
        db.execSQL(sql);

        sql = "CREATE TABLE if not exists USERS ("
                + "user_seq integer primary key autoincrement,"
                + "username text,"
                + "password text,"
                + "nickname text,"
                + "image integer,"
                + "preference integer);";
        db.execSQL(sql);

        sql = "INSERT INTO USERS (username, password, nickname, image, preference) VALUES ('leeyoungshin', '123','lee', 1, 1)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists USERS";
        db.execSQL(sql);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE if exists USERS";
        db.execSQL(sql);
        onCreate(db);
    }
}
