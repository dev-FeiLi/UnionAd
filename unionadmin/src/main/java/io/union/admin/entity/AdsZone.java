package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/10.
 */
@Entity
@Table(name = "ads_zone")
public class AdsZone implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zoneid", nullable = false, unique = true)
    private Long zoneid;

    @Column(name = "zonename")
    private String zonename;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "paytype")
    private Integer paytype;

    @Column(name = "viewtype")
    private Integer viewtype;

    @Column(name = "viewadids")
    private String viewadids;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "zstatus")
    private Integer zstatus;

    @Column(name = "hcontrol")
    private String hcontrol;

    @Column(name = "jdomain")
    private String jdomain;

    @Column(name = "idomain")
    private String idomain;

    @Column(name = "description")
    private String description;

    @Column(name = "viewname")
    private String viewname;

    public AdsZone() {
    }

    public Long getZoneid() {
        return zoneid;
    }

    public void setZoneid(Long zoneid) {
        this.zoneid = zoneid;
    }

    public String getZonename() {
        return zonename;
    }

    public void setZonename(String zonename) {
        this.zonename = zonename;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    public Integer getViewtype() {
        return viewtype;
    }

    public void setViewtype(Integer viewtype) {
        this.viewtype = viewtype;
    }

    public String getViewadids() {
        return viewadids;
    }

    public void setViewadids(String viewadids) {
        this.viewadids = viewadids;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getZstatus() {
        return zstatus;
    }

    public void setZstatus(Integer zstatus) {
        this.zstatus = zstatus;
    }

    public String getHcontrol() {
        return hcontrol;
    }

    public void setHcontrol(String hcontrol) {
        this.hcontrol = hcontrol;
    }

    public String getJdomain() {
        return jdomain;
    }

    public void setJdomain(String jdomain) {
        this.jdomain = jdomain;
    }

    public String getIdomain() {
        return idomain;
    }

    public void setIdomain(String idomain) {
        this.idomain = idomain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getViewname() {
        return viewname;
    }

    public void setViewname(String viewname) {
        this.viewname = viewname;
    }
}
