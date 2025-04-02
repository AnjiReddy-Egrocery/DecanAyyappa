package com.dst.ayyapatelugu.Model;

public class AyyaTempleListModel {
    private String templeName;
    private String templeNameTelugu;
    private String openingTime;
    private String closingTime;
    private String location;
    private String image;

    private String latitude;
    private String longitude;

    public AyyaTempleListModel() {

    }

    public AyyaTempleListModel(String templeName, String templeNameTelugu, String openingTime, String closingTime, String location, String image, String latitude, String longitude) {
        this.templeName = templeName;
        this.templeNameTelugu = templeNameTelugu;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.location = location;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getTempleName() {
        return templeName;
    }

    public void setTempleName(String templeName) {
        this.templeName = templeName;
    }

    public String getTempleNameTelugu() {
        return templeNameTelugu;
    }

    public void setTempleNameTelugu(String templeNameTelugu) {
        this.templeNameTelugu = templeNameTelugu;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
