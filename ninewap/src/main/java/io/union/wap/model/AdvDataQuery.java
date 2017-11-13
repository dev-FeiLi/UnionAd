package io.union.wap.model;

import java.util.Date;

public class AdvDataQuery {
    private Long uid;
    private Date startTime;
    private Date stopTime;

    public Date getStartTime() {
        return startTime;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }
}
