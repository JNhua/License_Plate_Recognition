package com.license_plate_recognition.recognize.model;

import cn.bmob.v3.BmobUser;


public class User extends BmobUser {
    private static final long serialVersionUID = 1L;
    private String school="";
    private int price=0;

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public int getPrice(){return price;}

    public void setPrice(int price){this.price=price;}

}
