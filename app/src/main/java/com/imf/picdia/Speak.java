package com.imf.picdia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

public class Speak extends AppCompatActivity {

    private static final String TAG="Speak.java";
    private ImageButton startSpeech;
    private TextView showWord, showCorrect, showStep, showOriginal;
    private Button backButton;
    private ImageView SpeakPhoto;
    String photosid;
    String classname;//從聖涵那裡抓單字下來 //classname = "dog";
    boolean gotAnswer;


    //使用下面這兩個東西來跟SQLite拿拍照記錄的資料
    private  DbDAO dbDAO;
    private List<DBcontact> records;



    String question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);
        showWord = (TextView)findViewById(R.id.textView); //顯示使用者念出來ㄉ字
        showCorrect = (TextView)findViewById(R.id.textView2); //念對與否
        showStep = (TextView)findViewById(R.id.textView3);
        showOriginal = (TextView)findViewById(R.id.textView4); //辨識出來圖片的字
        backButton =(Button)findViewById(R.id.button);
        SpeakPhoto= (ImageView)findViewById(R.id.photo2speak);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                //back to parent activity
            }
        });

        gotAnswer=false;


        dbDAO = new DbDAO(getApplicationContext());
        records = dbDAO.getAll(); //這樣的方式有點冗，之後再改成只要隨機查詢一筆資料即可

        for (DBcontact record : records) {
            photosid=""+record.getSid();
            classname=record.getPname();
            break;
            //這只是測試功能正常的寫法，只拿出第一筆照片紀錄來顯示，之後要改成隨機挑選
        }


        Log.e(photosid,classname);

        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeFile(getSdcardPath()+"/PicDia/photos/"+photosid+".jpg", options);
        //Bitmap bmp = BitmapFactory.decodeFile(getSdcardPath()+"/PicDia+/now2.jpg", options);
        SpeakPhoto.setImageBitmap(bmp);






        startSpeech = (ImageButton) findViewById(R.id.start_speech);
        startSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Speak.this, MainService.class);
                intent.setPackage(getPackageName());
                startService(intent);

                //classname = getString(R.string.dog);
                showOriginal.setText(classname);
                showWord.setText("");
                showCorrect.setText("");
                showStep.setText("請說話");
                startSpeech.setEnabled(false);
                startSpeech.setVisibility(View.GONE);
                Log.d(TAG, "jump2MainService");
            }
        });

    }





    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent

            String sttresult = intent.getStringExtra("result");
            Log.d("receiver", "Got message: " + sttresult);
            Toast.makeText(Speak.this, sttresult, Toast.LENGTH_SHORT).show();
            gotAnswer=true;
            showResult(sttresult);
        }
    };
    public void RecoverButton(){
        Intent intent = new Intent();
        intent.setClass(Speak.this, MainService.class);
        intent.setAction("com.imf.picdia.MainService");
        intent.setPackage(getPackageName());
        stopService(intent);
        startSpeech.setEnabled(true);
        startSpeech.setVisibility(View.VISIBLE);
    }
    public void showResult(String sttresult){
        showWord.setText(sttresult);
        //showOriginal.setText(classname);
        if (sttresult.equals(classname))
            showCorrect.setText("Correct");
        else
        {
            showCorrect.setText("Wrong");
            showStep.setText("點麥克風再試一次");
        }
        //Toast.makeText(MainActivity.this,sttresult,Toast.LENGTH_SHORT).show();
        RecoverButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    protected void onPause() {
        super.onPause();

        if (MainService.getServiceIsOn()) {

            Intent intent = new Intent();
            intent.setClass(Speak.this, MainService.class);
//                intent.setAction("nctu.imf.sirenplayer.MainService");
            intent.setPackage(getPackageName());
            stopService(intent);
            startSpeech.setEnabled(true);
            startSpeech.setVisibility(View.VISIBLE);
        }
    }

    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //debug//////////////////////////////////////////////////
        if(gotAnswer) {
            Log.e("Speak", "Run to OnResume");
        }
        ////////////////////////////////////////////////////////

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("my-event"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

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
}
