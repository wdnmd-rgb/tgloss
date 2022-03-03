package com.loss.entity;

public class MaxEle {
    private String rid;
    private String eventTime;
    private Double ele;

    public String getRid() {
        return rid;
    }

    public void setRid(String rid) {
        this.rid = rid;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public Double getEle() {
        return ele;
    }

    public void setEle(Double ele) {
        this.ele = ele;
    }

    @Override
    public String toString() {
        return "MaxEle{" +
                "rid='" + rid + '\'' +
                ", eventTime='" + eventTime + '\'' +
                ", ele='" + ele + '\'' +
                '}';
    }
}
