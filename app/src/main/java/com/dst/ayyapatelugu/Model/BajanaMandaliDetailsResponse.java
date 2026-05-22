package com.dst.ayyapatelugu.Model;

import java.util.List;

public class BajanaMandaliDetailsResponse {

    public String status;
    public String errorCode;
    public String imageUrl;
    public List<BajanaMandaliItem> result;

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

    public List<BajanaMandaliItem> getResult() {
        return result;
    }

    public void setResult(List<BajanaMandaliItem> result) {
        this.result = result;
    }
    public class BajanaMandaliItem {
        public String bajanamandaliName;
        public String nameOfGuru;
        public String bajanamandaliEmail;
        public String bajanamandaliMobile;
        public String bajanamandaliCity;
        public String bajanamandaliDescription;
        public String profilePic;

        public String getBajanamandaliName() {
            return bajanamandaliName;
        }

        public void setBajanamandaliName(String bajanamandaliName) {
            this.bajanamandaliName = bajanamandaliName;
        }

        public String getNameOfGuru() {
            return nameOfGuru;
        }

        public void setNameOfGuru(String nameOfGuru) {
            this.nameOfGuru = nameOfGuru;
        }

        public String getBajanamandaliEmail() {
            return bajanamandaliEmail;
        }

        public void setBajanamandaliEmail(String bajanamandaliEmail) {
            this.bajanamandaliEmail = bajanamandaliEmail;
        }

        public String getBajanamandaliMobile() {
            return bajanamandaliMobile;
        }

        public void setBajanamandaliMobile(String bajanamandaliMobile) {
            this.bajanamandaliMobile = bajanamandaliMobile;
        }

        public String getBajanamandaliCity() {
            return bajanamandaliCity;
        }

        public void setBajanamandaliCity(String bajanamandaliCity) {
            this.bajanamandaliCity = bajanamandaliCity;
        }

        public String getBajanamandaliDescription() {
            return bajanamandaliDescription;
        }

        public void setBajanamandaliDescription(String bajanamandaliDescription) {
            this.bajanamandaliDescription = bajanamandaliDescription;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }
    }
}
