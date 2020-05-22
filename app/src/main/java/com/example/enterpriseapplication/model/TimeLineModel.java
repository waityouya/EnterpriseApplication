package com.example.enterpriseapplication.model;

public class TimeLineModel {
    /** 时间 */
    private String acceptTime;
    /** 描述 */
    private String acceptStation;

    public TimeLineModel() {
    }

    public TimeLineModel(String acceptTime, String acceptStation) {
        this.acceptTime = acceptTime;
        this.acceptStation = acceptStation;
    }

    public String getAcceptTime() {
        return acceptTime;
    }

    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }

    public String getAcceptStation() {
        return acceptStation;
    }

    public void setAcceptStation(String acceptStation) {
        this.acceptStation = acceptStation;
    }
}
