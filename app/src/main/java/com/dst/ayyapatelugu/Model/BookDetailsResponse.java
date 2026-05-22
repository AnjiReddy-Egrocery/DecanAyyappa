package com.dst.ayyapatelugu.Model;

import java.util.List;

public class BookDetailsResponse {

    public String status;
    public String errorCode;
    public String imageUrl;
    public List<BookItem> result;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<BookItem> getResult() {
        return result;
    }

    public void setResult(List<BookItem> result) {
        this.result = result;
    }

    public class BookItem {
        public String name;
        public String author;
        public String publishedOn;
        public String price;
        public String pages;
        public String image;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPages() {
            return pages;
        }

        public void setPages(String pages) {
            this.pages = pages;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
