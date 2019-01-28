package com.sri.dominospizza.Model;

/**
 * Created by Scarecrow on 2/6/2018.
 */

public class User {

    private String name;
    private String password;
    private String phone;
    private String isStaff;
    private String secureCode;

    public User() {
    }

    public User(String Pname, String Ppassword, String secureCode) {
        name = Pname;
        password = Ppassword;
        isStaff = "false";
        this.secureCode = secureCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIsStaff() {
        return isStaff;
    }

    public void setIsStaff(String isStaff) {
        this.isStaff = isStaff;
    }

    public String getSecureCode() {
        return secureCode;
    }

    public void setSecureCode(String secureCode) {
        this.secureCode = secureCode;
    }
}
