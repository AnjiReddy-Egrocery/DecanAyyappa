package com.dst.ayyapatelugu.Model;

import java.util.List;

public class MapDataResponse {
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

        private String annadhanamId;
        private String annadhanamName;
        private String annadhanamNameTelugu;
        private String annadhanamDetails;
        private String annadhanamDetailsTelugu;
        private String cpName;
        private String cpMobile;
        private String startingDate;
        private String endingDate;
        private String startTime;
        private String endTime;
        private String location;
        private String latitude;
        private String longitude;
        private String zipCode;
        private String image;

        public String getAnnadhanamId() {
            return annadhanamId;
        }

        public void setAnnadhanamId(String annadhanamId) {
            this.annadhanamId = annadhanamId;
        }

        public String getAnnadhanamName() {
            return annadhanamName;
        }

        public void setAnnadhanamName(String annadhanamName) {
            this.annadhanamName = annadhanamName;
        }

        public String getAnnadhanamNameTelugu() {
            return annadhanamNameTelugu;
        }

        public void setAnnadhanamNameTelugu(String annadhanamNameTelugu) {
            this.annadhanamNameTelugu = annadhanamNameTelugu;
        }

        public String getAnnadhanamDetails() {
            return annadhanamDetails;
        }

        public void setAnnadhanamDetails(String annadhanamDetails) {
            this.annadhanamDetails = annadhanamDetails;
        }

        public String getAnnadhanamDetailsTelugu() {
            return annadhanamDetailsTelugu;
        }

        public void setAnnadhanamDetailsTelugu(String annadhanamDetailsTelugu) {
            this.annadhanamDetailsTelugu = annadhanamDetailsTelugu;
        }

        public String getCpName() {
            return cpName;
        }

        public void setCpName(String cpName) {
            this.cpName = cpName;
        }

        public String getCpMobile() {
            return cpMobile;
        }

        public void setCpMobile(String cpMobile) {
            this.cpMobile = cpMobile;
        }

        public String getStartingDate() {
            return startingDate;
        }

        public void setStartingDate(String startingDate) {
            this.startingDate = startingDate;
        }

        public String getEndingDate() {
            return endingDate;
        }

        public void setEndingDate(String endingDate) {
            this.endingDate = endingDate;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
