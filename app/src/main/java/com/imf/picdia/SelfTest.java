package com.imf.picdia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SelfTest extends AppCompatActivity {


    private static final String TAG="SelfTest.java";

    private ImageView img,result;
    private TextView titletext,cortext,corAns,clitext,cliAns,scoreboard;
    private Button btn_A,btn_B,btn_C,btn_D;
    private boolean answer =false;
    private String optionA,optionB,optionC,optionD;
    int now=0;
    int score=0;
    int correctCode=1;  //正確答案順序ACDAB


    ///讀檔名稱全部待改

    //讀檔名稱
    private String img_path="/sdcard/pixiv/pixiv55975604.png";
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
        cortext= (TextView)findViewById(R.id.textView_Reans);
        corAns= (TextView)findViewById(R.id.textView_CorAns);
        clitext= (TextView)findViewById(R.id.textView_TAns);
        cliAns= (TextView)findViewById(R.id.textView_cliAns);

        scoreboard= (TextView)findViewById(R.id.textView_board);

        result.setVisibility(View.GONE);


        //讀入照片
        setQphoto(img_path);

        //Option
        optionA= "Apple";
        optionB= "Banana";
        optionC= "Cat";
        optionD= "Dog";
        btn_A.setText(optionA);
        btn_B.setText(optionB);
        btn_C.setText(optionC);
        btn_D.setText(optionD);
        btn_A.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(1);
                switch2result();
                cliAns.setText(optionA);
                changeoption();
            }
        });
        btn_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(2);
                switch2result();
                cliAns.setText(optionB);
                changeoption();
            }
        });
        btn_C.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(3);
                switch2result();
                cliAns.setText(optionC);
                changeoption();
            }
        });
        btn_D.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answer = checkAnswer(4);
                switch2result();
                cliAns.setText(optionD);
                changeoption();
            }
        });



    }
    private void switch2result() {
        result.setVisibility(View.VISIBLE);
        corAns.setText(optionA);
        now++;

        if (answer) {
            result.setImageResource(R.drawable.tick);
            score++;

        } else{
            result.setImageResource(R.drawable.wrong);
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
    private void setQphoto(String path){
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.d(ACTIVITY_TAG,now+ "  bitmap set");
        img.setImageBitmap(bitmap);
    }

    private void changeoption(){
        switch (now){
            case 1:
                btn_A.setText("a");
                btn_B.setText("b");
                btn_C.setText("saber");
                btn_D.setText("d");
                img_path="/sdcard/pixiv/pixiv55975068.png";
                correctCode=3;  //選項C為真
                setQphoto(img_path);
                break;
            case 2:
                btn_A.setText("a");
                btn_B.setText("b");
                btn_C.setText("c");
                btn_D.setText("pokemon");
                correctCode=4;  //選項D為真
                img_path="/sdcard/pixiv/pixiv55538971_1.png";
                setQphoto(img_path);
                break;
            case 3:
                btn_A.setText("miku");
                btn_B.setText("b");
                btn_C.setText("c");
                btn_D.setText("d");
                correctCode=1;  //選項A為真
                img_path="/sdcard/pixiv/pixiv55541172.jpg";
                setQphoto(img_path);
                break;
            case 4:
                btn_A.setText("a");
                btn_B.setText("lancer");
                btn_C.setText("c");
                btn_D.setText("d");
                correctCode=2;  //選項B為真
                img_path="/sdcard/pixiv/pixiv54561644.jpg";
                setQphoto(img_path);
                break;
            case 5:
                btn_A.setVisibility(View.INVISIBLE);
                btn_B.setVisibility(View.INVISIBLE);
                btn_C.setVisibility(View.INVISIBLE);
                btn_D.setVisibility(View.INVISIBLE);
                titletext.setText("測驗已完成!!! 右上角為您本次測驗的分數");
                cortext.setVisibility(View.INVISIBLE);
                corAns.setVisibility(View.INVISIBLE);
                cliAns.setVisibility(View.INVISIBLE);
                clitext.setVisibility(View.INVISIBLE);
                result.setVisibility(View.INVISIBLE);
                img_path="";
                setQphoto(img_path);
                break;

        }

    }
}

