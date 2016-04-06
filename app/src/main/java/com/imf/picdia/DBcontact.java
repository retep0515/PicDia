package com.imf.picdia;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pika on 2015/10/3.
 */
public class DBcontact {
    long _id;
    int _sid;
    String _pname;


    public DBcontact(){
        //do nothing
    }
    public DBcontact(long id,int sid,String pname){
        this._id =id;
        this._sid=sid;
        this._pname=pname;

    }


    public long getId(){
        return  _id ;
    }
    public void SetId(long id){
        this._id=id;
    }



    public int getSid(){
        return  _sid ;
    }
    public void SetSid(int sid){
        this._sid=sid;
    }


    public String getPname(){
        return  _pname ;
    }
    public void SetPname(String pname){
        this._pname=pname;
    }

}
