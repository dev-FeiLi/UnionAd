package io.union.admin.entity;

import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecgframework.poi.excel.annotation.ExcelTarget;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@ExcelTarget("adsAdvpay")
@Table(name = "ads_advpay")
public class AdsAdvpay implements Serializable {

    public AdsAdvpay(Date addTime, long uid, String username, double money, int paytype, String tocard, long manId, String manAccount, String payinfo) {
        this.addTime = addTime;
        this.uid = uid;
        this.username = username;
        this.money = money;
        this.paytype = paytype;
        this.tocard = tocard;
        this.manId = manId;
        this.manAccount = manAccount;
        this.payinfo = payinfo;
    }

    public AdsAdvpay() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Excel(name = "用户ID", orderNum = "1", mergeVertical = false, isImportField = "uid")
    @Column(name = "uid")
    private Long uid;

    @Excel(name = "广告主", orderNum = "2", width = 25D, mergeVertical = false, isImportField = "username")
    @Column(name = "username")
    private String username;

    @Excel(name = "金额", orderNum = "3", mergeVertical = false, isImportField = "money")
    @Column(name = "money")
    private Double money;

    @Excel(name = "支付类型", orderNum = "4", mergeVertical = false, replace = {"增加_1", "扣除_2"}, isImportField = "paytype")
    @Column(name = "paytype")
    private Integer paytype;

    @Excel(name = "收款流向", orderNum = "5", width = 55D, mergeVertical = false, isImportField = "tocard")
    @Column(name = "tocard")
    private String tocard;

    @Excel(name = "管理员", orderNum = "6", mergeVertical = false, isImportField = "manId")
    @Column(name = "man_id")
    private Long manId;

    @Excel(name = "管理员账号", orderNum = "7", mergeVertical = false, isImportField = "manAccount")
    @Column(name = "man_account")
    private String manAccount;

    @Excel(name = "说明", orderNum = "9", width = 30D, mergeVertical = false, isImportField = "payinfo")
    @Column(name = "payinfo")
    private String payinfo;

    @Excel(name = "添加时间", orderNum = "8", width = 24D, mergeVertical = false, isImportField = "addTime")
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

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    public String getTocard() {
        return tocard;
    }

    public void setTocard(String tocard) {
        this.tocard = tocard;
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

    public String getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(String payinfo) {
        this.payinfo = payinfo;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }
}
