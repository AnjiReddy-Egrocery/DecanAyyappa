package com.dst.ayyapatelugu.Model;

public class BooksModelResult {
    private String name;
    private String price;
    private String image;
    private String author;
    private String publishedOn;
    private String pages;

    public BooksModelResult() {

    }

    public BooksModelResult(String name, String price, String image, String author, String publishedOn, String pages) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.author = author;
        this.publishedOn = publishedOn;
        this.pages = pages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }
}
