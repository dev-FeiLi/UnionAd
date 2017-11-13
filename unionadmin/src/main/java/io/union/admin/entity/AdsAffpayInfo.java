package io.union.admin.entity;

import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Date;

/**
 * Created by Shell Li on 2017/9/4.
 */
public class AdsAffpayInfo {
    @Excel(name = "网站主ID", orderNum = "1", mergeVertical = false, isImportField = "uid")
    private Long uid;

    @Excel(name = "网站主帐号", orderNum = "2", width = 25D, mergeVertical = false, isImportField = "username")
    private String username;

    @Excel(name = "收款银行", orderNum = "3", width = 20D)
    private String bankname;

    @Excel(name = "收款银行开户地址", orderNum = "4", width = 20D)
    private String bankbranch;

    @Excel(name = "收款账号", orderNum = "5", width = 35D)
    private String banknum;

    @Excel(name = "收款人", orderNum = "6")
    private String bankaccount;

    //    @Excel(name = "应付", orderNum = "7", mergeVertical = false, isImportField = "money")
    private Double money;

    @Excel(name = "实付", orderNum = "7", mergeVertical = false, isImportField = "realmoney")
    private Double realmoney;

    //    @Excel(name = "管理员", orderNum = "9", mergeVertical = false, isImportField = "manAccount")
    private String manAccount;

    @Excel(name = "支付时间", orderNum = "8", width = 24D, mergeVertical = false, isImportField = "paytime")
    private String paytime;

    //    @Excel(name = "添加时间", orderNum = "11", width = 24D, mergeVertical = false, isImportField = "addTime")
    private Date addTime;

    @Excel(name = "状态", orderNum = "9", mergeVertical = false, replace = {"未支付_0", "已支付_1", "支付失败_2", "拒绝支付_3", "撤销_9"}, isImportField = "pstatus")
    private Integer pstatus;

    @Excel(name = "类型", orderNum = "10", mergeVertical = false, replace = {"日结_1", "周结_2", "月结_3"}, isImportField = "pstatus")
    private Integer paytype;

    //    @Excel(name = "说明", orderNum = "14", mergeVertical = false, isImportField = "payinfo")
    private String payinfo;

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

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getBankbranch() {
        return bankbranch;
    }

    public void setBankbranch(String bankbranch) {
        this.bankbranch = bankbranch;
    }

    public String getBanknum() {
        return banknum;
    }

    public void setBanknum(String banknum) {
        this.banknum = banknum;
    }

    public String getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(String bankaccount) {
        this.bankaccount = bankaccount;
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

    public String getManAccount() {
        return manAccount;
    }

    public void setManAccount(String manAccount) {
        this.manAccount = manAccount;
    }

    public String getPaytime() {
        return paytime;
    }

    public void setPaytime(String paytime) {
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

    public String getPayinfo() {
        return payinfo;
    }

    public void setPayinfo(String payinfo) {
        this.payinfo = payinfo;
    }
}
