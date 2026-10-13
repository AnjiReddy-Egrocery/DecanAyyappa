package com.dst.ayyapatelugu.Model;

import java.util.List;

public class BannerResponse {
    public String status;
    public String errorCode;
    public String imageUrl;          // base url, e.g. https://www.ayyappatelugu.com/public/assets/banner/
    public List<BannerItem> result;

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

    public List<BannerItem> getResult() {
        return result;
    }

    public void setResult(List<BannerItem> result) {
        this.result = result;
    }

    public static class BannerItem {
        public String bannerId;
        public String title;
        public String description;
        public String buttonText1;
        public String buttonUrl1;

        public String getBannerId() {
            return bannerId;
        }

        public void setBannerId(String bannerId) {
            this.bannerId = bannerId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getButtonText1() {
            return buttonText1;
        }

        public void setButtonText1(String buttonText1) {
            this.buttonText1 = buttonText1;
        }

        public String getButtonUrl1() {
            return buttonUrl1;
        }

        public void setButtonUrl1(String buttonUrl1) {
            this.buttonUrl1 = buttonUrl1;
        }

        public String getButtonText2() {
            return buttonText2;
        }

        public void setButtonText2(String buttonText2) {
            this.buttonText2 = buttonText2;
        }

        public String getButtonUrl2() {
            return buttonUrl2;
        }

        public void setButtonUrl2(String buttonUrl2) {
            this.buttonUrl2 = buttonUrl2;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String buttonText2;
        public String buttonUrl2;
        public String image;         // file name only
    }
}
