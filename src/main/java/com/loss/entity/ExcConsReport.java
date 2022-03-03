package com.loss.entity;

public class ExcConsReport {
    private String consNo;
    private String consName;
    private String consAddr;
    private String assetNo;
    private String rid;
    private Double pearson;
    private Double ele;
    private String maxIndex;
    private String eleArray;
    private String tgEleArray;
    private String dateDayStart;
    private String dateDayEnd;
    private String tgNo;
    private String timeArray;

    public String getConsNo() {
        return consNo;
    }

    public void setConsNo(String consNo) {
        this.consNo = consNo;
    }

    public String getConsName() {
        return consName;
    }

    public void setConsName(String consName) {
        this.consName = consName;
    }

    public String getConsAddr() {
        return consAddr;
    }

    public void setConsAddr(String consAddr) {
        this.consAddr = consAddr;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public Double getPearson() {
        return pearson;
    }

    public void setPearson(Double pearson) {
        this.pearson = pearson;
    }

    public Double getEle() {
        return ele;
    }

    public void setEle(Double ele) {
        this.ele = ele;
    }

    public String getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(String maxIndex) {
        this.maxIndex = maxIndex;
    }

    public String getEleArray() {
        return eleArray;
    }

    public void setEleArray(String eleArray) {
        this.eleArray = eleArray;
    }

    public String getTgEleArray() {
        return tgEleArray;
    }

    public void setTgEleArray(String tgEleArray) {
        this.tgEleArray = tgEleArray;
    }

    public String getDateDayStart() {
        return dateDayStart;
    }

    public void setDateDayStart(String dateDayStart) {
        this.dateDayStart = dateDayStart;
    }

    public String getDateDayEnd() {
        return dateDayEnd;
    }

    public void setDateDayEnd(String dateDayEnd) {
        this.dateDayEnd = dateDayEnd;
    }

    public String getTgNo() {
        return tgNo;
    }

    public void setTgNo(String tgNo) {
        this.tgNo = tgNo;
    }

    public String getTimeArray() {
        return timeArray;
    }

    public void setTimeArray(String timeArray) {
        this.timeArray = timeArray;
    }

    @Override
    public String toString() {
        return "ExcConsReport{" +
                "consNo='" + consNo + '\'' +
                ", consName='" + consName + '\'' +
                ", consAddr='" + consAddr + '\'' +
                ", assetNo='" + assetNo + '\'' +
                ", rid='" + rid + '\'' +
                ", pearson=" + pearson +
                ", ele=" + ele +
                ", maxIndex='" + maxIndex + '\'' +
                ", eleArray='" + eleArray + '\'' +
                ", tgEleArray='" + tgEleArray + '\'' +
                ", dateDayStart='" + dateDayStart + '\'' +
                ", dateDayEnd='" + dateDayEnd + '\'' +
                ", tgNo='" + tgNo + '\'' +
                ", timeArray='" + timeArray + '\'' +
                '}';
    }
}
