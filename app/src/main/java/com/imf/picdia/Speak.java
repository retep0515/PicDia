package com.imf.picdia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class Speak extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speak);
        ImageView NR=(ImageView)findViewById(R.id.speak_nr);
        Button BB=(Button)findViewById(R.id.speak_back);
        NR.setImageResource(R.drawable.not_ready);

        BB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




    }
}
