package com.dst.ayyapatelugu.Model;

import com.google.gson.annotations.SerializedName;

public class NewsListModel {

    private  String newsTitle;

    private String newsMiniDescription;

    private String image;

    public NewsListModel() {

    }

    public NewsListModel(String newsTitle, String discription, String image) {
        this.newsTitle = newsTitle;
        this.newsMiniDescription = discription;
        this.image = image;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getDiscription() {
        return newsMiniDescription;
    }

    public void setDiscription(String discription) {
        this.newsMiniDescription = discription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
