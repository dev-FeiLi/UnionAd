package io.union.wap.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "union_stats")
public class UnionStats implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "views")
    private Long views = 0L;

    @Column(name = "clicks")
    private Long clicks = 0L;

    @Column(name = "clickip")
    private Long clickip = 0L;

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

    @Column(name = "paynum")
    private Long paynum = 0L;

    @Column(name = "dedunum")
    private Long dedunum = 0L;

    @Column(name = "sumpay")
    private Double sumpay = 0.0;

    @Column(name = "sumadvpay")
    private Double sumadvpay = 0.0;

    @Column(name = "sumprofit")
    private Double sumprofit = 0.0;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    public UnionStats() {
    }

    public UnionStats(Date addTime, Integer paytype, Double sumpay) {
        this.addTime = addTime;
        this.paytype = paytype;
        this.sumpay = sumpay;
    }

    public UnionStats(Date addTime, Long planid, Long views, Long clicks, Long clickip, Long paynum, Long dedunum, Double sumadvpay) {
        this.addTime = addTime;
        this.planid = planid;
        this.views = views;
        this.clicks = clicks;
        this.clickip = clickip;
        this.paynum = paynum;
        this.dedunum = dedunum;
        this.sumadvpay = sumadvpay;
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
}
