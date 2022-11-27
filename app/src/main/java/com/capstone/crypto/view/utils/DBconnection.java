package com.capstone.crypto.view.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBconnection {
    DBHelper helper;
    SQLiteDatabase db;

    public DBconnection(Context context) {
        this.helper = new DBHelper(context, "newdb.db", null, 1);
        this.db = helper.getWritableDatabase();
    }

    public DBHelper getHelper() {
        return helper;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
