package com.dst.ayyapatelugu.Model;

import java.util.List;

public class GuruSwamiDetailsResponse {
    public String status;
    public String errorCode;
    public String imageUrl;
    public List<GuruSwamiItem> result;

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

    public List<GuruSwamiItem> getResult() {
        return result;
    }

    public void setResult(List<GuruSwamiItem> result) {
        this.result = result;
    }

    public class GuruSwamiItem {
        public String guruswamiId;
        public String guruswamiName;
        public String templeName;
        public String cityName;
        public String profilePic;

        public String getGuruswamiId() {
            return guruswamiId;
        }

        public void setGuruswamiId(String guruswamiId) {
            this.guruswamiId = guruswamiId;
        }

        public String getGuruswamiName() {
            return guruswamiName;
        }

        public void setGuruswamiName(String guruswamiName) {
            this.guruswamiName = guruswamiName;
        }

        public String getTempleName() {
            return templeName;
        }

        public void setTempleName(String templeName) {
            this.templeName = templeName;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }
    }

}
