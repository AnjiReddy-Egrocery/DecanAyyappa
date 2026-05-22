package com.dst.ayyapatelugu.Model;

import java.util.List;

public class TempleDetailsResponse {
    public String status;
    public String errorCode;
    public String imageUrl;
    public List<TemplesItem> result;

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

    public List<TemplesItem> getResult() {
        return result;
    }

    public void setResult(List<TemplesItem> result) {
        this.result = result;
    }

    public class TemplesItem {
        public String templeId;
        public String templeName;
        public String templeNameTelugu;
        public String openingTime;
        public String closingTime;
        public String location;
        public String image;

        public String getTempleId() {
            return templeId;
        }

        public void setTempleId(String templeId) {
            this.templeId = templeId;
        }

        public String getTempleName() {
            return templeName;
        }

        public void setTempleName(String templeName) {
            this.templeName = templeName;
        }

        public String getTempleNameTelugu() {
            return templeNameTelugu;
        }

        public void setTempleNameTelugu(String templeNameTelugu) {
            this.templeNameTelugu = templeNameTelugu;
        }

        public String getOpeningTime() {
            return openingTime;
        }

        public void setOpeningTime(String openingTime) {
            this.openingTime = openingTime;
        }

        public String getClosingTime() {
            return closingTime;
        }

        public void setClosingTime(String closingTime) {
            this.closingTime = closingTime;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
