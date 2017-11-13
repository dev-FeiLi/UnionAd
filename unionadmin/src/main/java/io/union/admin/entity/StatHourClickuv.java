package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/26.
 */
@Entity
@Table(name = "stat_hour_clickuv")
public class StatHourClickuv implements Serializable {

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
    private Long hour0 = 0L;

    @Column(name = "hour1")
    private Long hour1 = 0L;

    @Column(name = "hour2")
    private Long hour2 = 0L;

    @Column(name = "hour3")
    private Long hour3 = 0L;

    @Column(name = "hour4")
    private Long hour4 = 0L;

    @Column(name = "hour5")
    private Long hour5 = 0L;

    @Column(name = "hour6")
    private Long hour6 = 0L;

    @Column(name = "hour7")
    private Long hour7 = 0L;

    @Column(name = "hour8")
    private Long hour8 = 0L;

    @Column(name = "hour9")
    private Long hour9 = 0L;

    @Column(name = "hour10")
    private Long hour10 = 0L;

    @Column(name = "hour11")
    private Long hour11 = 0L;

    @Column(name = "hour12")
    private Long hour12 = 0L;

    @Column(name = "hour13")
    private Long hour13 = 0L;

    @Column(name = "hour14")
    private Long hour14 = 0L;

    @Column(name = "hour15")
    private Long hour15 = 0L;

    @Column(name = "hour16")
    private Long hour16 = 0L;

    @Column(name = "hour17")
    private Long hour17 = 0L;

    @Column(name = "hour18")
    private Long hour18 = 0L;

    @Column(name = "hour19")
    private Long hour19 = 0L;

    @Column(name = "hour20")
    private Long hour20 = 0L;

    @Column(name = "hour21")
    private Long hour21 = 0L;

    @Column(name = "hour22")
    private Long hour22 = 0L;

    @Column(name = "hour23")
    private Long hour23 = 0L;

    public StatHourClickuv() {
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

    public Long getHour0() {
        return hour0;
    }

    public void setHour0(Long hour0) {
        this.hour0 = hour0;
    }

    public Long getHour1() {
        return hour1;
    }

    public void setHour1(Long hour1) {
        this.hour1 = hour1;
    }

    public Long getHour2() {
        return hour2;
    }

    public void setHour2(Long hour2) {
        this.hour2 = hour2;
    }

    public Long getHour3() {
        return hour3;
    }

    public void setHour3(Long hour3) {
        this.hour3 = hour3;
    }

    public Long getHour4() {
        return hour4;
    }

    public void setHour4(Long hour4) {
        this.hour4 = hour4;
    }

    public Long getHour5() {
        return hour5;
    }

    public void setHour5(Long hour5) {
        this.hour5 = hour5;
    }

    public Long getHour6() {
        return hour6;
    }

    public void setHour6(Long hour6) {
        this.hour6 = hour6;
    }

    public Long getHour7() {
        return hour7;
    }

    public void setHour7(Long hour7) {
        this.hour7 = hour7;
    }

    public Long getHour8() {
        return hour8;
    }

    public void setHour8(Long hour8) {
        this.hour8 = hour8;
    }

    public Long getHour9() {
        return hour9;
    }

    public void setHour9(Long hour9) {
        this.hour9 = hour9;
    }

    public Long getHour10() {
        return hour10;
    }

    public void setHour10(Long hour10) {
        this.hour10 = hour10;
    }

    public Long getHour11() {
        return hour11;
    }

    public void setHour11(Long hour11) {
        this.hour11 = hour11;
    }

    public Long getHour12() {
        return hour12;
    }

    public void setHour12(Long hour12) {
        this.hour12 = hour12;
    }

    public Long getHour13() {
        return hour13;
    }

    public void setHour13(Long hour13) {
        this.hour13 = hour13;
    }

    public Long getHour14() {
        return hour14;
    }

    public void setHour14(Long hour14) {
        this.hour14 = hour14;
    }

    public Long getHour15() {
        return hour15;
    }

    public void setHour15(Long hour15) {
        this.hour15 = hour15;
    }

    public Long getHour16() {
        return hour16;
    }

    public void setHour16(Long hour16) {
        this.hour16 = hour16;
    }

    public Long getHour17() {
        return hour17;
    }

    public void setHour17(Long hour17) {
        this.hour17 = hour17;
    }

    public Long getHour18() {
        return hour18;
    }

    public void setHour18(Long hour18) {
        this.hour18 = hour18;
    }

    public Long getHour19() {
        return hour19;
    }

    public void setHour19(Long hour19) {
        this.hour19 = hour19;
    }

    public Long getHour20() {
        return hour20;
    }

    public void setHour20(Long hour20) {
        this.hour20 = hour20;
    }

    public Long getHour21() {
        return hour21;
    }

    public void setHour21(Long hour21) {
        this.hour21 = hour21;
    }

    public Long getHour22() {
        return hour22;
    }

    public void setHour22(Long hour22) {
        this.hour22 = hour22;
    }

    public Long getHour23() {
        return hour23;
    }

    public void setHour23(Long hour23) {
        this.hour23 = hour23;
    }

    public StatHourClickuv(Long hour0, Long hour1, Long hour2, Long hour3, Long hour4, Long hour5, Long hour6, Long hour7, Long hour8, Long hour9, Long hour10, Long hour11, Long hour12, Long hour13, Long hour14, Long hour15, Long hour16, Long hour17, Long hour18, Long hour19, Long hour20, Long hour21, Long hour22, Long hour23) {
        this.hour0 = hour0;
        this.hour1 = hour1;
        this.hour2 = hour2;
        this.hour3 = hour3;
        this.hour4 = hour4;
        this.hour5 = hour5;
        this.hour6 = hour6;
        this.hour7 = hour7;
        this.hour8 = hour8;
        this.hour9 = hour9;
        this.hour10 = hour10;
        this.hour11 = hour11;
        this.hour12 = hour12;
        this.hour13 = hour13;
        this.hour14 = hour14;
        this.hour15 = hour15;
        this.hour16 = hour16;
        this.hour17 = hour17;
        this.hour18 = hour18;
        this.hour19 = hour19;
        this.hour20 = hour20;
        this.hour21 = hour21;
        this.hour22 = hour22;
        this.hour23 = hour23;
    }
}
