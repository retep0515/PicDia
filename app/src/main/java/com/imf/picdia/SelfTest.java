package com.imf.picdia;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfTest extends AppCompatActivity {


    private static final String TAG="SelfTest.java";

    private ImageView img,result;
    private TextView titletext,scoreboard;
    private Button btn_A,btn_B,btn_C,btn_D;
    private ImageButton btn_back;
    private boolean answer =false;
    private String optionA,optionB,optionC,optionD;
    int now=0;
    int score=0;
    int correctCode=1;  //正確答案順序ACDAB


    ///讀檔名稱全部待改

    //讀檔名稱
    //private String img_path="/sdcard/pixiv/pixiv55975604.png";
    private static final String ACTIVITY_TAG = "SelfTest";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_test);
        img = (ImageView)findViewById(R.id.imageView);
        titletext = (TextView)findViewById(R.id.titleText);
        btn_A = (Button)findViewById(R.id.buttonA);
        btn_B = (Button)findViewById(R.id.buttonB);
        btn_C = (Button)findViewById(R.id.buttonC);
        btn_D = (Button)findViewById(R.id.buttonD);
        result= (ImageView)findViewById(R.id.resultview);
        btn_back= (ImageButton) findViewById(R.id.botton_back);
        btn_back.setVisibility(View.INVISIBLE);
        scoreboard= (TextView)findViewById(R.id.textView_board);

        result.setVisibility(View.GONE);


        //讀入照片
        //setQphoto(img_path);
        Drawable mydraw =getResources().getDrawable(R.drawable.selftest1);
        img.setImageDrawable(mydraw);

        //Option
        optionA= "Apple";
        optionB= "Camera";
        optionC= "Orange";
        optionD= "Banana";
        btn_A.setText(optionA);
        btn_B.setText(optionB);
        btn_C.setText(optionC);
        btn_D.setText(optionD);
        btn_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(1);
                switch2result();

                changeoption();
            }
        });
        btn_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(2);
                switch2result();

                changeoption();
            }
        });
        btn_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(3);
                switch2result();

                changeoption();
            }
        });
        btn_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(4);
                switch2result();

                changeoption();
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back =new Intent();
                back.setClass(SelfTest.this,Learn.class);
                startActivity(back);
                finish();
            }
        });



    }
    private void switch2result() {
        result.setVisibility(View.VISIBLE);
        now++;
        if (answer) {
            result.setImageResource(R.drawable.tick);
            MediaPlayer mpc =MediaPlayer.create(getApplicationContext(),R.raw.correct_sound);
            mpc.start();
            score++;

        } else{
            result.setImageResource(R.drawable.wrong);
            MediaPlayer mpw =MediaPlayer.create(getApplicationContext(),R.raw.wrong_sound);
            mpw.start();
        }
        scoreboard.setText("Score: "+ score+"/5");
        answer=false;  //reset answer
    }

    private boolean checkAnswer(int selected){
        if (correctCode==selected)
            return true;
        else
            return false;
    }
//    private void setQphoto(String path){
//        //Bitmap bitmap = BitmapFactory.decodeFile(path);
//        Log.d(ACTIVITY_TAG,now+ "  bitmap set");
//        //img.setImageBitmap(bitmap);
//        img=(ImageView)findViewById(R.id.imageView);
//        Drawable myDraw = getResources().getDrawable("R.drawable"+path);
//    }

    private void changeoption(){
        switch (now){
            case 1:
                setOptionText("Desk","Pencil","Chair","Lamb");
                Drawable mydraw2 =getResources().getDrawable(R.drawable.selftest2);
                img.setImageDrawable(mydraw2);
                correctCode=3;  //選項C為真

                break;
            case 2:
                setOptionText("knife","broom","fridge","umbrella");
                correctCode=4;  //選項D為真
                Drawable mydraw3 =getResources().getDrawable(R.drawable.selftest3);
                img.setImageDrawable(mydraw3);
                break;
            case 3:
                setOptionText("hotdog","soup","noodle","rice");
                correctCode=1;  //選項A為真
                Drawable mydraw4 =getResources().getDrawable(R.drawable.selftest4);
                img.setImageDrawable(mydraw4);
                break;
            case 4:
                setOptionText("Cat","Dog","Bird","Worm");
                correctCode=2;  //選項B為真
                Drawable mydraw5 =getResources().getDrawable(R.drawable.selftest5);
                img.setImageDrawable(mydraw5);
                break;
            case 5:
                btn_A.setVisibility(View.INVISIBLE);
                btn_B.setVisibility(View.INVISIBLE);
                btn_C.setVisibility(View.INVISIBLE);
                btn_D.setVisibility(View.INVISIBLE);
                titletext.setText("測驗已完成!!! 右上角為您本次測驗的分數");
                result.setVisibility(View.INVISIBLE);
                btn_back.setVisibility(View.VISIBLE);
                img.setVisibility(View.INVISIBLE);
                break;

        }

    }
    private void setOptionText(String A,String B,String C,String D){
        optionA= A;
        optionB= B;
        optionC= C;
        optionD= D;
        btn_A.setText(optionA);
        btn_B.setText(optionB);
        btn_C.setText(optionC);
        btn_D.setText(optionD);
    }
}

