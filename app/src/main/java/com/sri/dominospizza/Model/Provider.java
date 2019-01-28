package com.sri.dominospizza.Model;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public class Provider {
    private String phone;
    private String name;
    private String logo;
    private String address;

    public Provider() {
    }

    public Provider(String Pphone, String Pname, String Paddress) {
        this.phone = Pphone;
        this.name = Pname;
        this.address = Paddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
