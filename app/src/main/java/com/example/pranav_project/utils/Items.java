package com.example.pranav_project.utils;

import java.util.HashMap;
import java.util.Map;

public class Items {

   public   Map< String ,Integer > itemCost = new HashMap<>();

    public Items() {

        itemCost.put("Chicken" , 120);
        itemCost.put("Mutton" , 250);
        itemCost.put("Chicken Kabab" ,180);
        itemCost.put("Fry Fish" , 130);
        itemCost.put("Biriyani" ,150);
    }

    public  String[]foodItems = {"Chicken" , "Mutton" , "Chicken Kabab" , "Fry Fish" , "Biriyani"};

    public int getPriceOfItem(int noOfItem , String itemName){
        int sum = itemCost.get(itemName.trim())*noOfItem;
        return sum;
    }
}
