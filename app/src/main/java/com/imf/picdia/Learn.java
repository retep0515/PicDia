package com.imf.picdia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Learn extends AppCompatActivity {


    String str="";
    private Intent intent;
    private Bundle bundle;

    String webip,lastid,photodir,readData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        WebView wv= (WebView)findViewById(R.id.WVhtml);

        intent = this.getIntent();
        bundle = intent.getExtras();
        webip=bundle.getString("webip");
        lastid=bundle.getString("lastid");
        photodir=bundle.getString("photodir");

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
