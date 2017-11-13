package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "log_os")
public class LogOs implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "cus_os")
    private String cusOs;

    @Column(name = "cus_num")
    private Long cusNum = 0L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    public LogOs() {
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

    public String getCusOs() {
        return cusOs;
    }

    public void setCusOs(String cusOs) {
        this.cusOs = cusOs;
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
