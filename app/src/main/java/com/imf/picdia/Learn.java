package com.imf.picdia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Sampler;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Learn extends AppCompatActivity {



    private static final String TAG="Learn.java";

    //DB

    Long nowDBid;

    ListView learnlist;

    private DBcontact dbcontact;
    //DB
    private DbDAO dbDAO;
    private static final String DBTag ="DBActivity";
    // ListView使用的自定Adapter物件
    private DBAdapter dbAdapter;
    // 儲存所有記事本的List物件

    //for TTS
    String answer="";



    /*///////////////////////////////////////////
    // ListView使用的自定Adapter物件
    private DBAdapter dbAdapter;

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
    private Button toSelftest,toIndex;

    //dictionary variable
    String vocabulary="apple";//要查的單字  之後改這邊就好  用intent的方式傳過來即可?
    String url="http://tw.websaru.com/"+vocabulary+".html";
    Button btn_out;
    TextView dict_TV;
    LinearLayout dict_layout;
    String title;//存詞性解釋的
    String sentence="";//存例句的



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        DBactstart();
        webip=this.getString(R.string.webip);
        //直接從 R.string抓資料，這樣bundle可以少抓一個值
        dbcontact= new DBcontact();
        dbDAO=new DbDAO(getApplicationContext());
        Log.i(TAG,"DB Setup");
        if (dbDAO.getCount()==0){
            dbDAO.sample();
        }
        records= dbDAO.getAll();
        dbAdapter = new DBAdapter(this,R.layout.db_item,records);
        list_records=(ListView)findViewById(R.id.photoList);
        list_records.setAdapter(dbAdapter);
        Log.i(TAG, "Get records  " + dbDAO.getCount());
        toSelftest =(Button)findViewById(R.id.selftest);
        //DBLayout=(LinearLayout)findViewById(R.id.photoList);

        //TTs Setup
        tts = new TextToSpeech(Learn.this, ttsInitListener);


        list_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //重念該詞一次
                //讓TTS再念一次
                tts.speak(dbAdapter.get(position).getPname(), TextToSpeech.QUEUE_FLUSH, null);
            }
        });
        list_records.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                vocabulary=dbAdapter.get(position).getPname();
                new Thread(runnable).start();
                dict_layout.setVisibility(View.VISIBLE);
                return false;
            }
        });

        toSelftest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toself=new Intent();
                toself.setClass(Learn.this,SelfTest.class);
                startActivity(toself);
                finish();
            }
        });
        btn_out = (Button)findViewById(R.id.button_out_dict);
        btn_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dict_layout.getVisibility()==View.VISIBLE)
                    dict_layout.setVisibility(View.GONE);
            }
        });
        toIndex =(Button)findViewById(R.id.back_index);
        toIndex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toself=new Intent();
                toself.setClass(Learn.this,MainActivity.class);
                startActivity(toself);
                finish();
            }
        });



        //webip=this.getString(R.string.webip);
        //WebView wv= (WebView)findViewById(R.id.WVhtml);



//        intent = this.getIntent();
//        bundle = intent.getExtras();
//        //webip=bundle.getString("webip");
//        //lastid=bundle.getString("lastid");
//        photodir=bundle.getString("photodir");

        //________________________DB______________________________
        //insert 不行，換成用update操作試試
        //DBcontact RS1= new DBcontact(intbuf,111,"abcd");
        //boolean dbupdatereturn;
        //dbupdatereturn=dbDAO.update(RS1);
        //if(!dbupdatereturn){Log.e(TAG,"update fail");}
        //else{Log.e(TAG,"update success");}


//        dbDAO = new DbDAO(getApplicationContext());
//        records = dbDAO.getAll();  //把所有的資料都丟給 private List<DBcontact> records;
//        //用dbDAO來取得資料庫並做操作

//
//        str="<head><title>LearnRecord</title></head><body>" +
//                "<table border=\"1\"><tr><th>id</th><th>server_id</th><th>name</th></tr>";
//        for (DBcontact record : records) {
//            s_append="<tr><th>"+record.getId()+"</th><th>"+record.getSid()+"</th><th>"+record.getPname()+"</th></tr>";
//            str+=s_append;//開個for迴圈，把一行一行的資料寫成HTML<table>的格式
//            Log.e(TAG, "ID in DB : " + record.getId()+"_"+record.getSid()+"_"+record.getPname());
//        }
//        str+="</table></body></html>";


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


       //wv.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);


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
    //===================================================
    //TTS
    protected TextToSpeech tts; //private 改 protected 試試看
    private TextToSpeech.OnInitListener ttsInitListener = new TextToSpeech.OnInitListener() {
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.US);

// 如果該語言資料不見了或沒有支援則無法使用
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                } else {
// 語調(1為正常語調；0.5比正常語調低一倍；2比正常語調高一倍)
                    tts.setPitch((float) 1.5);
// 速度(1為正常速度；0.5比正常速度慢一倍；2比正常速度快一倍)
                    tts.setSpeechRate((float) 1.0);
// 設定要說的內容文字
                    tts.speak(answer, TextToSpeech.QUEUE_FLUSH, null);
                }
            } else {
                //Toast.makeText(MainActivity.this, "Initialization Failed!",
                //Toast.LENGTH_LONG).show();
            }
        }
    };


    //====================================================================================================
    //dictionary Parser
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            try {
                Document document = Jsoup.connect(url).get();
                Elements div = document.select("div#wrap");
                Element word=div.select("ol").first();
                title="詞性解釋："+word.text()+"\n";
                Elements engsen=div.select("dd");
                Elements chsen=div.select("dt");
                for(int i=0;i<4;i++)//例句為爬4句，要多再調即可R
                {
                    sentence=sentence+"例句："+engsen.get(i).text()+"\n"+"解釋："+chsen.get(i).text()+"\n";
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Dict_handler.sendEmptyMessage(0);
        }
    };

    @SuppressLint("HandlerLeak")
    Handler Dict_handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dict_TV.setText(title + sentence);//最後用一個textview接  然後顯示出來
        }
    };

    public void DBactstart() {
        Log.i(TAG, "DBactivity Start Up");
    }
}
