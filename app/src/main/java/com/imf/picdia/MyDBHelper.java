package com.imf.picdia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by IMF on 2015/11/21.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    //資料庫名稱
    private static final String DATABASE_NAME = "pic.db";
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "photos";
    // 資料庫物件，固定的欄位變數
    private static SQLiteDatabase database;




    public MyDBHelper(Context context) {
        //透過建構子MyDBHelper直接呼叫父類別建構子來建立參數的資料庫
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbDAO.CREATE_TABLE);
        Log.e("MyDBHelper","db setup");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // 刪除原有的表格再建一個新的來update資料
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS");
        //把最新版本的SQLiteDB丟進onCreate產生一個新的資料庫來取代掉舊的
        onCreate(sqLiteDatabase);
    }
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, DATABASE_VERSION).getWritableDatabase();
        }

        return database;
    }
}
