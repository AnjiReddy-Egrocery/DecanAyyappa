package com.dst.ayyapatelugu.Model;

public class YatraListModel {

    private String nameOfPlace;
    private String days;
    private String devotees;
    private String amount;
    private String image;

    public YatraListModel() {

    }

    public YatraListModel(String nameOfPlace, String days, String devotees, String amount, String image) {
        this.nameOfPlace = nameOfPlace;
        this.days = days;
        this.devotees = devotees;
        this.amount = amount;
        this.image = image;
    }

    public String getNameOfPlace() {
        return nameOfPlace;
    }

    public void setNameOfPlace(String nameOfPlace) {
        this.nameOfPlace = nameOfPlace;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getDevotees() {
        return devotees;
    }

    public void setDevotees(String devotees) {
        this.devotees = devotees;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
