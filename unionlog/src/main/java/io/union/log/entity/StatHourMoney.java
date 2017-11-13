package io.union.log.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "stat_hour_money")
public class StatHourMoney implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "siteid")
    private Long siteid;

    @Column(name = "planid")
    private Long planid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "hour0")
    private Double hour0 = 0.0D;

    @Column(name = "hour1")
    private Double hour1 = 0.0D;

    @Column(name = "hour2")
    private Double hour2 = 0.0D;

    @Column(name = "hour3")
    private Double hour3 = 0.0D;

    @Column(name = "hour4")
    private Double hour4 = 0.0D;

    @Column(name = "hour5")
    private Double hour5 = 0.0D;

    @Column(name = "hour6")
    private Double hour6 = 0.0D;

    @Column(name = "hour7")
    private Double hour7 = 0.0D;

    @Column(name = "hour8")
    private Double hour8 = 0.0D;

    @Column(name = "hour9")
    private Double hour9 = 0.0D;

    @Column(name = "hour10")
    private Double hour10 = 0.0D;

    @Column(name = "hour11")
    private Double hour11 = 0.0D;

    @Column(name = "hour12")
    private Double hour12 = 0.0D;

    @Column(name = "hour13")
    private Double hour13 = 0.0D;

    @Column(name = "hour14")
    private Double hour14 = 0.0D;

    @Column(name = "hour15")
    private Double hour15 = 0.0D;

    @Column(name = "hour16")
    private Double hour16 = 0.0D;

    @Column(name = "hour17")
    private Double hour17 = 0.0D;

    @Column(name = "hour18")
    private Double hour18 = 0.0D;

    @Column(name = "hour19")
    private Double hour19 = 0.0D;

    @Column(name = "hour20")
    private Double hour20 = 0.0D;

    @Column(name = "hour21")
    private Double hour21 = 0.0D;

    @Column(name = "hour22")
    private Double hour22 = 0.0D;

    @Column(name = "hour23")
    private Double hour23 = 0.0D;

    public StatHourMoney() {
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

    public Long getSiteid() {
        return siteid;
    }

    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }

    public Long getPlanid() {
        return planid;
    }

    public void setPlanid(Long planid) {
        this.planid = planid;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Double getHour0() {
        return hour0;
    }

    public void setHour0(Double hour0) {
        this.hour0 = hour0;
    }

    public Double getHour1() {
        return hour1;
    }

    public void setHour1(Double hour1) {
        this.hour1 = hour1;
    }

    public Double getHour2() {
        return hour2;
    }

    public void setHour2(Double hour2) {
        this.hour2 = hour2;
    }

    public Double getHour3() {
        return hour3;
    }

    public void setHour3(Double hour3) {
        this.hour3 = hour3;
    }

    public Double getHour4() {
        return hour4;
    }

    public void setHour4(Double hour4) {
        this.hour4 = hour4;
    }

    public Double getHour5() {
        return hour5;
    }

    public void setHour5(Double hour5) {
        this.hour5 = hour5;
    }

    public Double getHour6() {
        return hour6;
    }

    public void setHour6(Double hour6) {
        this.hour6 = hour6;
    }

    public Double getHour7() {
        return hour7;
    }

    public void setHour7(Double hour7) {
        this.hour7 = hour7;
    }

    public Double getHour8() {
        return hour8;
    }

    public void setHour8(Double hour8) {
        this.hour8 = hour8;
    }

    public Double getHour9() {
        return hour9;
    }

    public void setHour9(Double hour9) {
        this.hour9 = hour9;
    }

    public Double getHour10() {
        return hour10;
    }

    public void setHour10(Double hour10) {
        this.hour10 = hour10;
    }

    public Double getHour11() {
        return hour11;
    }

    public void setHour11(Double hour11) {
        this.hour11 = hour11;
    }

    public Double getHour12() {
        return hour12;
    }

    public void setHour12(Double hour12) {
        this.hour12 = hour12;
    }

    public Double getHour13() {
        return hour13;
    }

    public void setHour13(Double hour13) {
        this.hour13 = hour13;
    }

    public Double getHour14() {
        return hour14;
    }

    public void setHour14(Double hour14) {
        this.hour14 = hour14;
    }

    public Double getHour15() {
        return hour15;
    }

    public void setHour15(Double hour15) {
        this.hour15 = hour15;
    }

    public Double getHour16() {
        return hour16;
    }

    public void setHour16(Double hour16) {
        this.hour16 = hour16;
    }

    public Double getHour17() {
        return hour17;
    }

    public void setHour17(Double hour17) {
        this.hour17 = hour17;
    }

    public Double getHour18() {
        return hour18;
    }

    public void setHour18(Double hour18) {
        this.hour18 = hour18;
    }

    public Double getHour19() {
        return hour19;
    }

    public void setHour19(Double hour19) {
        this.hour19 = hour19;
    }

    public Double getHour20() {
        return hour20;
    }

    public void setHour20(Double hour20) {
        this.hour20 = hour20;
    }

    public Double getHour21() {
        return hour21;
    }

    public void setHour21(Double hour21) {
        this.hour21 = hour21;
    }

    public Double getHour22() {
        return hour22;
    }

    public void setHour22(Double hour22) {
        this.hour22 = hour22;
    }

    public Double getHour23() {
        return hour23;
    }

    public void setHour23(Double hour23) {
        this.hour23 = hour23;
    }
}
