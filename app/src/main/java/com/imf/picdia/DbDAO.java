package com.imf.picdia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IMF-H-A on 2015/10/3.
 */

public class DbDAO {

    private static final String TAG="DBDAO";
    public static final String TABLE_NAME = "record";
    public static final String KEY_ID = "_id";
    //public static final String COMMAND = "command";
    //public static final String TIME = "time";
    //public static final String LAT = "lat";
    //public static final String LNG = "lng";
    public static final String SID="sid";
    public static final String PNAME="pname";
    public DBcontact dBcontact=new DBcontact();

    public static  final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    SID + " INTEGER, "+
                    PNAME + " TEXT )";

    private static SQLiteDatabase db;


    public DbDAO(Context context)
    {
        db = MyDBHelper.getDatabase(context);

    }

    public  void close(){
        db.close();
    }




    public int insertRecord(int sid,String pname){
        ContentValues cv = new ContentValues();
        cv.put(SID,sid);
        cv.put(PNAME,pname);
        //經過測試把下面那行的null那格參數用pname硬塞進去好像沒用耶，改成空字串也沒用
        long id = db.insert(TABLE_NAME,null,cv);
        int reint=(int)id;

        return reint;
    }

    public  DBcontact insert(DBcontact dBcontact){

        ContentValues cv = new ContentValues();
        ////////////////////////////////////////把KEY_ID 註解掉，讓SQLite自動幫我生成試試看
        //com.imf.picdia E/SQLiteDatabase: Error inserting pname=11 sid=1 _id=0
        //android.database.sqlite.SQLiteConstraintException:
        //PRIMARY KEY must be unique (code 19)

        //YA!! 下面那一行註解掉就可以insert成功，不會有錯誤訊息了
        //cv.put(KEY_ID,dBcontact.getId());
        cv.put(SID,dBcontact.getSid());
        cv.put(PNAME,dBcontact.getPname());
         long id = db.insert(TABLE_NAME, null, cv);
         dBcontact.SetId(id);
         return dBcontact;
    }


    public boolean update(DBcontact dBcontact) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();
        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(KEY_ID,dBcontact.getId());
        cv.put(SID,dBcontact.getSid());
        cv.put(PNAME,dBcontact.getPname());
        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + dBcontact.getId();
        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;
   }


    // 刪除參數指定編號的資料
    public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }
    public void clean(){
        db.delete(TABLE_NAME, null, null);
    }

    // 讀取所有DBcontact資料
    public List<DBcontact> getAll() {
        List<DBcontact> result = new ArrayList<>();
        DBcontact dbc;
        Cursor cursor = db.query(
                TABLE_NAME, null,null,null,null,null,null);
        while (cursor.moveToNext()) {  //如果還有下一筆資料就把cursor往後移一格，直到全部讀完為止

            dbc=new DBcontact(cursor.getLong(0),cursor.getInt(1),cursor.getString(2));
            result.add(dbc);
            //result.add(getRecord(cursor));
            //debug///////////////////////////////////////////////////////////////////////////////////////////////
            //終於找到問題了，原來是這邊的List<DBcontact>沒有順利接到資料啊


            //String logtodebug=cursor.getLong(0)+"_"+cursor.getInt(1)+"_"+cursor.getString(2);
            //Log.e("cursor debug :",logtodebug);



            /////////////////////////////////////////////////////////////////////////////////////////////////////


        }
        cursor.close();
        return result;
    }


    public DBcontact getRecord(Cursor cursor) {

        DBcontact result = new DBcontact();

        result.SetId(cursor.getLong(0));

        return result;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }





    public void sample(){

        DBcontact RS1= new DBcontact(0,1,"Chair");
        DBcontact RS2= new DBcontact(0,2,"Apple");
        DBcontact RS3= new DBcontact(0,3,"Hot dog");
        DBcontact RS4= new DBcontact(0,4,"umbrella");
        DBcontact RS5= new DBcontact(0,5,"dog");

        insert(RS1);
        insert(RS2);
        insert(RS3);
        insert(RS4);
        insert(RS5);
        //insert(RS4);
    }

}
