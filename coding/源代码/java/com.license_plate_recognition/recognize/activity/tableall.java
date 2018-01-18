package com.license_plate_recognition.recognize.activity;

import cn.bmob.v3.BmobObject;

/**
 * Created by 78056 on 2018/1/16.
 */

public class tableall extends BmobObject {
    private String user1_id;
    private String location;
    private String user2_id;
    private int in_out;
    private String price;
    private String cost;
    private String time;

    public String getUser1_id() {
        return user1_id;
    }
    public void setUser1_id(String user1_id) {
        this.user1_id = user1_id;
    }
    public String getLocation(){return  location;}

    public void setLocation(String location) {
        this.location = location;
    }
    public String getUser2_id(){
        return user2_id;
    }

    public void setUser2_id(String user2_id) {
        this.user2_id = user2_id;
    }

    public int getIn_out() {
        return in_out;
    }

    public void setIn_out(int in_out) {
        this.in_out = in_out;
    }
    public String getPrice(){
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String getCost(){
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }
    public String getTime(){
        return  time;
    }
    public void setTime(String time){
        this.time=time;
    }




}
