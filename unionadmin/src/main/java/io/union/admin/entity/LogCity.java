package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "log_city")
public class LogCity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "cus_province")
    private String cusProvince;

    @Column(name = "cus_city")
    private String cusCity;

    @Column(name = "cus_isp")
    private String cusIsp;

    @Column(name = "cus_num")
    private Long cusNum = 0L;

    @Column(name = "add_time")
    private Date addTime;

    @Transient
    private String cityInfo;

    public LogCity() {
    }

    public LogCity(String cusProvince, String cusCity, Long cusNum, Date addTime) {
        this.cusProvince = cusProvince;
        this.cusCity = cusCity;
        this.cusNum = cusNum;
        this.addTime = addTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getCusProvince() {
        return cusProvince;
    }

    public void setCusProvince(String cusProvince) {
        this.cusProvince = cusProvince;
    }

    public String getCusCity() {
        return cusCity;
    }

    public void setCusCity(String cusCity) {
        this.cusCity = cusCity;
    }

    public String getCusIsp() {
        return cusIsp;
    }

    public void setCusIsp(String cusIsp) {
        this.cusIsp = cusIsp;
    }

    public Long getCusNum() {
        return cusNum;
    }

    public void setCusNum(Long cusNum) {
        this.cusNum = cusNum;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public String getCityInfo() {
        return cityInfo;
    }

    public void setCityInfo(String cityInfo) {
        this.cityInfo = cityInfo;
    }
}
