package com.example.enterpriseapplication.model;

import java.util.List;

public class UpRecordData {
    String recordInfo;
    List<String> images;

    public UpRecordData(String recordInfo,List<String> images){
        this.recordInfo = recordInfo;
        this.images = images;
    }
    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getRecordInfo() {
        return recordInfo;
    }

    public void setRecordInfo(String recordInfo) {
        this.recordInfo = recordInfo;
    }
}
