package com.timsmith.responder;

/**
 * Created by Tim on 08/02/2017.
 */

public class User {
    private String name;
    private String phone;
    private String image;
    private String dob;//must match db

    public User(){

    }

    public User(String name, String phone, String image, String dob) {
        this.name = name;
        this.phone = phone;
        this.image = image;
        this.dob = dob;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
