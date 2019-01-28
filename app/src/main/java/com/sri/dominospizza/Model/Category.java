package com.sri.dominospizza.Model;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public class Category {
    private String Name;
    private String Image;
    private String Phone;

    public Category() {
    }

    public Category(String name, String image, String phone) {
        Name = name;
        Image = image;
        Phone = phone;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}
