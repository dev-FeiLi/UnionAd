package io.union.log.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/8.
 */
@Entity
@Table(name = "ads_ad")
public class AdsAd implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adid", unique = true, nullable = false)
    private Long adid;

    @Column(name = "adname")
    private String adname;

    @Column(name = "planid")
    private Long planid;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "adtype", length = 1)
    private Integer adtype;

    @Column(name = "imageurl")
    private String imageurl;

    @Column(name = "adurl")
    private String adurl;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "astatus", length = 1)
    private Integer astatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addtime;

    @Column(name = "limitdiv")
    private Integer limitdiv;

    @Column(name = "limitdivheight")
    private Integer limitdivheight;

    @Column(name = "limitpop", length = 1)
    private Integer limitpop;

    @Column(name = "deduction", length = 3)
    private Integer deduction;

    @Column(name = "adeffect")
    private Integer adeffect = 0;

    @Column(name = "data_type")
    private Integer dataType = 0;

    public AdsAd() {
    }

    public Long getAdid() {
        return adid;
    }

    public void setAdid(Long adid) {
        this.adid = adid;
    }

    public String getAdname() {
        return adname;
    }

    public void setAdname(String adname) {
        this.adname = adname;
    }

    public Long getPlanid() {
        return planid;
    }

    public void setPlanid(Long planid) {
        this.planid = planid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Integer getAdtype() {
        return adtype;
    }

    public void setAdtype(Integer adtype) {
        this.adtype = adtype;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getAdurl() {
        return adurl;
    }

    public void setAdurl(String adurl) {
        this.adurl = adurl;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getAstatus() {
        return astatus;
    }

    public void setAstatus(Integer astatus) {
        this.astatus = astatus;
    }

    public Date getAddtime() {
        return addtime;
    }

    public void setAddtime(Date addtime) {
        this.addtime = addtime;
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

    public Integer getDeduction() {
        return deduction;
    }

    public void setDeduction(Integer deduction) {
        this.deduction = deduction;
    }

    public Integer getAdeffect() {
        return adeffect;
    }

    public void setAdeffect(Integer adeffect) {
        this.adeffect = adeffect;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }
}
