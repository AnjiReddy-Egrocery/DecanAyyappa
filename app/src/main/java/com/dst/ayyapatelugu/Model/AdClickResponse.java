package com.dst.ayyapatelugu.Model;

import java.util.List;

public class AdClickResponse {
    public String status;
    public String errorCode;
    public String message;
    public List<ClickItem> result;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ClickItem> getResult() {
        return result;
    }

    public void setResult(List<ClickItem> result) {
        this.result = result;
    }

    public static class ClickItem {
        public String clickId;
        public String registerId;
        public String imageId;
        public String fullName;
        public String userMobile;

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public String getClickId() {
            return clickId;
        }

        public void setClickId(String clickId) {
            this.clickId = clickId;
        }
    }
}
