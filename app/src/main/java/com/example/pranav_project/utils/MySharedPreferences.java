package com.example.pranav_project.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    public static MySharedPreferences mInstanse;
    public static SharedPreferences mPref;
    public static Context context;
    private static String PREF_NAME = "";

    public MySharedPreferences(){
        mPref = context.getSharedPreferences(PREF_NAME , Context.MODE_PRIVATE);
    }

    public static MySharedPreferences getInstance(Context cont){
        context = cont;
        if(mPref==null)
            mInstanse = new MySharedPreferences();
        return mInstanse;
    }

    public void setLogin(String value){
        mPref.edit().putString("login" , value).apply();
    }
    public String getLogin(){
        return mPref.getString("login" ,"");
    }

    public void setUserData(String key , String value){
        mPref.edit().putString(key, value).apply();
    }
    public String getUserData(String key) {
      return  mPref.getString(key,"");
    }


}
