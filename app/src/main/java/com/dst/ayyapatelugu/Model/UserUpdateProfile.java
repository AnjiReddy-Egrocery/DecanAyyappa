package com.dst.ayyapatelugu.Model;

import java.util.List;

public class UserUpdateProfile {

    private String status;
    private String errorCode;
    private String imageUrl;
    private List<UserInfoModel> result;
    private String message;

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

    public List<UserInfoModel> getResult() {
        return result;
    }

    public void setResult(List<UserInfoModel> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class UserInfoModel {
        private String registerId;
        private String fullName;
        private String userLocation;
        private String userEmail;
        private String userMobile;
        private String userState;
        private String userCity;
        private String userDescription;
        private String profilePic;
        private String nameOnFlyer;
        private String designationOnFlyer;
        private String picOnFlyer;

        public String getRegisterId() {
            return registerId;
        }

        public void setRegisterId(String registerId) {
            this.registerId = registerId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getUserLocation() {
            return userLocation;
        }

        public void setUserLocation(String userLocation) {
            this.userLocation = userLocation;
        }

        public String getUserEmail() {
            return userEmail;
        }

        public void setUserEmail(String userEmail) {
            this.userEmail = userEmail;
        }

        public String getUserMobile() {
            return userMobile;
        }

        public void setUserMobile(String userMobile) {
            this.userMobile = userMobile;
        }

        public String getUserState() {
            return userState;
        }

        public void setUserState(String userState) {
            this.userState = userState;
        }

        public String getUserCity() {
            return userCity;
        }

        public void setUserCity(String userCity) {
            this.userCity = userCity;
        }

        public String getUserDescription() {
            return userDescription;
        }

        public void setUserDescription(String userDescription) {
            this.userDescription = userDescription;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getNameOnFlyer() {
            return nameOnFlyer;
        }

        public void setNameOnFlyer(String nameOnFlyer) {
            this.nameOnFlyer = nameOnFlyer;
        }

        public String getDesignationOnFlyer() {
            return designationOnFlyer;
        }

        public void setDesignationOnFlyer(String designationOnFlyer) {
            this.designationOnFlyer = designationOnFlyer;
        }

        public String getPicOnFlyer() {
            return picOnFlyer;
        }

        public void setPicOnFlyer(String picOnFlyer) {
            this.picOnFlyer = picOnFlyer;
        }
    }
}
