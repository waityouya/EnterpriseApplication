package com.example.enterpriseapplication.model;

public class LoginReturnData {
    private String userId;
    private String appToken;

    public String getUserId() {
        return userId;
    }


    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
