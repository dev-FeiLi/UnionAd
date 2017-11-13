package io.union.log.model;

public class UserProfitModel {

    private Long uid;
    private Integer utype;
    private Double money = 0.0;
    private Double weekmoney = 0.0;
    private Double monthmoney = 0.0;
    private Double xmoney = 0.0;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getUtype() {
        return utype;
    }

    public void setUtype(Integer utype) {
        this.utype = utype;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
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
}
