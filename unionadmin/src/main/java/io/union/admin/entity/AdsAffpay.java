package io.union.admin.entity;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@ExcelTarget("adsAffpay")
@Table(name = "ads_affpay")
public class AdsAffpay implements Serializable {

    public AdsAffpay() {
    }

    public AdsAffpay(double money, double realmoney) {
        this.money = money;
        this.realmoney = realmoney;
    }

    public AdsAffpay(long uid, String username, double money, double realmoney, String payinfo, long manId, String manAccount,
                     Date paytime, Date addTime, int pstatus, int paytype) {
        this.uid = uid;
        this.username = username;
        this.money = money;
        this.realmoney = realmoney;
        this.payinfo = payinfo;
        this.manId = manId;
        this.manAccount = manAccount;
        this.paytime = paytime;
        this.addTime = addTime;
        this.pstatus = pstatus;
        this.paytype = paytype;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Excel(name = "网站主ID", orderNum = "1", mergeVertical = false, isImportField = "uid")
    @Column(name = "uid")
    private Long uid;

    @Excel(name = "网站主帐号", orderNum = "2", width = 25D, mergeVertical = false, isImportField = "username")
    @Column(name = "username")
    private String username;

    @Excel(name = "应付", orderNum = "3", mergeVertical = false, isImportField = "money")
    @Column(name = "money")
    private Double money;

    @Excel(name = "实付", orderNum = "4", mergeVertical = false, isImportField = "realmoney")
    @Column(name = "realmoney")
    private Double realmoney;

    @Excel(name = "说明", orderNum = "11", mergeVertical = false, isImportField = "payinfo")
    @Column(name = "payinfo")
    private String payinfo;

    @Excel(name = "管理员ID", orderNum = "5", mergeVertical = false, isImportField = "manId")
    @Column(name = "man_id")
    private Long manId;

    @Excel(name = "管理员", orderNum = "6", mergeVertical = false, isImportField = "manAccount")
    @Column(name = "man_account")
    private String manAccount;

    @Excel(name = "支付时间", orderNum = "7", width = 24D, mergeVertical = false, isImportField = "paytime")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paytime")
    private Date paytime;

    @Excel(name = "添加时间", orderNum = "8", width = 24D, mergeVertical = false, isImportField = "addTime")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    @Excel(name = "状态", orderNum = "9", mergeVertical = false, replace = {"未支付_0", "已支付_1", "支付失败_2", "拒绝支付_3", "撤销_9"}, isImportField = "pstatus")
    @Column(name = "pstatus")
    private Integer pstatus;

    @Excel(name = "类型", orderNum = "10", mergeVertical = false, replace = {"日结_1", "周结_2", "月结_3"}, isImportField = "pstatus")
    @Column(name = "paytype")
    private Integer paytype;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getRealmoney() {
        return realmoney;
    }

    public void setRealmoney(Double realmoney) {
        this.realmoney = realmoney;
    }

    public String getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(String payinfo) {
        this.payinfo = payinfo;
    }

    public Long getManId() {
        return manId;
    }

    public void setManId(Long manId) {
        this.manId = manId;
    }

    public String getManAccount() {
        return manAccount;
    }

    public void setManAccount(String manAccount) {
        this.manAccount = manAccount;
    }

    public Date getPaytime() {
        return paytime;
    }

    public void setPaytime(Date paytime) {
        this.paytime = paytime;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getPstatus() {
        return pstatus;
    }

    public void setPstatus(Integer pstatus) {
        this.pstatus = pstatus;
    }

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }
}
