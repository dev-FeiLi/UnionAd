package io.union.wap.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/10.
 */
@Entity
@Table(name = "ads_user")
public class AdsUser implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", nullable = false, unique = true)
    private Long uid;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "utype")
    private Integer utype;

    @Column(name = "qq")
    private String qq;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "idcard")
    private String idcard;

    @Column(name = "realname")
    private String realname;

    @Column(name = "bankname")
    private String bankname;

    @Column(name = "bankbranch")
    private String bankbranch;

    @Column(name = "bankaccount")
    private String bankaccount;

    @Column(name = "banknum")
    private String banknum;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "regtime")
    private Date regtime;

    @Column(name = "regip")
    private String regip;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "logintime")
    private Date logintime;

    @Column(name = "loginip")
    private String loginip;

    @Column(name = "deduction")
    private String deduction;

    @Column(name = "money")
    private Double money;

    @Column(name = "daymoney")
    private Double daymoney;

    @Column(name = "weekmoney")
    private Double weekmoney;

    @Column(name = "monthmoney")
    private Double monthmoney;

    @Column(name = "xmoney")
    private Double xmoney;

    @Column(name = "serviceid")
    private Long serviceid;

    @Column(name = "limiturl")
    private Integer limiturl;

    @Column(name = "limitdiv")
    private Integer limitdiv;

    @Column(name = "limitdivheight")
    private Integer limitdivheight;

    @Column(name = "limitpop")
    private Integer limitpop;

    @Column(name = "limitplan")
    private String limitplan;

    @Column(name = "runtype")
    private Integer runtype;

    @Column(name = "ustatus")
    private Integer ustatus;

    @Column(name = "memo")
    private String memo;

    @Column(name = "paytype")
    private Integer paytype;

    @Column(name = "chartype")
    private Integer chartype;

    public AdsUser() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUtype() {
        return utype;
    }

    public void setUtype(Integer utype) {
        this.utype = utype;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
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

    public String getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(String bankaccount) {
        this.bankaccount = bankaccount;
    }

    public String getBanknum() {
        return banknum;
    }

    public void setBanknum(String banknum) {
        this.banknum = banknum;
    }

    public Date getRegtime() {
        return regtime;
    }

    public void setRegtime(Date regtime) {
        this.regtime = regtime;
    }

    public String getRegip() {
        return regip;
    }

    public void setRegip(String regip) {
        this.regip = regip;
    }

    public Date getLogintime() {
        return logintime;
    }

    public void setLogintime(Date logintime) {
        this.logintime = logintime;
    }

    public String getLoginip() {
        return loginip;
    }

    public void setLoginip(String loginip) {
        this.loginip = loginip;
    }

    public String getDeduction() {
        return deduction;
    }

    public void setDeduction(String deduction) {
        this.deduction = deduction;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public Double getDaymoney() {
        return daymoney;
    }

    public void setDaymoney(Double daymoney) {
        this.daymoney = daymoney;
    }

    public Double getWeekmoney() {
        return weekmoney;
    }

    public void setWeekmoney(Double weekmoney) {
        this.weekmoney = weekmoney;
    }

    public Double getMonthmoney() {
        return monthmoney;
    }

    public void setMonthmoney(Double monthmoney) {
        this.monthmoney = monthmoney;
    }

    public Double getXmoney() {
        return xmoney;
    }

    public void setXmoney(Double xmoney) {
        this.xmoney = xmoney;
    }

    public Long getServiceid() {
        return serviceid;
    }

    public void setServiceid(Long serviceid) {
        this.serviceid = serviceid;
    }

    public Integer getLimiturl() {
        return limiturl;
    }

    public void setLimiturl(Integer limiturl) {
        this.limiturl = limiturl;
    }

    public Integer getLimitdiv() {
        return limitdiv;
    }

    public void setLimitdiv(Integer limitdiv) {
        this.limitdiv = limitdiv;
    }

    public Integer getLimitdivheight() {
        return limitdivheight;
    }

    public void setLimitdivheight(Integer limitdivheight) {
        this.limitdivheight = limitdivheight;
    }

    public Integer getLimitpop() {
        return limitpop;
    }

    public void setLimitpop(Integer limitpop) {
        this.limitpop = limitpop;
    }

    public String getLimitplan() {
        return limitplan;
    }

    public void setLimitplan(String limitplan) {
        this.limitplan = limitplan;
    }

    public Integer getRuntype() {
        return runtype;
    }

    public void setRuntype(Integer runtype) {
        this.runtype = runtype;
    }

    public Integer getUstatus() {
        return ustatus;
    }

    public void setUstatus(Integer ustatus) {
        this.ustatus = ustatus;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    public Integer getChartype() {
        return chartype;
    }

    public void setChartype(Integer chartype) {
        this.chartype = chartype;
    }
}
