package com.dst.ayyapatelugu.Model;

public class BlogDetail {
    private String blogId;
    private String title;
    private String titleUrl;
    private String image;
    private String smalldescription;
    private String description;

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleUrl() {
        return titleUrl;
    }

    public void setTitleUrl(String titleUrl) {
        this.titleUrl = titleUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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
}
