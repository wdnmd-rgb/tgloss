package com.loss.entity;

public class ConsReport {
    private String tgNo;
    private String rid;
    private String consNo;
    private String consName;
    private Double tFactor;
    private String assetNo;
    private Double lossEle;
    private Double lossPre;
    private Double ele;
    private Double pearson;
    private String dateDay;
    private String eleArray;
    private String tgEleArray;
    private String dateDayStart;
    private String timeArray;
    private String maxIndex;

    public String getTgNo() {
        return tgNo;
    }

    public void setTgNo(String tgNo) {
        this.tgNo = tgNo;
    }

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

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

    public Double gettFactor() {
        return tFactor;
    }

    public void settFactor(Double tFactor) {
        this.tFactor = tFactor;
    }

    public String getAssetNo() {
        return assetNo;
    }

    public void setAssetNo(String assetNo) {
        this.assetNo = assetNo;
    }

    public Double getEle() {
        return ele;
    }

    public void setEle(Double ele) {
        this.ele = ele;
    }

    public Double getPearson() {
        return pearson;
    }

    public void setPearson(Double pearson) {
        this.pearson = pearson;
    }

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
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

    public Double getLossEle() {
        return lossEle;
    }

    public void setLossEle(Double lossEle) {
        this.lossEle = lossEle;
    }

    public Double getLossPre() {
        return lossPre;
    }

    public void setLossPre(Double lossPre) {
        this.lossPre = lossPre;
    }

    public String getDateDayStart() {
        return dateDayStart;
    }

    public void setDateDayStart(String dateDayStart) {
        this.dateDayStart = dateDayStart;
    }

    public String getTimeArray() {
        return timeArray;
    }

    public void setTimeArray(String timeArray) {
        this.timeArray = timeArray;
    }

    public String getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(String maxIndex) {
        this.maxIndex = maxIndex;
    }

    @Override
    public String toString() {
        return "ConsReport{" +
                "tgNo='" + tgNo + '\'' +
                ", rid='" + rid + '\'' +
                ", consNo='" + consNo + '\'' +
                ", consName='" + consName + '\'' +
                ", tFactor=" + tFactor +
                ", assetNo='" + assetNo + '\'' +
                ", lossEle=" + lossEle +
                ", lossPre=" + lossPre +
                ", ele=" + ele +
                ", pearson=" + pearson +
                ", dateDay='" + dateDay + '\'' +
                ", eleArray='" + eleArray + '\'' +
                ", tgEleArray='" + tgEleArray + '\'' +
                ", dateDayStart='" + dateDayStart + '\'' +
                ", timeArray='" + timeArray + '\'' +
                ", maxIndex='" + maxIndex + '\'' +
                '}';
    }
}
