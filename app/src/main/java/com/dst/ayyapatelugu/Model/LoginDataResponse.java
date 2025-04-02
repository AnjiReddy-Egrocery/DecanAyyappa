package com.dst.ayyapatelugu.Model;

public class LoginDataResponse {
    private String status;
    private String errorCode;
    private String imageUrl;
    private Result result;
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

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public static class Result {

        private String userMid;
        private String userId;
        private String userEmail;
        private String userMobile;
        private String userFirstName;
        private String userLastName;
        private String userIsAdmin;
        private String userUserType;
        private String userSannidanamId;

        public Result(String userId, String firstname, String lastname, String email, String mobilenumber) {

            this.userId=userId;
            this.userFirstName=firstname;
            this.userLastName=lastname;
            this.userEmail=email;
            this.userMobile=mobilenumber;

        }

        public String getUserMid() {
            return userMid;
        }

        public void setUserMid(String userMid) {
            this.userMid = userMid;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
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

        public String getUserFirstName() {
            return userFirstName;
        }

        public void setUserFirstName(String userFirstName) {
            this.userFirstName = userFirstName;
        }

        public String getUserLastName() {
            return userLastName;
        }

        public void setUserLastName(String userLastName) {
            this.userLastName = userLastName;
        }

        public String getUserIsAdmin() {
            return userIsAdmin;
        }

        public void setUserIsAdmin(String userIsAdmin) {
            this.userIsAdmin = userIsAdmin;
        }

        public String getUserUserType() {
            return userUserType;
        }

        public void setUserUserType(String userUserType) {
            this.userUserType = userUserType;
        }

        public String getUserSannidanamId() {
            return userSannidanamId;
        }

        public void setUserSannidanamId(String userSannidanamId) {
            this.userSannidanamId = userSannidanamId;
        }

    }
}
