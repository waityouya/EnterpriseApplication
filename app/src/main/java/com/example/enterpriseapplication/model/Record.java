package com.example.enterpriseapplication.model;

public class Record {
    int recordId;
    int enterpriseId;
    String driverName;
    String plateNumber;
    String imagerUrl;
    int aduit;
    String vIN;
    String driverNumber;
    String notReason;
    String enterpriName;
    String appToken;
    String upTime;
    String aduitTime;

    public Record(int enterpriseId,String driverName,String plateNumber,String driverNumber,
                  String vin,String token){
        this.enterpriseId = enterpriseId;
        this.driverName = driverName;
        this.plateNumber = plateNumber;
        this.driverNumber = driverNumber;
        this.vIN  = vin;
        this.appToken = token;
    }

    public Record(String driverNumber,int enterpriseId,String token ){
        this.enterpriseId = enterpriseId;
        this.driverNumber = driverNumber;
        this.appToken = token;
    }

    public Record(){

    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
    }

    public String getAduitTime() {
        return aduitTime;
    }

    public void setAduitTime(String aduitTime) {
        this.aduitTime = aduitTime;
    }

    public String getAppToken() {
        return appToken;
    }

    public void setAppToken(String appToken) {
        this.appToken = appToken;
    }

    public int getAduit() {
        return aduit;
    }

    public String getDriverName() {
        return driverName;
    }
    public String getDriverNumber() {
        return driverNumber;
    }
    public String getEnterpriName() {
        return enterpriName;
    }
    public int getEnterpriseId() {
        return enterpriseId;
    }
    public String getImagerUrl() {
        return imagerUrl;
    }
    public String getNotReason() {
        return notReason;
    }
    public String getPlateNumber() {
        return plateNumber;
    }
    public int getRecordId() {
        return recordId;
    }

    public String getvIN() {
        return vIN;
    }

    public void setvIN(String vIN) {
        this.vIN = vIN;
    }

    public void setAduit(int aduit) {
        this.aduit = aduit;
    }
    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
    public void setDriverNumber(String driverNumber) {
        this.driverNumber = driverNumber;
    }
    public void setEnterpriName(String enterpriName) {
        this.enterpriName = enterpriName;
    }
    public void setEnterpriseId(int enterpriseId) {
        this.enterpriseId = enterpriseId;
    }
    public void setImagerUrl(String imagerUrl) {
        this.imagerUrl = imagerUrl;
    }
    public void setNotReason(String notReason) {
        this.notReason = notReason;
    }
    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }
    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

}
