package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Shell Li on 2017/8/8.
 */
@Entity
@Table(name = "ads_plan")
public class AdsPlan implements Serializable {
    public Long planId;
    public String title;
    public Long uid;
    public String userName;
    public String price;
    public Integer userTotal;
    public double limitMoney;
    public Integer payType;
    public String isAudit;
    public Integer deduction;
    public String limitArea;
    public String limitTime;
    public String limitDevice;
    public String limitUid = "";
    public String limitSite = "";
    public int limitDiv;
    public int limitDivHeight;
    public int limitPop;
    public String limitType;

    public Date startTime;
    public Date stopTime;
    public Integer pstatus = 0;
    public Date addTime;
    public String adsUrl;
    public int priority;
    private String isnotify;
    private Double clickRate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planid", unique = true, nullable = false)
    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Column(name = "uid")
    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    @Column(name = "username")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Column(name = "price")
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    @Column(name = "usertotal")
    public Integer getUserTotal() {
        return userTotal;
    }

    public void setUserTotal(Integer userTotal) {
        this.userTotal = userTotal;
    }

    @Column(name = "limitmoney")
    public double getLimitMoney() {
        return limitMoney;
    }

    public void setLimitMoney(double limitMoney) {
        this.limitMoney = limitMoney;
    }

    @Column(name = "paytype")
    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    @Column(name = "isaudit")
    public String getIsAudit() {
        return isAudit;
    }

    public void setIsAudit(String isAudit) {
        this.isAudit = isAudit;
    }

    @Column(name = "deduction")
    public Integer getDeduction() {
        return deduction;
    }

    public void setDeduction(Integer deduction) {
        this.deduction = deduction;
    }

    @Column(name = "limitarea")
    public String getLimitArea() {
        return limitArea;
    }

    public void setLimitArea(String limitArea) {
        this.limitArea = limitArea;
    }

    @Column(name = "limittime")
    public String getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(String limitTime) {
        this.limitTime = limitTime;
    }

    @Column(name = "limitdevice")
    public String getLimitDevice() {
        return limitDevice;
    }

    public void setLimitDevice(String limitDevice) {
        this.limitDevice = limitDevice;
    }

    @Column(name = "limituid")
    public String getLimitUid() {
        return limitUid;
    }

    public void setLimitUid(String limitUid) {
        this.limitUid = limitUid;
    }

    @Column(name = "limitsite")
    public String getLimitSite() {
        return limitSite;
    }

    public void setLimitSite(String limitSite) {
        this.limitSite = limitSite;
    }

    @Column(name = "limitdiv")
    public int getLimitDiv() {
        return limitDiv;
    }

    public void setLimitDiv(int limitDiv) {
        this.limitDiv = limitDiv;
    }

    @Column(name = "limitdivheight")
    public int getLimitDivHeight() {
        return limitDivHeight;
    }

    public void setLimitDivHeight(int limitDivHeight) {
        this.limitDivHeight = limitDivHeight;
    }

    @Column(name = "limitpop")
    public int getLimitPop() {
        return limitPop;
    }

    public void setLimitPop(int limitPop) {
        this.limitPop = limitPop;
    }

    @Column(name = "limittype")
    public String getLimitType() {
        return limitType;
    }

    public void setLimitType(String limitType) {
        this.limitType = limitType;
    }

    @Column(name = "starttime")
    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    @Column(name = "stoptime")
    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
    }

    @Column(name = "pstatus")
    public int getPstatus() {
        return pstatus;
    }

    public void setPstatus(Integer pstatus) {
        this.pstatus = pstatus;
    }

    @Column(name = "add_time")
    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    @Column(name = "ads_url")
    public String getAdsUrl() {
        return adsUrl;
    }

    public void setAdsUrl(String adsUrl) {
        this.adsUrl = adsUrl;
    }

    @Column(name = "priority")
    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Column(name = "isnotify")
    public String getIsnotify() {
        return isnotify;
    }

    public void setIsnotify(String isnotify) {
        this.isnotify = isnotify;
    }

    @Column(name = "click_rate")
    public Double getClickRate() {
        return clickRate;
    }

    public void setClickRate(Double clickRate) {
        this.clickRate = clickRate;
    }
}
