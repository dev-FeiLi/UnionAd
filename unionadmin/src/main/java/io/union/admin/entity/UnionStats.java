package io.union.admin.entity;

import org.jeecgframework.poi.excel.annotation.Excel;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "union_stats")
public class UnionStats implements Serializable {

    @Excel(name = "名称", orderNum = "2", width = 24D, mergeVertical = false)
    @Transient
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Excel(name = "展现", orderNum = "3", width = 10D, mergeVertical = false)
    @Column(name = "views")
    private Long views = 0L;

    @Excel(name = "点击", orderNum = "4", width = 10D, mergeVertical = false)
    @Column(name = "clicks")
    private Long clicks = 0L;

    @Excel(name = "排重", orderNum = "5", width = 10D, mergeVertical = false)
    @Column(name = "clickip")
    private Long clickip = 0L;

    @Excel(name = "点击率", orderNum = "6", width = 10D, mergeVertical = false)
    @Transient
    private String clickRate;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "planid")
    private Long planid;

    @Column(name = "adid")
    private Long adid;

    @Column(name = "advid")
    private Long advid;

    @Column(name = "siteid")
    private Long siteid;

    @Column(name = "zoneid")
    private Long zoneid;

    @Column(name = "paytype")
    private Integer paytype;

    @Excel(name = "结算", orderNum = "7", width = 15D, mergeVertical = false)
    @Column(name = "paynum")
    private Long paynum = 0L;

    @Excel(name = "扣量", orderNum = "8", width = 15D, mergeVertical = false)
    @Column(name = "dedunum")
    private Long dedunum = 0L;

    @Excel(name = "结算率", orderNum = "9", width = 10D, mergeVertical = false)
    @Transient
    private String payRate;

    @Excel(name = "应付", orderNum = "10", width = 15D, mergeVertical = false)
    @Column(name = "sumpay")
    private Double sumpay = 0.0;

    @Excel(name = "应收", orderNum = "11", width = 15D, mergeVertical = false)
    @Column(name = "sumadvpay")
    private Double sumadvpay = 0.0;

    @Excel(name = "盈利", orderNum = "12", width = 15D, mergeVertical = false)
    @Column(name = "sumprofit")
    private Double sumprofit = 0.0;

    @Excel(name = "日期", orderNum = "1", width = 20D, exportFormat = "yyyy-MM-dd", mergeVertical = false)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    public UnionStats() {
    }

    public UnionStats(Long paynum, Double sumpay, Double sumadvpay, Double sumprofit, Date addTime) {
        this.paynum = paynum;
        this.sumpay = sumpay;
        this.sumadvpay = sumadvpay;
        this.sumprofit = sumprofit;
        this.addTime = addTime;
    }

    public UnionStats(Long views, Long clicks, Long clickip, Long paynum, Long dedunum, Double sumpay, Double sumadvpay, Double sumprofit) {
        this.views = views;
        this.clicks = clicks;
        this.clickip = clickip;
        this.paynum = paynum;
        this.dedunum = dedunum;
        this.sumpay = sumpay;
        this.sumadvpay = sumadvpay;
        this.sumprofit = sumprofit;
    }

    public UnionStats(Date addTime, Long id, Long views, Long clicks, Long clickip, Long paynum, Long dedunum, Double sumpay, Double sumadvpay, Double sumprofit) {
        this.addTime = addTime;
        this.id = id;
        this.views = views;
        this.clicks = clicks;
        this.clickip = clickip;
        this.paynum = paynum;
        this.dedunum = dedunum;
        this.sumpay = sumpay;
        this.sumadvpay = sumadvpay;
        this.sumprofit = sumprofit;
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
