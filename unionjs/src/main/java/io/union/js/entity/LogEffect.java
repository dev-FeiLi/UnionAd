package io.union.js.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "log_effect")
public class LogEffect implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "affuid")
    private Long affuid;

    @Column(name = "affname")
    private String affname;

    @Column(name = "advuid")
    private Long advuid;

    @Column(name = "advname")
    private String advname;

    @Column(name = "planid")
    private Long planid;

    @Column(name = "planname")
    private String planname;

    @Column(name = "adid")
    private Long adid;

    @Column(name = "adname")
    private String adname;

    @Column(name = "zoneid")
    private Long zoneid;

    @Column(name = "siteid")
    private Long siteid;

    @Column(name = "clickip")
    private String clickip;

    @Column(name = "sessionid")
    private String sessionid;

    @Column(name = "regid")
    private String regid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_time")
    private Date regTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAffuid() {
        return affuid;
    }

    public void setAffuid(Long affuid) {
        this.affuid = affuid;
    }

    public String getAffname() {
        return affname;
    }

    public void setAffname(String affname) {
        this.affname = affname;
    }

    public Long getAdvuid() {
        return advuid;
    }

    public void setAdvuid(Long advuid) {
        this.advuid = advuid;
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

    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public Long getAdid() {
        return adid;
    }

    public void setAdid(Long adid) {
        this.adid = adid;
    }

    public String getAdname() {
        return adname;
    }

    public void setAdname(String adname) {
        this.adname = adname;
    }

    public Long getZoneid() {
        return zoneid;
    }

    public void setZoneid(Long zoneid) {
        this.zoneid = zoneid;
    }

    public Long getSiteid() {
        return siteid;
    }

    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }

    public String getClickip() {
        return clickip;
    }

    public void setClickip(String clickip) {
        this.clickip = clickip;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public String getRegid() {
        return regid;
    }

    public void setRegid(String regid) {
        this.regid = regid;
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
