package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "log_screen")
public class LogScreen implements Serializable {

    public LogScreen() {
    }

    public LogScreen(String cusScreen, Long cusNum, Long uid, Date addTime) {
        this.cusScreen = cusScreen;
        this.cusNum = cusNum;
        this.uid = uid;
        this.addTime = addTime;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "cus_screen")
    private String cusScreen;

    @Column(name = "cus_num")
    private Long cusNum = 0L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

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

    public String getCusScreen() {
        return cusScreen;
    }

    public void setCusScreen(String cusScreen) {
        this.cusScreen = cusScreen;
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
}
