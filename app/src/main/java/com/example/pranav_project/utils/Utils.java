package com.example.pranav_project.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.pranav_project.R;

import java.text.SimpleDateFormat;
import java.time.OffsetTime;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class Utils {
    public static final int ENCRYPT_KEY=1472;

  public static AlertDialog getAlertDialog(Activity activity , String mesg){

    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
    View view = LayoutInflater.from(activity).inflate(R.layout.alertdialog , null, false);
    TextView tv = view.findViewById(R.id.id_alert_mesg);

    tv.setText(mesg);
    builder.setView(view);
    AlertDialog dialog = builder.create();
    return dialog;
  }

  public static void toast(Activity activity , String mesg){
    Toast.makeText(activity, " "+ mesg, Toast.LENGTH_SHORT).show();
  }


  public static String getDateAndTime(){
    SimpleDateFormat date = new SimpleDateFormat("dd-MMMM-yyyy");
    String apnaTime = date.format(Calendar.getInstance().getTime());
    OffsetTime time = OffsetTime.now();
    return apnaTime+"-"+time.getHour()+"-"+time.getMinute()+"-"+time.getSecond();
  }

  public static String getDate(){
    SimpleDateFormat date =new SimpleDateFormat("dd-MMMM-yyyy");
    return date.format(Calendar.getInstance().getTime()).toString();
  }

  public static String getTime(){
    OffsetTime time = OffsetTime.now();
    String x = time.getHour()+"-"+time.getMinute()+"-"+time.getSecond()+"";
    return x;
  }

  public static String getEncryptMessage(String msg){
      char []ch = msg.toCharArray();
      char []ch2 = new char[ch.length];
      int intArr[] = new int[ch.length];
      for(int i = 0 ;i<ch.length ; i++)
           ch2[i] = (char) ( (int) ch[i] +  1472 )   ;

      return new String(ch2);
  }

  public static String getDecryptedMessage(String msg)
  {
      if(msg.length()==0)   return "";
      char []ch = msg.toCharArray();
      char []ch2 = new char[ch.length];
      for(int i = 0 ;i<ch.length ; i++)
          ch2[i] = (char) ( (int) ch[i] -  1472 )   ;
      return new String(ch2);
  }

  public static int getSum(LinkedList<Integer> list)
  {
      int sum = 0;
      for(int x : list)
          sum+=x;

      return sum;
  }

  public static long getTimeStamp()
  {
      long startTime = System.currentTimeMillis();
      startTime=startTime/1000;
      return startTime;
  }
}
