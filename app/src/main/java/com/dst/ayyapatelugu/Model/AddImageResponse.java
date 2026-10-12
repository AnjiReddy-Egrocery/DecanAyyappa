package com.dst.ayyapatelugu.Model;

import java.util.List;

public class AddImageResponse {

    public String status;
    public String errorCode;
    public String imageUrl;
    public List<AdItem> result;

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

    public List<AdItem> getResult() {
        return result;
    }

    public void setResult(List<AdItem> result) {
        this.result = result;
    }

    public static class AdItem {

        public String adimageId;


        public String adimageUrl;    // click chesinappudu open ayye link

        public String getAdimageId() {
            return adimageId;
        }

        public void setAdimageId(String adimageId) {
            this.adimageId = adimageId;
        }

        public String getAdimageUrl() {
            return adimageUrl;
        }

        public void setAdimageUrl(String adimageUrl) {
            this.adimageUrl = adimageUrl;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String image;         // image file name
    }
}
