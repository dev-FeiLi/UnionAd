package io.union.wap.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/10.
 */
@Entity
@Table(name = "ads_plan")
public class AdsPlan implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planid", unique = true, nullable = false)
    private Long planid;

    @Column(name = "title")
    private String title;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "username")
    private String username;

    @Column(name = "price")
    private String price;

    @Column(name = "usertotal")
    private Integer usertotal;

    @Column(name = "limitmoney")
    private Double limitmoney;

    @Column(name = "paytype")
    private Integer paytype;

    @Column(name = "isaudit")
    private String isaudit;

    @Column(name = "deduction")
    private Integer deduction;

    @Column(name = "limitarea")
    private String limitarea;

    @Column(name = "limittime")
    private String limittime;

    @Column(name = "limitdevice")
    private String limitdevice;

    @Column(name = "limituid")
    private String limituid;

    @Column(name = "limitsite")
    private String limitsite;

    @Column(name = "limitpop")
    private Integer limitpop;

    @Column(name = "limitdiv")
    private Integer limitdiv;

    @Column(name = "limitdivheight")
    private Integer limitdivheight;

    @Column(name = "limittype")
    private String limittype;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "starttime")
    private Date starttime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "stoptime")
    private Date stoptime;

    @Column(name = "pstatus")
    private Integer pstatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "ads_url")
    private String adsUrl;

    @Column(name = "priority")
    private Integer priority = 5;

    @Column(name = "isnotify")
    private String isnotify = "N";

    @Column(name = "click_rate")
    private Double clickRate;

    public AdsPlan() {
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

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getUsertotal() {
        return usertotal;
    }

    public void setUsertotal(Integer usertotal) {
        this.usertotal = usertotal;
    }

    public Double getLimitmoney() {
        return limitmoney;
    }

    public void setLimitmoney(Double limitmoney) {
        this.limitmoney = limitmoney;
    }

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    public String getIsaudit() {
        return isaudit;
    }

    public void setIsaudit(String isaudit) {
        this.isaudit = isaudit;
    }

    public Integer getDeduction() {
        return deduction;
    }

    public void setDeduction(Integer deduction) {
        this.deduction = deduction;
    }

    public String getLimitarea() {
        return limitarea;
    }

    public void setLimitarea(String limitarea) {
        this.limitarea = limitarea;
    }

    public String getLimittime() {
        return limittime;
    }

    public void setLimittime(String limittime) {
        this.limittime = limittime;
    }

    public String getLimitdevice() {
        return limitdevice;
    }

    public void setLimitdevice(String limitdevice) {
        this.limitdevice = limitdevice;
    }

    public String getLimituid() {
        return limituid;
    }

    public void setLimituid(String limituid) {
        this.limituid = limituid;
    }

    public String getLimitsite() {
        return limitsite;
    }

    public void setLimitsite(String limitsite) {
        this.limitsite = limitsite;
    }

    public Integer getLimitpop() {
        return limitpop;
    }

    public void setLimitpop(Integer limitpop) {
        this.limitpop = limitpop;
    }

    public Integer getLimitdiv() {
        return limitdiv;
    }

    public void setLimitdiv(Integer limitdiv) {
        this.limitdiv = limitdiv;
    }

    public Integer getLimitdivheight() {
        return limitdivheight;
    }

    public void setLimitdivheight(Integer limitdivheight) {
        this.limitdivheight = limitdivheight;
    }

    public String getLimittype() {
        return limittype;
    }

    public void setLimittype(String limittype) {
        this.limittype = limittype;
    }

    public Date getStarttime() {
        return starttime;
    }

    public void setStarttime(Date starttime) {
        this.starttime = starttime;
    }

    public Date getStoptime() {
        return stoptime;
    }

    public void setStoptime(Date stoptime) {
        this.stoptime = stoptime;
    }

    public Integer getPstatus() {
        return pstatus;
    }

    public void setPstatus(Integer pstatus) {
        this.pstatus = pstatus;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getAdsUrl() {
        return adsUrl;
    }

    public void setAdsUrl(String adsUrl) {
        this.adsUrl = adsUrl;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getIsnotify() {
        return isnotify;
    }

    public void setIsnotify(String isnotify) {
        this.isnotify = isnotify;
    }

    public Double getClickRate() {
        return clickRate;
    }

    public void setClickRate(Double clickRate) {
        this.clickRate = clickRate;
    }
}
