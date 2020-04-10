package com.example.pranav_project.pojo;

import com.example.pranav_project.utils.MyConstants;

import java.util.Map;

public class Pojo_Order_Fetching {
    String  Name,date,time ,
    user_id ;
    Map<String,Object>items;
    long timestamp;

    public String getName() {
        return Name;
    }

    public String getDate() {
        return date;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTime() {
        return time;
    }


    public String getUser_id() {
        return user_id;
    }

    public Map<String, Object> getItems() {
        return items;
    }

}
