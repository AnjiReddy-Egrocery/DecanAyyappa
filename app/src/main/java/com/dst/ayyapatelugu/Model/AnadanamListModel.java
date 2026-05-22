package com.dst.ayyapatelugu.Model;

public class AnadanamListModel {
    private String annadhanamId;
    private String annadhanamName;
    private String annadhanamNameTelugu;
    private String startingDate;
    private String endingDate;
    private String startTime;
    private String endTime;
    private String location;
    private String image;

    public AnadanamListModel() {

    }

    public AnadanamListModel(String annadhanamId, String annadhanamName, String annadhanamNameTelugu, String startingDate, String endingDate, String startTime, String endTime, String location, String image) {
        this.annadhanamId = annadhanamId;
        this.annadhanamName = annadhanamName;
        this.annadhanamNameTelugu = annadhanamNameTelugu;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.image = image;
    }

    public String getAnnadhanamId() {
        return annadhanamId;
    }

    public void setAnnadhanamId(String annadhanamId) {
        this.annadhanamId = annadhanamId;
    }

    public String getAnnadhanamName() {
        return annadhanamName;
    }

    public void setAnnadhanamName(String annadhanamName) {
        this.annadhanamName = annadhanamName;
    }

    public String getAnnadhanamNameTelugu() {
        return annadhanamNameTelugu;
    }

    public void setAnnadhanamNameTelugu(String annadhanamNameTelugu) {
        this.annadhanamNameTelugu = annadhanamNameTelugu;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
