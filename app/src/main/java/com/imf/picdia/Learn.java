package com.imf.picdia;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class Learn extends AppCompatActivity {



    private static final String TAG="Learn.java";
    private DBcontact dbcontact;
    //DB
    private DbDAO dbDAO;
    Long nowDBid;


    /*///////////////////////////////////////////
    // ListView使用的自定Adapter物件
    private DBAdapter dbAdapter;
    //如果把這個class import近來，會有很多東西要改很麻煩，先暫時放著，要用到的時候再處理


    */
    // 儲存所有記事本的List物件

    private List<DBcontact> records;
    // 選單項目物件
    private ListView list_records;
    private LinearLayout DBLayout;
    private static final int NOTI_ID =100;

    MyDBHelper mDBHelper = new MyDBHelper(this);
    SQLiteDatabase db = null;


    String str="";
    String s_append;
    private Intent intent;
    private Bundle bundle;
    //String webip 的取得形式，換成R.string全域調用的寫法試試
    String webip;
    String lastid,photodir,readData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        webip=this.getString(R.string.webip);
        //直接從 R.string抓資料，這樣bundle可以少抓一個值


        WebView wv= (WebView)findViewById(R.id.WVhtml);



        intent = this.getIntent();
        bundle = intent.getExtras();
        //webip=bundle.getString("webip");
        //lastid=bundle.getString("lastid");
        photodir=bundle.getString("photodir");

        //________________________DB______________________________


        dbDAO = new DbDAO(getApplicationContext());


        //dbDAO.sample();
        //test/////////////////////////////////////////
        //int intbuf=dbDAO.insertRecord(123,"456");
        //Log.e(TAG,"returnID:"+intbuf);







        //insert 不行，換成用update操作試試
        //DBcontact RS1= new DBcontact(intbuf,111,"abcd");
        //boolean dbupdatereturn;
        //dbupdatereturn=dbDAO.update(RS1);
        //if(!dbupdatereturn){Log.e(TAG,"update fail");}
        //else{Log.e(TAG,"update success");}


        records = dbDAO.getAll();  //把所有的資料都丟給 private List<DBcontact> records;
        //用dbDAO來取得資料庫並做操作



        //records.add(RS1);


        str="<head><title>LearnRecord</title></head><body>" +
                "<table border=\"1\"><tr><th>id</th><th>server_id</th><th>name</th></tr>";
        for (DBcontact record : records) {
            s_append="<tr><th>"+record.getId()+"</th><th>"+record.getSid()+"</th><th>"+record.getPname()+"</th></tr>";
            str+=s_append;//開個for迴圈，把一行一行的資料寫成HTML<table>的格式
            Log.e(TAG, "ID in DB : " + record.getId()+"_"+record.getSid()+"_"+record.getPname());
        }
        str+="</table></body></html>";














        /*
        try {
            FileReader fr = new FileReader(photodir+"/output.txt");
            BufferedReader br = new BufferedReader(fr);

            String temp = br.readLine(); //readLine()讀取一整行
            while (temp != null) {
                readData += temp;
                temp = br.readLine();
            }

        }catch (IOException e) {
            e.printStackTrace();
        }
        */









        wv.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);



        /*   把已經準備好的網頁內容顯示出來
        //後來想想，覺得直接寫死成HTML的缺點是缺乏彈性，比較好的做法是紀錄metadata再及時的依照使用者的喜好設定生成HTMLcode

        //每一筆資料的架構:
        //<tr><td>【img a href...id】</td><td>【NAME CH】</td><td>【NAME EN】</td><td>【photoTIME】</td></tr>

        try {
            FileReader fr = new FileReader(photodir+"/output.txt");
            BufferedReader br = new BufferedReader(fr);

            String temp = br.readLine(); //readLine()讀取一整行
            while (temp != null) {
                readData += temp;
                temp = br.readLine();
            }

        }catch (IOException e) {
            e.printStackTrace();
        }

        str=readData+"</table></body></html>";

        ##注意，如果要方便append，我在顯示的時候要自己加上HTML table 的收尾 tag >>已加


        wv.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);



         */





    }


    public String generateHTMLcode(){  //metadata 的寫法






    return str;
    }


}
