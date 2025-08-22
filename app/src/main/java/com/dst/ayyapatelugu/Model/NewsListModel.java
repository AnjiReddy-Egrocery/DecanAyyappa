package com.dst.ayyapatelugu.Model;

import com.google.gson.annotations.SerializedName;

public class NewsListModel {

    private  String newsTitle;

    private String newsDescription;

    private String image;

    public NewsListModel() {

    }

    public NewsListModel(String newsTitle, String discription, String image) {
        this.newsTitle = newsTitle;
        this.newsDescription = discription;
        this.image = image;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getDiscription() {
        return newsDescription;
    }

    public void setDiscription(String discription) {
        this.newsDescription = discription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
