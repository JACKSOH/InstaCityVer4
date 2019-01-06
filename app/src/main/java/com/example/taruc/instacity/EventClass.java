package com.example.taruc.instacity;

public class EventClass {
    public String userName;
    public String uploadDate;
    public String uploadTime;
    public String startTime;
    public String eventTitle;
    public String eventCaption;
    public String eventDate;
    public String eventImage;
    public String endTime;
    public String location;
    public String uid;
    public String locationShort;
    public double lat;
    public double lng;

    public EventClass(){

    }

    public EventClass(String userName, String uploadDate, String uploadTime, String startTime, String eventTitle, String eventCaption, String eventDate, String eventImage, String endTime, String location, String uid, String locationShort, double lat, double lng) {
        this.userName = userName;
        this.uploadDate = uploadDate;
        this.uploadTime = uploadTime;
        this.startTime = startTime;
        this.eventTitle = eventTitle;
        this.eventCaption = eventCaption;
        this.eventDate = eventDate;
        this.eventImage = eventImage;
        this.endTime = endTime;
        this.location = location;
        this.uid = uid;
        this.locationShort = locationShort;
        this.lat = lat;
        this.lng = lng;
    }

    public String getLocationShort() {
        return locationShort;
    }

    public void setLocationShort(String locationShort) {
        this.locationShort = locationShort;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getEventCaption() {
        return eventCaption;
    }

    public void setEventCaption(String eventCaption) {
        this.eventCaption = eventCaption;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventImage() {
        return eventImage;
    }

    public void setEventImage(String eventImage) {
        this.eventImage = eventImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(String uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(String uploadTime) {
        this.uploadTime = uploadTime;
    }
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }









}

