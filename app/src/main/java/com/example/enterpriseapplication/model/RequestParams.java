package com.example.enterpriseapplication.model;

public class RequestParams {
    private String enterpriseId;
    private String appToken;

    public RequestParams(String enterpriseId,String appToken){
        this.enterpriseId = enterpriseId;
        this.appToken = appToken;
    }

    public String getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(String enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }
}
