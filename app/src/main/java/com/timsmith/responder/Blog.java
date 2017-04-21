package com.timsmith.responder;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Tim on 30/10/2016.
 */
@IgnoreExtraProperties  //Ignore here is added for lat long set in mapping
public class Blog {

    private String title;
    private String desc;
    private String image;
    private String username;
    private String uid;//unused
    private Long timestamp;
    //private HashMap<String, Object> dateCreated;
    private String phone;
    private String latitudeText;
    private String longitudeText;
    private String location;



    public Blog(){

    }

    public Blog(String title, String desc, String image, Long timestamp, String latitudeText, String longitudeText, String phone, String location) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.timestamp = timestamp;
        this.latitudeText = latitudeText;
        this.longitudeText = longitudeText;
        this.phone = phone;
        this.location = location;

        this.username = username;
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getLatitudeText() {
        return latitudeText;
    }

    public void setLatitudeText(String latitudeText) {
        this.latitudeText = latitudeText;
    }

    public String getLongitudeText() {
        return longitudeText;
    }

    public void setLongitudeText(String longitudeText) {
        this.longitudeText = longitudeText;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
