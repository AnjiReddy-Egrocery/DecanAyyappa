package com.dst.ayyapatelugu.Model;

import java.util.List;

public class NewsDetailsResponse {
    public String status;
    public String errorCode;
    public String imageUrl;
    public List<NewsItem> result;

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

    public List<NewsItem> getResult() {
        return result;
    }

    public void setResult(List<NewsItem> result) {
        this.result = result;
    }

    public class NewsItem {
        public String newsId;
        public String newsTitle;
        public String newsDescription;
        public String image;

        public String getNewsId() {
            return newsId;
        }

        public void setNewsId(String newsId) {
            this.newsId = newsId;
        }

        public String getNewsTitle() {
            return newsTitle;
        }

        public void setNewsTitle(String newsTitle) {
            this.newsTitle = newsTitle;
        }

        public String getNewsDescription() {
            return newsDescription;
        }

        public void setNewsDescription(String newsDescription) {
            this.newsDescription = newsDescription;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
