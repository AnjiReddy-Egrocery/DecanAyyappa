package com.dst.ayyapatelugu.Model;

public class KaryakaramamListModel {

    private String title;
    private String smallDescription;
    private String image;

    private String description;

    public KaryakaramamListModel() {

    }

    public KaryakaramamListModel(String title, String smallDescription, String image, String description) {
        this.title = title;
        this.smallDescription = smallDescription;
        this.image = image;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSmallDescription() {
        return smallDescription;
    }

    public void setSmallDescription(String smallDescription) {
        this.smallDescription = smallDescription;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
