package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/17.
 */
@Entity
@Table(name = "ads_site")
public class AdsSite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "siteid", unique = true, nullable = false)
    private Long siteid;

    @Column(name = "sitename")
    private String sitename;

    @Column(name = "siteurl")
    private String siteurl;

    @Column(name = "sitetype")
    private Integer sitetype;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "dayip")
    private Long dayip;

    @Column(name = "daypv")
    private Long daypv;

    @Column(name = "beian")
    private String beian;

    @Column(name = "sstatus")
    private Integer sstatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    public Long getSiteid() {
        return siteid;
    }

    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }

    public String getSitename() {
        return sitename;
    }

    public void setSitename(String sitename) {
        this.sitename = sitename;
    }

    public String getSiteurl() {
        return siteurl;
    }

    public void setSiteurl(String siteurl) {
        this.siteurl = siteurl;
    }

    public Integer getSitetype() {
        return sitetype;
    }

    public void setSitetype(Integer sitetype) {
        this.sitetype = sitetype;
    }

    public Long getDayip() {
        return dayip;
    }

    public void setDayip(Long dayip) {
        this.dayip = dayip;
    }

    public Long getDaypv() {
        return daypv;
    }

    public void setDaypv(Long daypv) {
        this.daypv = daypv;
    }

    public String getBeian() {
        return beian;
    }

    public void setBeian(String beian) {
        this.beian = beian;
    }

    public Integer getSstatus() {
        return sstatus;
    }

    public void setSstatus(Integer sstatus) {
        this.sstatus = sstatus;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
