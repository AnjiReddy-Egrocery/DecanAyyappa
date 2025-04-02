package com.dst.ayyapatelugu.Model;

import java.util.List;

public class TempleMapDataResponse {
    private String status;
    private String errorCode;
    private String imageUrl;
    private List<Result> result;

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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }
    public class Result {

        private String templeId;
        private String templeName;
        private String templeNameTelugu;
        private Object templeDetails;
        private Object templeDetailsTelugu;
        private String openingTime;
        private String closingTime;
        private String location;
        private String latitude;
        private String longitude;
        private String zipCode;
        private String isAyyappaTemple;
        private String image;

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

        public Object getTempleDetails() {
            return templeDetails;
        }

        public void setTempleDetails(Object templeDetails) {
            this.templeDetails = templeDetails;
        }

        public Object getTempleDetailsTelugu() {
            return templeDetailsTelugu;
        }

        public void setTempleDetailsTelugu(Object templeDetailsTelugu) {
            this.templeDetailsTelugu = templeDetailsTelugu;
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

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getIsAyyappaTemple() {
            return isAyyappaTemple;
        }

        public void setIsAyyappaTemple(String isAyyappaTemple) {
            this.isAyyappaTemple = isAyyappaTemple;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

    }
}
