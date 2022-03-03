package com.loss.entity;

public class TgLossReport {
    private String tgNo;
    private String tgName;
    private String consCount;
    private String dateDayStart;
    private String dateDayEnd;
    private Double lossPerAvg;
    private Double lossEle;
    private String tgTypeName;
    private String tgClass;
    private String mergeLinelossRate;
    private int excConsCount;
    private int relaDayCount;
    private String cityNo;
    private String countyNo;
    private String orgNo;
    private String fzRyName;
    private Double ppq;
    private Double upq;

    @Override
    public String toString() {
        return "TgLossReport{" +
                "tgNo='" + tgNo + '\'' +
                ", tgName='" + tgName + '\'' +
                ", consCount='" + consCount + '\'' +
                ", dateDayStart='" + dateDayStart + '\'' +
                ", dateDayEnd='" + dateDayEnd + '\'' +
                ", lossPerAvg=" + lossPerAvg +
                ", lossEle=" + lossEle +
                ", tgTypeName='" + tgTypeName + '\'' +
                ", tgClass='" + tgClass + '\'' +
                ", mergeLinelossRate='" + mergeLinelossRate + '\'' +
                ", excConsCount=" + excConsCount +
                ", relaDayCount=" + relaDayCount +
                ", cityNo='" + cityNo + '\'' +
                ", countyNo='" + countyNo + '\'' +
                ", orgNo='" + orgNo + '\'' +
                ", fzRyName='" + fzRyName + '\'' +
                ", ppq=" + ppq +
                ", upq=" + upq +
                '}';
    }

    public Double getPpq() {
        return ppq;
    }

    public void setPpq(Double ppq) {
        this.ppq = ppq;
    }

    public Double getUpq() {
        return upq;
    }

    public void setUpq(Double upq) {
        this.upq = upq;
    }

    public String getFzRyName() {
        return fzRyName;
    }

    public void setFzRyName(String fzRyName) {
        this.fzRyName = fzRyName;
    }

    public String getCityNo() {
        return cityNo;
    }

    public void setCityNo(String cityNo) {
        this.cityNo = cityNo;
    }

    public String getCountyNo() {
        return countyNo;
    }

    public void setCountyNo(String countyNo) {
        this.countyNo = countyNo;
    }

    public String getOrgNo() {
        return orgNo;
    }

    public void setOrgNo(String orgNo) {
        this.orgNo = orgNo;
    }

    public String getTgNo() {
        return tgNo;
    }

    public void setTgNo(String tgNo) {
        this.tgNo = tgNo;
    }

    public String getTgName() {
        return tgName;
    }

    public void setTgName(String tgName) {
        this.tgName = tgName;
    }

    public String getConsCount() {
        return consCount;
    }

    public void setConsCount(String consCount) {
        this.consCount = consCount;
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

    public Double getLossPerAvg() {
        return lossPerAvg;
    }

    public void setLossPerAvg(Double lossPerAvg) {
        this.lossPerAvg = lossPerAvg;
    }

    public Double getLossEle() {
        return lossEle;
    }

    public void setLossEle(Double lossEle) {
        this.lossEle = lossEle;
    }

    public String getTgTypeName() {
        return tgTypeName;
    }

    public void setTgTypeName(String tgTypeName) {
        this.tgTypeName = tgTypeName;
    }

    public String getTgClass() {
        return tgClass;
    }

    public void setTgClass(String tgClass) {
        this.tgClass = tgClass;
    }

    public String getMergeLinelossRate() {
        return mergeLinelossRate;
    }

    public void setMergeLinelossRate(String mergeLinelossRate) {
        this.mergeLinelossRate = mergeLinelossRate;
    }

    public int getExcConsCount() {
        return excConsCount;
    }

    public void setExcConsCount(int excConsCount) {
        this.excConsCount = excConsCount;
    }

    public int getRelaDayCount() {
        return relaDayCount;
    }

    public void setRelaDayCount(int relaDayCount) {
        this.relaDayCount = relaDayCount;
    }

}
