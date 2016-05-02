package com.imf.picdia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by Pika on 2015/10/3.
 */
public class DBAdapter extends ArrayAdapter<DBcontact> {

    private int resource;
    private List<DBcontact> dBcontacts;


    public DBAdapter(Context context, int resource, List<DBcontact> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.dBcontacts = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout recordView;

        final DBcontact dBcontact = getItem(position);

        if (convertView == null) {
            recordView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getContext().getSystemService(inflater);
            li.inflate(resource, recordView, true);
        } else {
            recordView = (LinearLayout) convertView;
        }


        String img_path=getSdcardPath()+"/PicDia/photos/"+dBcontact.getSid()+".jpg";

        //  改這邊從Drawable拿


        ImageView item_p = (ImageView) recordView.findViewById(R.id.item_photo);
        TextView item_pname = (TextView) recordView.findViewById(R.id.item_name);

        item_pname.setText(dBcontact.getPname());



        BitmapFactory.Options options;
        options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bmp = BitmapFactory.decodeFile(img_path, options);
        item_p.setImageBitmap(bmp);
        /*
        CommandView.setText(dBcontact.get_Command());
        TimeView.setText(dBcontact.get_Time());
        */
            return recordView;
    }

    public DBcontact get(int index){
        return dBcontacts.get(index);
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
