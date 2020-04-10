package com.example.pranav_project.pojo;


import com.example.pranav_project.utils.MyConstants;


public class MyPojo {
    String Food ,
     added_item ,
     date ,
     item_cost ,
     time ,
     total_cost_of_the_item_added ;

    public String getFood() {
        return Food;
    }

    public void setFood(String food) {
        Food = food;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    String timeStamp = MyConstants.TIMESTAMP;

    public String getAdded_item() {
        return added_item;
    }

    public void setAdded_item(String added_item) {
        this.added_item = added_item;
    }

    public String getItem_cost() {
        return item_cost;
    }

    public void setItem_cost(String item_cost) {
        this.item_cost = item_cost;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTotal_cost_of_the_item_added() {
        return total_cost_of_the_item_added;
    }

    public void setTotal_cost_of_the_item_added(String total_cost_of_the_item_added) {
        this.total_cost_of_the_item_added = total_cost_of_the_item_added;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
