package com.dst.ayyapatelugu.Model;

public class GuruSwamiModelList {
    private String guruswamiId;
    private String guruswamiName;
    private String cityName;
    private String profilePic;
    private String templeName;
    private String mobileNo;
    private String locationName;
    private String stateName;

    // Default constructor
    public GuruSwamiModelList() {}

    // Full constructor
    public GuruSwamiModelList(String guruswamiId, String guruswamiName, String cityName, String profilePic, String templeName, String mobileNo, String locationName, String stateName) {
        this.guruswamiId = guruswamiId;
        this.guruswamiName = guruswamiName;
        this.cityName = cityName;
        this.profilePic = profilePic;
        this.templeName = templeName;
        this.mobileNo = mobileNo;
        this.locationName = locationName;
        this.stateName = stateName;
    }

    // Getter and Setter methods for each field
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

    public String getTempleName() {
        return templeName;
    }

    public void setTempleName(String templeName) {
        this.templeName = templeName;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}
