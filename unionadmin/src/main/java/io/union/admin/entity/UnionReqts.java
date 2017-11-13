package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "union_reqts")
public class UnionReqts implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "zoneid")
    private Long zoneid;

    @Column(name = "requests")
    private Long reqeusts = 0L;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    public UnionReqts() {
    }

    public UnionReqts(Date addTime, Long reqeusts) {
        this.addTime = addTime;
        this.reqeusts = reqeusts;
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

    public Long getZoneid() {
        return zoneid;
    }

    public void setZoneid(Long zoneid) {
        this.zoneid = zoneid;
    }

    public Long getReqeusts() {
        return reqeusts;
    }

    public void setReqeusts(Long reqeusts) {
        this.reqeusts = reqeusts;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
