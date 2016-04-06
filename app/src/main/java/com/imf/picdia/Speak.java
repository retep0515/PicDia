package com.imf.picdia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

public class Speak extends AppCompatActivity {

    private static final String TAG="Speak.java";
    private ImageButton startSpeech;
    private TextView showWord, showCorrect, showStep, showOriginal;
    private Button backButton;
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
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                RecoverButton();
            }
        });


        startSpeech = (ImageButton) findViewById(R.id.start_speech);
        startSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(Speak.this, MainService.class);
                intent.setPackage(getPackageName());
                startService(intent);
                String classname;//從聖涵那裡抓單字下來
                classname = "dog";
                //classname = getString(R.string.dog);
                showOriginal.setText(classname);
                showWord.setText("");
                showCorrect.setText("");
                showStep.setText("請說話");
                startSpeech.setEnabled(false);
                startSpeech.setVisibility(View.GONE);
                Log.d(TAG, "jump2MainSer");
            }
        });

    }
    /*
        private ImageView Image;
        private void findViews() {
            Image = (ImageView) findViewById(R.id.imageView);
        }
    */
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent

            String sttresult = intent.getStringExtra("result");
            Log.d("receiver", "Got message: " + sttresult);
            Toast.makeText(Speak.this, sttresult, Toast.LENGTH_SHORT).show();
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
        if (sttresult.equals("dog"))
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
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("my-event"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
