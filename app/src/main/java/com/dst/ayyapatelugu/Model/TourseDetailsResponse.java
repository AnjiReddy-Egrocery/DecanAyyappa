package com.dst.ayyapatelugu.Model;

import java.util.List;

public class TourseDetailsResponse {
    public String status;
    public String errorCode;
    public String imageUrl;
    public List<TourseItem> result;

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

    public List<TourseItem> getResult() {
        return result;
    }

    public void setResult(List<TourseItem> result) {
        this.result = result;
    }

    public class TourseItem {
        public String nameOfPlace;
        public String days;
        public String devotees;
        public String amount;
        public String image;

        public String getNameOfPlace() {
            return nameOfPlace;
        }

        public void setNameOfPlace(String nameOfPlace) {
            this.nameOfPlace = nameOfPlace;
        }

        public String getDays() {
            return days;
        }

        public void setDays(String days) {
            this.days = days;
        }

        public String getDevotees() {
            return devotees;
        }

        public void setDevotees(String devotees) {
            this.devotees = devotees;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
