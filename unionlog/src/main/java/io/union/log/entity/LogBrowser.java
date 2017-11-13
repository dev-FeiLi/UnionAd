package io.union.log.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "log_browser")
public class LogBrowser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "brw_name")
    private String brwName;

    @Column(name = "brw_version")
    private String brwVersion;

    @Column(name = "brw_plat")
    private String brwPlat;

    @Column(name = "brw_num")
    private Long brwNum = 0L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    public LogBrowser() {
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

    public String getBrwName() {
        return brwName;
    }

    public void setBrwName(String brwName) {
        this.brwName = brwName;
    }

    public String getBrwVersion() {
        return brwVersion;
    }

    public void setBrwVersion(String brwVersion) {
        this.brwVersion = brwVersion;
    }

    public String getBrwPlat() {
        return brwPlat;
    }

    public void setBrwPlat(String brwPlat) {
        this.brwPlat = brwPlat;
    }

    public Long getBrwNum() {
        return brwNum;
    }

    public void setBrwNum(Long brwNum) {
        this.brwNum = brwNum;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
