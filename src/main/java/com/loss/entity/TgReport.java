package com.loss.entity;

public class TgReport {
    private String tgNo;
    private Double ppq;
    private Double upq;
    private Double lossPq;
    private Double lossPer;
    private String dateDay;
    private int count;
    private int realCount;

    public String getTgNo() {
        return tgNo;
    }

    public void setTgNo(String tgNo) {
        this.tgNo = tgNo;
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

    public Double getLossPq() {
        return lossPq;
    }

    public void setLossPq(Double lossPq) {
        this.lossPq = lossPq;
    }

    public Double getLossPer() {
        return lossPer;
    }

    public void setLossPer(Double lossPer) {
        this.lossPer = lossPer;
    }

    public String getDateDay() {
        return dateDay;
    }

    public void setDateDay(String dateDay) {
        this.dateDay = dateDay;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getRealCount() {
        return realCount;
    }

    public void setRealCount(int realCount) {
        this.realCount = realCount;
    }

    @Override
    public String toString() {
        return "TgReport{" +
                "tgNo='" + tgNo + '\'' +
                ", ppq=" + ppq +
                ", upq=" + upq +
                ", lossPq=" + lossPq +
                ", lossPer=" + lossPer +
                ", dateDay='" + dateDay + '\'' +
                ", count=" + count +
                ", realCount=" + realCount +
                '}';
    }
}

