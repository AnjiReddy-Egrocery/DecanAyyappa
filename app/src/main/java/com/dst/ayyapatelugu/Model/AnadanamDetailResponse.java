package com.dst.ayyapatelugu.Model;

import java.util.List;

public class AnadanamDetailResponse {
    public String status;
    public String errorCode;
    public String imageUrl;
    public List<AnadanamItem> result;

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

    public List<AnadanamItem> getResult() {
        return result;
    }

    public void setResult(List<AnadanamItem> result) {
        this.result = result;
    }

    public class AnadanamItem {

        public String annadhanamId;
        public String annadhanamName;
        public String annadhanamNameTelugu;
        public String startTime;
        public String endTime;
        public String location;
        public String image;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }
    }
}
