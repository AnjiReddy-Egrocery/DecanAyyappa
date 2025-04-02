package com.dst.ayyapatelugu.Model;


public class decoratormodelResult {

    private String decoratorId;

    private String decoratorName;
    private String specialization;
    private String fullName;
    private String villageName;
    private String mobileNumber;
    private String emailId;
    private String cityName;
    private String decoratorDescription;

    private String decoratorNameUrl;

    private String profilePic;

    public decoratormodelResult() {

    }

    public decoratormodelResult(String decoratorId, String decoratorName, String specialization, String fullName, String villageName, String mobileNumber, String emailId, String cityName, String decoratorDescription, String decoratorNameUrl, String profilePic) {
        this.decoratorId = decoratorId;
        this.decoratorName = decoratorName;
        this.specialization = specialization;
        this.fullName = fullName;
        this.villageName = villageName;
        this.mobileNumber = mobileNumber;
        this.emailId = emailId;
        this.cityName = cityName;
        this.decoratorDescription = decoratorDescription;
        this.decoratorNameUrl = decoratorNameUrl;
        this.profilePic = profilePic;
    }

    public String getDecoratorId() {
        return decoratorId;
    }

    public void setDecoratorId(String decoratorId) {
        this.decoratorId = decoratorId;
    }

    public String getDecoratorName() {
        return decoratorName;
    }

    public void setDecoratorName(String decoratorName) {
        this.decoratorName = decoratorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getVillageName() {
        return villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
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

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDecoratorDescription() {
        return decoratorDescription;
    }

    public void setDecoratorDescription(String decoratorDescription) {
        this.decoratorDescription = decoratorDescription;
    }

    public String getDecoratorNameUrl() {
        return decoratorNameUrl;
    }

    public void setDecoratorNameUrl(String decoratorNameUrl) {
        this.decoratorNameUrl = decoratorNameUrl;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }
}
