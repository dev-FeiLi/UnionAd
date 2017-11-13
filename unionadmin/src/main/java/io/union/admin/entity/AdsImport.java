package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by win10 on 2017/9/5.
 */
@Entity
@Table(name = "ads_import")
public class AdsImport implements Serializable {
    public AdsImport(Long id, Long uid, String affname, Long advid, String advname, Long planid, String title, Long affnum, Long advnum, Double affprice, Double advprice, Double sumpay, Double sumadvpay, Double sumprofit, Date statDate, Date addTime, Integer istatus, String data) {
        this.id = id;
        this.uid = uid;
        this.affname = affname;
        this.advid = advid;
        this.advname = advname;
        this.planid = planid;
        this.title = title;
        this.affnum = affnum;
        this.advnum = advnum;
        this.affprice = affprice;
        this.advprice = advprice;
        this.sumpay = sumpay;
        this.sumadvpay = sumadvpay;
        this.sumprofit = sumprofit;
        this.statDate = statDate;
        this.addTime = addTime;
        this.istatus = istatus;
        this.data = data;
    }

    public AdsImport() {

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "affname")
    private String affname;

    @Column(name = "advid")
    private Long advid;

    @Column(name = "advname")
    private String advname;

    @Column(name = "planid")
    private Long planid;

    @Column(name = "title")
    private String title;

    @Column(name = "affnum")
    private Long affnum;

    @Column(name = "advnum")
    private Long advnum;

    @Column(name = "affprice")
    private Double affprice;

    @Column(name = "advprice")
    private Double advprice;

    @Column(name = "sumpay")
    private Double sumpay;

    @Column(name = "sumadvpay")
    private Double sumadvpay;

    @Column(name = "sumprofit")
    private Double sumprofit;

    @Column(name = "stat_date")
    private Date statDate;

    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "istatus")
    private Integer istatus;

    @Column(name = "data")
    private String data;
    @Column(name = "statsid")
    private Long statsid;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getAffname() {
        return affname;
    }

    public void setAffname(String affname) {
        this.affname = affname;
    }

    public Long getAdvid() {
        return advid;
    }

    public void setAdvid(Long advid) {
        this.advid = advid;
    }

    public String getAdvname() {
        return advname;
    }

    public void setAdvname(String advname) {
        this.advname = advname;
    }

    public Long getPlanid() {
        return planid;
    }

    public void setPlanid(Long planid) {
        this.planid = planid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getAffnum() {
        return affnum;
    }

    public void setAffnum(Long affnum) {
        this.affnum = affnum;
    }

    public Long getAdvnum() {
        return advnum;
    }

    public void setAdvnum(Long advnum) {
        this.advnum = advnum;
    }

    public Double getAffprice() {
        return affprice;
    }

    public void setAffprice(Double affprice) {
        this.affprice = affprice;
    }

    public Double getAdvprice() {
        return advprice;
    }

    public void setAdvprice(Double advprice) {
        this.advprice = advprice;
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

    public Date getStatDate() {
        return statDate;
    }

    public void setStatDate(Date statDate) {
        this.statDate = statDate;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStatsid() {
        return statsid;
    }

    public void setStatsid(Long statsid) {
        this.statsid = statsid;
    }
}
