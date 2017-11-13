package io.union.log.model;

import java.util.Date;

public class UserSettleModel {

    private Long affid;
    private Long advid;
    private Double affprice;
    private Double advprice;
    private Date addTime;
    private Integer utype;

    public UserSettleModel() {
    }

    public Long getAffid() {
        return affid;
    }

    public void setAffid(Long affid) {
        this.affid = affid;
    }

    public Long getAdvid() {
        return advid;
    }

    public void setAdvid(Long advid) {
        this.advid = advid;
    }

    public Double getAffprice() {
        return affprice;
    }

    public void setAffprice(Double affprice) {
        this.affprice = affprice;
    }

    public Double getAdvprice() {
        return advprice;
    }

    public void setAdvprice(Double advprice) {
        this.advprice = advprice;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Integer getUtype() {
        return utype;
    }

    public void setUtype(Integer utype) {
        this.utype = utype;
    }
}
