package com.dst.ayyapatelugu.Model;

import java.util.List;

public class PadayatraResponse {
    private String status;
    private String errorCode;
    private String imageUrl;
    private List<PadayatraBrundam> result;

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

    public List<PadayatraBrundam> getResult() {
        return result;
    }

    public void setResult(List<PadayatraBrundam> result) {
        this.result = result;
    }
}
