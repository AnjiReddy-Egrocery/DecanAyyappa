package com.dst.ayyapatelugu.Model;

import java.util.List;

public class ActivitiesDetailsResponse {

    public String status;
    public String errorCode;
    public String imageUrl;
    public List<ActivitiesItem> result;

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

    public List<ActivitiesItem> getResult() {
        return result;
    }

    public void setResult(List<ActivitiesItem> result) {
        this.result = result;
    }

    public class ActivitiesItem {

        public String title;
        public String smallDescription;
        public String description;
        public String image;

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
}
