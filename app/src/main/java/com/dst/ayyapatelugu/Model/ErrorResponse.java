package com.dst.ayyapatelugu.Model;

import com.google.gson.annotations.SerializedName;

public class ErrorResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("postedData")
    private String postedData;

    @SerializedName("errorMessage")
    private String errorMessage;

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

    public String getPostedData() {
        return postedData;
    }

    public void setPostedData(String postedData) {
        this.postedData = postedData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
