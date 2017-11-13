package io.union.admin.model;

import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
public class UnionStatsPlan implements Serializable {
    @Excel(name = "日期", orderNum = "1", width = 20D, exportFormat = "yyyy-MM-dd", mergeVertical = false)
    private Date addTime;

    @Excel(name = "广告商名称", orderNum = "2", width = 20D, mergeVertical = false)
    private String username;

    @Excel(name = "广告商ID", orderNum = "3", width = 10D, mergeVertical = false)
    private Long uid;

    @Excel(name = "名称", orderNum = "4", width = 24D, mergeVertical = false)
    private String name;

    private Long id;

    @Excel(name = "展现", orderNum = "5", width = 10D, mergeVertical = false)
    private Long views = 0L;

    @Excel(name = "点击", orderNum = "6", width = 10D, mergeVertical = false)
    private Long clicks = 0L;

    @Excel(name = "排重", orderNum = "7", width = 10D, mergeVertical = false)
    private Long clickip = 0L;

    @Excel(name = "点击率", orderNum = "8", width = 10D, mergeVertical = false)
    private String clickRate;

    @Excel(name = "结算", orderNum = "9", width = 15D, mergeVertical = false)
    private Long paynum = 0L;

    @Excel(name = "扣量", orderNum = "10", width = 15D, mergeVertical = false)
    private Long dedunum = 0L;

    @Excel(name = "结算率", orderNum = "11", width = 10D, mergeVertical = false)
    private String payRate;

    @Excel(name = "应付", orderNum = "12", width = 15D, mergeVertical = false)
    private Double sumpay = 0.0;

    @Excel(name = "应收", orderNum = "13", width = 15D, mergeVertical = false)
    private Double sumadvpay = 0.0;

    @Excel(name = "盈利", orderNum = "14", width = 15D, mergeVertical = false)
    private Double sumprofit = 0.0;

    private Long planid;

    private Long adid;

    private Long advid;

    private Long siteid;

    private Long zoneid;

    private Integer paytype;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getViews() {
        return views;
    }

    public void setViews(Long views) {
        this.views = views;
    }

    public Long getClicks() {
        return clicks;
    }

    public void setClicks(Long clicks) {
        this.clicks = clicks;
    }

    public Long getClickip() {
        return clickip;
    }

    public void setClickip(Long clickip) {
        this.clickip = clickip;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getPlanid() {
        return planid;
    }

    public void setPlanid(Long planid) {
        this.planid = planid;
    }

    public Long getAdid() {
        return adid;
    }

    public void setAdid(Long adid) {
        this.adid = adid;
    }

    public Long getAdvid() {
        return advid;
    }

    public void setAdvid(Long advid) {
        this.advid = advid;
    }

    public Long getSiteid() {
        return siteid;
    }

    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }

    public Long getZoneid() {
        return zoneid;
    }

    public void setZoneid(Long zoneid) {
        this.zoneid = zoneid;
    }

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    public Long getPaynum() {
        return paynum;
    }

    public void setPaynum(Long paynum) {
        this.paynum = paynum;
    }

    public Long getDedunum() {
        return dedunum;
    }

    public void setDedunum(Long dedunum) {
        this.dedunum = dedunum;
    }

    public Double getSumpay() {
        return sumpay;
    }

    public void setSumpay(Double sumpay) {
        this.sumpay = sumpay;
    }

    public Double getSumadvpay() {
        return sumadvpay;
    }

    public void setSumadvpay(Double sumadvpay) {
        this.sumadvpay = sumadvpay;
    }

    public Double getSumprofit() {
        return sumprofit;
    }

    public void setSumprofit(Double sumprofit) {
        this.sumprofit = sumprofit;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClickRate() {
        return clickRate;
    }

    public void setClickRate(String clickRate) {
        this.clickRate = clickRate;
    }

    public String getPayRate() {
        return payRate;
    }

    public void setPayRate(String payRate) {
        this.payRate = payRate;
    }
}
