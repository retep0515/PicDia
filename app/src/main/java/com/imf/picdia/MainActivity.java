package com.imf.picdia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {





    //_________________________________________________________________________



    private static final String TAG="MainActivity";

    int ask4txtio=0;//0:do nothing 1:read data  2:append new record
    boolean sdcardready,photoready,serverready;//確認SD卡該有的資料夾目錄都有了
    int lastserverRespondcode=-1;
    String webip;
    //String webip="http://192.168.0.100/photo/";  //小白盒wifi
    //String webip="http://140.113.2.218/~p0213453/AndroidDB/";  //學校計中的server




    String photodir=getSdcardPath()+"/PicDia";
    String lastid="last";
    String answer="";
    String score=""; //先宣告成字串形式比較好測試，之後考慮改成int

    /*  目前要解決的問題:
    選擇語言的功能後來發現好像沒有必要，就讓他中英文都講就可以了
    TTS設定之後考慮存在SQLite裡面，這樣會比存取文字檔還要快很多
    0.讓TTS講中文
    1.把layout邊邊會被切掉的問題解決
    2.把photoUpLoad的code 複製&轉換進來 >> 若有必要，要再多做一個activity顯示照片及辨識結果
    3.在手機端SD卡裡面建一個"PicDia資料夾"，/img 存放照片及log.txt  >>for 學習歷程檔案的紀錄
    4.結果顯示還需要再另外開一個activity，然後要有重播&返回主畫面&再拍一張的按鈕

    #因為我會從主畫面呼叫很多很多不同種的activity，所以必須要在onActivityResult的地方
    對於requestCode做判斷，才知道是從哪一個activity返回的，才知道現在該做些什麼事
    預計就照順序 拍照(返回)0 口說練習 1 自我測驗 2 學習總覽 3 功能設定 4
    先把switch的各個case區塊寫好吧

    程式第一次開時執行時會跑的幾個thread:
    startCheck >>檢查SD卡的資料夾目錄是否已建立，並檢查server端是否已開啟且standby
    txtio >>持續的讀寫紀錄(在每次拍完照得到辨識結果後都會更新紀錄)


    檔案說明:
    answer.java >> 每次拍照顯示辨識結果  //等結果回來的那段時間，要開個thread顯示"辨識中..."，不要是全黑畫面
    speech >> 口說練習
    learn >> 學習歷程檔案   // HTML + webView 解決
    set >> 功能設定




    */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webip=this.getString(R.string.webip);

        ImageButton pic=(ImageButton) findViewById(R.id.photo);
        ImageButton learn=(ImageButton) findViewById(R.id.learn);
        ImageButton selfTest=(ImageButton) findViewById(R.id.selfTest);
        ImageButton speak=(ImageButton) findViewById(R.id.speak);
        ImageButton setting=(ImageButton) findViewById(R.id.setting);


        Thread t0 = new Thread(startCheck);
        //Thread t1 = new Thread(txtio);
        t0.start();
        //t1.start();

        pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoready=true;
                //lastid="last";
                answer="";
                score="";
                Intent imgcap = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File f = getFile();
                imgcap.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                startActivityForResult(imgcap,5);


            }
        });

        learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Learn.class);
                Bundle bundle = new Bundle(); //需要塞進bundle裡面的東西都在intent呼叫之前前塞一塞
                bundle.putString("wepip", webip);
                //bundle.putString("lastid",lastid);
                bundle.putString("photodir", photodir);
                //bundle.putString("input", s2);
                intent.putExtras(bundle);
                startActivityForResult(intent,3);

            }
        });


        selfTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,SelfTest.class);
                Bundle bundle = new Bundle();
                //bundle.putString("input", s2);
                intent.putExtras(bundle);
                startActivityForResult(intent,2);

            }
        });


        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Speak.class);
                Bundle bundle = new Bundle();
                //bundle.putString("input", s2);
                intent.putExtras(bundle);
                startActivityForResult(intent,1);

            }
        });


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,Setting.class);
                Bundle bundle = new Bundle();
                //bundle.putString("input", s2);
                intent.putExtras(bundle);
                startActivityForResult(intent,4);

            }
        });

























    }






    private final BroadcastReceiver AsyncTaskForPostFileReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(MainActivity.this, "PostFileComplete", Toast.LENGTH_SHORT).show();
            //開thread 那一整段移到這裡
            // get ? 名稱="........"
            // ###################__________###############_____________
            // TextView set text

        }
    };

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        //預計就照順序 拍照(返回)0 口說練習 1 自我測驗 2 學習總覽 3 功能設定 4
        //先把switch的各個case區塊寫好吧
        Bundle bundle = new Bundle(); //需要塞進bundle裡面的東西都在intent呼叫之前前塞一塞
        bundle.putString("wepip", webip);
        //bundle.putString("lastid",lastid);
        bundle.putString("photodir",photodir);
        Intent intent = new Intent();
        switch (requestCode){



            case 0:  //拍照辨識結果返回
                if(resultCode==1){
                    /////////////////////////////////////////
                    //再拍一張
                    //如果resultCode==0 就什麼也不做
                }

                break;
            case 1:  //口說練習

                break;
            case 2:  //自我測驗

                break;
            case 3:  //學習總覽

                break;
            case 4:  //功能設定  //傳回值用bitwise來看，如果是0表示沒有修改，else  &1 改語速 &2改語調  &4 改serverIP
                //從回傳的intent拿修改資料
                //後來想想覺得這方法不好
                //準備改成使用sharedPreferences來更改設定

                int cc=data.getExtras().getInt("changeCode");
                float fc;String newip;
                if((cc&1)>0){fc=data.getExtras().getFloat("speed");}
                if((cc&2)>0){fc=data.getExtras().getFloat("pitch");}
                if((cc&4)>0){newip=data.getExtras().getString("newip");}
                if((cc&8)>0){}//有想到甚麼再加




                break;
            case 5:  //取得照片，做完處理後交給answer.java 顯示

                //這邊會有個小小的bug 將來有空要記得改
                //要分辨來到這邊是已經成功拍了一張，還是使用者按返回鍵取消不拍了，這兩種case的處理種況不同

                Log.e("MA","Run to here.");

                String path = photodir + "/now.jpg";

                //iv.setImageDrawable(Drawable.createFromPath(path));

                Log.e(TAG, "before sleep &now id is gotton");
                //##################################################################
                //我後來決定把getnowid這個thread 搬到 Answer.java 裡面才做


                //在開始把照片縮小尺寸前，先執行Thread t1 = new Thread(getnowid);  //跟server要id編號
                //Thread tn = new Thread(getnowid);
                //tn.run();
                //////////////////////目前已確定 getnowid 裡面有 bug 待檢查?


                //如果怕server太慢回應，可以先sleep(200);





                double hi, wi, scale;
                int iw, ih;
                BitmapFactory.Options options;
                options = new BitmapFactory.Options();
                options.inSampleSize = 2;

                Bitmap bmp = BitmapFactory.decodeFile(path, options);
                hi = bmp.getHeight();
                wi = bmp.getWidth();
                scale = Math.sqrt(1000000 / (hi * wi));
                iw = (int) (wi * scale);
                ih = (int) (hi * scale);
                //Log.e(hi+" "+wi+" "+ih+" "+iw+" "+scale, "debug");


                //Bitmap b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length)
                Bitmap bmp2 = Bitmap.createScaledBitmap(bmp, iw, ih, false);
                //iv.setImageBitmap(bmp2);

                //iv.setImageBitmap(bmp);



                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(new File(photodir + "/now2.jpg"));
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    //Log.e("goin catch",e.toString());
                    e.printStackTrace();
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                Log.e(TAG,"Ready go into Answer.java");
                intent.setClass(MainActivity.this,Answer.class);


                intent.putExtras(bundle);
                startActivityForResult(intent, 0);








                break;
        }


    }



    private File getFile(){
        File folder= new File(photodir);

        //if(!folder.exists()){folder.mkdir();}
        File img=new File(folder,"now.jpg");
        return img;

    }






    Runnable startCheck = new Runnable() {
        @Override
        public void run() {

            File folder= new File(getSdcardPath()+"/PicDia");
            if(!folder.exists()){ //沒有找到已存在的 PicDia資料夾，就全部重新建立吧
                folder.mkdir();
                folder= new File(getSdcardPath()+"/PicDia/photos");
                folder.mkdir();

                //////////////////////////////////////////////////////////////
                //還有建立學習歷程記錄的文字檔
                try {
                    FileWriter fw = new FileWriter(getSdcardPath()+"/PicDia/learn.txt", false);
                    BufferedWriter bw = new BufferedWriter(fw);
                    bw.write(" ");
                    //bw.write("<head><title>LearnRecord</title></head><body><table>");
                    bw.newLine();
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            ///////////////////////////////////////////////////////////////////
            //接下來檢查server狀態，看看是否可以連上，如果連不上就彈出視窗提醒使用者拍照上傳辨識的功能目前無法使用
            try {

                HttpURLConnection conn = null;

                URL url_address = new URL(webip+"ok.php"); //單純用id=0來查詢，確認server目前是否有開機
                //lastserverRespondcode=conn.getResponseCode();

                // Open a HTTP  connection to  the URL

                conn = (HttpURLConnection) url_address.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US) AppleWebKit/525.19 (KHTML, like Gecko) Chrome/0.3.154.9 Safari/525.19");
                conn.setRequestProperty("Accept-Charset","utf-8");


                InputStreamReader inStream;
                String s,buf;
                s="";

                //inStream = new InputStreamReader(conn.getInputStream(),"UTF-8"); //如果是無效的URL，會在這一行丟出FileNotFoundException
                //BufferedReader br = new BufferedReader(inStream);
                //while((buf=br.readLine())!=null) {s = s + buf;}


                lastserverRespondcode =conn.getResponseCode(); //為何無法偵測 QQ
                if(lastserverRespondcode==200){serverready=true;}
                else{
                    serverready=false;
                    /////////////////////////////////////////////////////////////////////
                    //彈出視窗提醒使用者拍照上傳辨識的功能目前無法使用





                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }










        }
    };


    Runnable txtio = new Runnable() {
        @Override
        public void run() {
            try { //避免startCheck還沒準備好，所以先sleep 5秒
                sleep(5000);


                while (ask4txtio>=0) {
                    switch (ask4txtio){
                        case 0:
                            break;
                        case 1:  //讀檔

                            break;
                        case 2:  //寫檔 (append)

                            break;
                    }

                    sleep(500);//每隔0.5秒再檢查一次有無新的要求
                }





            } catch (InterruptedException e) {
                e.printStackTrace();
            }







        }
    };

    Runnable getnowid = new Runnable() {
        public void run() {

            String now=getHTML(webip+"nowid.php");
            int i,k;


            Log.e("OnGetnowid","after visited nowid.php");

            i=now.indexOf("body");
            k=now.lastIndexOf("body");
            k-=2;
            now=now.substring(i,k);



            //debug

            i=now.indexOf("id");
            i+=3;
            now=now.substring(i,now.length());

            Log.e("substring",now);
            lastid=now;



        }
    };


    public String getSdcardPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir.getPath();  //AVD logcat 指出 ，這行使用的to string 會有問題，要改
        //Caused by: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String java.io.File.toString()' on a null object reference at com.imf.photoupload.MainActivity.getSdcardPath(MainActivity.java:151)
    }

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



}
