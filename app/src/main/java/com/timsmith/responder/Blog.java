package com.timsmith.responder;

/**
 * Created by Tim on 30/10/2016.
 */

public class Blog {

    private String title;
    private String desc;
    private String image;
    private String username;
    private String uid;//unused
    private Long timestamp;
    //private HashMap<String, Object> dateCreated;
    private String phoneNumber;
    private String latitudeText;
    private String longitudeText;



    public Blog(){

    }

    public Blog(String title, String desc, String image, Long timestamp, String latitudeText, String longitudeText) {
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.timestamp = timestamp;
        this.latitudeText = latitudeText;
        this.longitudeText = longitudeText;

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


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
}
