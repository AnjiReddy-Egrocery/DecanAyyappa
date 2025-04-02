package com.dst.ayyapatelugu.Model;

public class SevaListModel {

    private String title;
    private String smalldescription;
    private String description;
    private String image;

    public SevaListModel() {
    }

    public SevaListModel(String title, String smalldescription, String description, String image) {
        this.title = title;
        this.smalldescription = smalldescription;
        this.description = description;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmalldescription() {
        return smalldescription;
    }

    public void setSmalldescription(String smalldescription) {
        this.smalldescription = smalldescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
