package com.dst.ayyapatelugu.Model;

import java.util.List;

public class DecaratorDetailsResponse {
    public String status;
    public String errorCode;
    public String imageUrl;
    public List<DecaratorItem> result;

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

    public List<DecaratorItem> getResult() {
        return result;
    }

    public void setResult(List<DecaratorItem> result) {
        this.result = result;
    }
    public class DecaratorItem {
        public String decoratorName;
        public String fullName;
        public String specialization;
        public String villageName;
        public String cityName;
        public String mobileNumber;
        public String emailId;
        public String decoratorDescription;
        public String profilePic;

        public String getDecoratorName() {
            return decoratorName;
        }

        public void setDecoratorName(String decoratorName) {
            this.decoratorName = decoratorName;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getSpecialization() {
            return specialization;
        }

        public void setSpecialization(String specialization) {
            this.specialization = specialization;
        }

        public String getVillageName() {
            return villageName;
        }

        public void setVillageName(String villageName) {
            this.villageName = villageName;
        }

        public String getCityName() {
            return cityName;
        }

        public void setCityName(String cityName) {
            this.cityName = cityName;
        }

        public String getMobileNumber() {
            return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
            this.mobileNumber = mobileNumber;
        }

        public String getEmailId() {
            return emailId;
        }

        public void setEmailId(String emailId) {
            this.emailId = emailId;
        }

        public String getDecoratorDescription() {
            return decoratorDescription;
        }

        public void setDecoratorDescription(String decoratorDescription) {
            this.decoratorDescription = decoratorDescription;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }
    }
}
