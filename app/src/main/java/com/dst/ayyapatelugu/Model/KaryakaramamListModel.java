package com.dst.ayyapatelugu.Model;

public class KaryakaramamListModel {

    private String activitiesId;
    private String title;
    private String smallDescription;
    private String image;

    private String description;

    public KaryakaramamListModel() {

    }

    public KaryakaramamListModel(String activitiesId, String title, String smallDescription, String image, String description) {
        this.activitiesId = activitiesId;
        this.title = title;
        this.smallDescription = smallDescription;
        this.image = image;
        this.description = description;
    }

    public String getActivitiesId() {
        return activitiesId;
    }

    public void setActivitiesId(String activitiesId) {
        this.activitiesId = activitiesId;
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
