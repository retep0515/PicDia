package com.imf.picdia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class Answer extends AppCompatActivity {


    //再結果還沒回來之前，要顯示一個等待中的畫面

    private Intent intent;
    private Bundle bundle;
    String s;
    String answer,score,webip,lastid,photodir,toSpeak;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        ImageButton repeat = (ImageButton)findViewById(R.id.repeat);
        ImageButton retry = (ImageButton)findViewById(R.id.retry);
        ImageButton back = (ImageButton)findViewById(R.id.back);
        ImageView photoView = (ImageView)findViewById(R.id.photoView);
        photoView.setImageResource(R.drawable.uploading);
        TextView chtxt= (TextView)findViewById(R.id.CH);
        TextView entxt= (TextView)findViewById(R.id.EN);
        chtxt.setVisibility(TextView.INVISIBLE);//先不顯示中文
        //Button aback = (Button)findViewById(R.id.ab_button); //之後會用imageButtom替換掉
        intent = this.getIntent();
        bundle = intent.getExtras();
        webip=bundle.getString("webip");
        lastid=bundle.getString("lastid");
        photodir=bundle.getString("photodir");

        Thread t0= new Thread(waitAnswer);
        t0.run();



        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //讓TTS再念一次
                tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(1, intent);
                finish();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(0, intent);
                finish();

            }
        });

        while (answer.length() <= 2) {

            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.e("sleep()失敗", "debug");
            }

        }

        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeFile(photodir+"/now2.jpg", options);
        photoView.setImageBitmap(bmp);


        //panswer.setText("辨識結果: " + answer);
        //pscore.setText("照片分數: "+score);

        //接下來要把結果唸出來

        toSpeak = answer;

        tts = new TextToSpeech(Answer.this, ttsInitListener);

        Toast.makeText(getApplicationContext(), toSpeak, Toast.LENGTH_SHORT).show();
        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

    }


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


    public String getHTML(String strURL) {
        String buf="";String s="";
        int now;

        URLConnection conn = null;
        InputStreamReader inStream;

        try {
            URL url_address = new URL(strURL);

            //Log.e("run to here.", "before setRequestProperty");
            conn=url_address.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/0.3.154.9 Safari/525.19");
            conn.setRequestProperty("Accept-Charset", "utf-8");//測試看看加上這一行後資料夾名稱為中文亂碼的問題有沒有解決///////////////////////////////////////////////////




            //下一行有bug
            inStream = new InputStreamReader(conn.getInputStream(),"UTF-8"); //如果是無效的URL，會在這一行丟出FileNotFoundException
            //inStream = new InputStreamReader(conn.getInputStream()); //如果是無效的URL，會在這一行丟出FileNotFoundException

            BufferedReader br = new BufferedReader(inStream);
            while((buf=br.readLine())!=null){
                s=s+buf;
            }




        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Log.e("run to here.","before return");




        return s;
    }


    private final BroadcastReceiver AsyncTaskForPostFileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(Answer.this, "PostFileComplete", Toast.LENGTH_SHORT).show();
            //開thread 那一整段移到這裡
            // get ? 名稱="........"
            // ###################__________###############_____________
            // TextView set text

        }
    };




    Runnable waitAnswer =new Runnable() {
        @Override
        public void run() {
            ///////////////////上傳照片並等待辨識結果
            registerReceiver(AsyncTaskForPostFileReceiver, new IntentFilter("PostFileComplete"));
            AsyncTaskForPostFile PostFile = new AsyncTaskForPostFile(Answer.this);
            PostFile.execute(photodir + "/now2.jpg", webip + "test.php", lastid + ".jpg");

            int i, k;
            String now;// = getHTML(webip+"getResult.php?id=" + lastid);
            now = getHTML(webip + "getResult.php?id=" + lastid);
            i = now.indexOf("body");
            k = now.lastIndexOf("body");
            i += 5;
            k -= 2;
            now = now.substring(i, k);

            k=now.indexOf("##########");

            answer=now.substring(0,k);

            score=now.substring(k+10,now.length());

            Log.e("substring", now+"_"+answer+"_"+score);

            while(answer.length() <= 2){
                try {
                    sleep(1000); //休息1秒鐘之後再問一次
                } catch (InterruptedException e) {
                    Log.e("sleep()失敗","debug");
                    e.printStackTrace();

                }
                now = getHTML(webip+"getResult.php?id=" + lastid);
                i = now.indexOf("body");
                k = now.lastIndexOf("body");
                i += 5;
                k -= 2;
                now = now.substring(i, k);

                k=now.indexOf("##########");

                answer=now.substring(0,k);

                score=now.substring(k+10,now.length());

                Log.e("substring", now+"_"+answer+"_"+score);

            }

            //panswer.setText(answer);
            //只有主activity 有權限改TextView,所以不能寫在這裡

            //這個thread結束前在結尾的地方要呼叫 addRecord 把結果寫進文字檔裡面
            Thread t1=new Thread(addRecord);
            t1.run();

        }


    };



    Runnable addRecord = new Runnable() {
        @Override
        public void run() {
            try {
                FileWriter fw = new FileWriter(photodir+"/learn.txt", true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(answer);
                bw.newLine();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


}
