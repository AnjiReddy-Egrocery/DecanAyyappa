package com.dst.ayyapatelugu.Model;

import java.util.List;

public class ForgotDataResponse {
    private String status;
    private String errorCode;
    private String imageUrl;
    private List<Result> result;
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

    public List<Result> getResult() {
        return result;
    }

    public void setResult(List<Result> result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public class Result {

        private String registerId;
        private String fullName;
        private Object userLocation;
        private String userEmail;
        private String userMobile;
        private Object userState;
        private Object userCity;
        private Object userDescription;
        private String profilePic;
        private String otp;

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

        public Object getUserLocation() {
            return userLocation;
        }

        public void setUserLocation(Object userLocation) {
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

        public Object getUserState() {
            return userState;
        }

        public void setUserState(Object userState) {
            this.userState = userState;
        }

        public Object getUserCity() {
            return userCity;
        }

        public void setUserCity(Object userCity) {
            this.userCity = userCity;
        }

        public Object getUserDescription() {
            return userDescription;
        }

        public void setUserDescription(Object userDescription) {
            this.userDescription = userDescription;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
        }

    }
}
