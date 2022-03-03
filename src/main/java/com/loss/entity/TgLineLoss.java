package com.loss.entity;

public class TgLineLoss {
    private String tgNo;
    private Double ppq;
    private Double upq;
    private Double lossPq;
    private Double lossPer;
    private String eventTime;
    private String remark;

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

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "TgLineLoss{" +
                "tgNo='" + tgNo + '\'' +
                ", ppq=" + ppq +
                ", upq=" + upq +
                ", lossPq=" + lossPq +
                ", lossPer=" + lossPer +
                ", eventTime='" + eventTime + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
