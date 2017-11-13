package io.union.js.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/7/10.
 */
@Entity
@Table(name = "log_visit")
public class LogVisit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true)
    private Long id;

    @Column(name = "planid")
    private Long planid;

    @Column(name = "siteid")
    private Long siteid;

    @Column(name = "uid")
    private Long uid;

    @Column(name = "adid")
    private Long adid;

    @Column(name = "zoneid")
    private Long zoneid;

    @Column(name = "paytype")
    private Integer paytype;

    @Column(name = "referer_url")
    private String refererUrl;

    @Column(name = "site_page")
    private String sitePage;

    @Column(name = "page_title")
    private String pageTitle;

    @Column(name = "brw_name")
    private String brwName;

    @Column(name = "brw_version")
    private String brwVersion;

    @Column(name = "brw_ua")
    private String brwUa;

    @Column(name = "cus_os")
    private String cusOs;

    @Column(name = "cus_screen")
    private String cusScreen;

    @Column(name = "cus_flash")
    private String cusFlash;

    @Column(name = "cus_java")
    private String cusJava;

    @Column(name = "cus_cookie")
    private String cusCookie;

    @Column(name = "cus_ip")
    private String cusIp;

    @Column(name = "cus_province")
    private String cusProvince;

    @Column(name = "cus_city")
    private String cusCity;

    @Column(name = "cus_isp")
    private String cusIsp;

    @Column(name = "deduction")
    private String deduction;

    @Column(name = "jsessionid")
    private String jsessionid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;

    @Column(name = "price")
    private Double price;

    @Column(name = "advprice")
    private Double advprice;

    @Column(name = "device")
    private Integer device;

    @Column(name = "pvnum")
    private Long pvnum;

    @Column(name = "uvnum")
    private Long uvnum;

    @Column(name = "urlnum")
    private Long urlnum;

    @Column(name = "uvpvnum")
    private Long uvpvnum;

    @Column(name = "uvipnum")
    private Long uvipnum;

    @Column(name = "uvurlnum")
    private Long uvurlnum;

    @Column(name = "doubt")
    private String doubt;

    public LogVisit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPlanid() {
        return planid;
    }

    public void setPlanid(Long planid) {
        this.planid = planid;
    }

    public Long getSiteid() {
        return siteid;
    }

    public void setSiteid(Long siteid) {
        this.siteid = siteid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getAdid() {
        return adid;
    }

    public void setAdid(Long adid) {
        this.adid = adid;
    }

    public Long getZoneid() {
        return zoneid;
    }

    public void setZoneid(Long zoneid) {
        this.zoneid = zoneid;
    }

    public Integer getPaytype() {
        return paytype;
    }

    public void setPaytype(Integer paytype) {
        this.paytype = paytype;
    }

    public String getRefererUrl() {
        return refererUrl;
    }

    public void setRefererUrl(String refererUrl) {
        this.refererUrl = refererUrl;
    }

    public String getSitePage() {
        return sitePage;
    }

    public void setSitePage(String sitePage) {
        this.sitePage = sitePage;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    public String getBrwName() {
        return brwName;
    }

    public void setBrwName(String brwName) {
        this.brwName = brwName;
    }

    public String getBrwVersion() {
        return brwVersion;
    }

    public void setBrwVersion(String brwVersion) {
        this.brwVersion = brwVersion;
    }

    public String getBrwUa() {
        return brwUa;
    }

    public void setBrwUa(String brwUa) {
        this.brwUa = brwUa;
    }

    public String getCusOs() {
        return cusOs;
    }

    public void setCusOs(String cusOs) {
        this.cusOs = cusOs;
    }

    public String getCusScreen() {
        return cusScreen;
    }

    public void setCusScreen(String cusScreen) {
        this.cusScreen = cusScreen;
    }

    public String getCusFlash() {
        return cusFlash;
    }

    public void setCusFlash(String cusFlash) {
        this.cusFlash = cusFlash;
    }

    public String getCusJava() {
        return cusJava;
    }

    public void setCusJava(String cusJava) {
        this.cusJava = cusJava;
    }

    public String getCusCookie() {
        return cusCookie;
    }

    public void setCusCookie(String cusCookie) {
        this.cusCookie = cusCookie;
    }

    public String getCusIp() {
        return cusIp;
    }

    public void setCusIp(String cusIp) {
        this.cusIp = cusIp;
    }

    public String getCusProvince() {
        return cusProvince;
    }

    public void setCusProvince(String cusProvince) {
        this.cusProvince = cusProvince;
    }

    public String getCusCity() {
        return cusCity;
    }

    public void setCusCity(String cusCity) {
        this.cusCity = cusCity;
    }

    public String getCusIsp() {
        return cusIsp;
    }

    public void setCusIsp(String cusIsp) {
        this.cusIsp = cusIsp;
    }

    public String getDeduction() {
        return deduction;
    }

    public void setDeduction(String deduction) {
        this.deduction = deduction;
    }

    public String getJsessionid() {
        return jsessionid;
    }

    public void setJsessionid(String jsessionid) {
        this.jsessionid = jsessionid;
    }

    public Date getAddTime() {
        return addTime;
    }

    public void setAddTime(Date addTime) {
        this.addTime = addTime;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAdvprice() {
        return advprice;
    }

    public void setAdvprice(Double advprice) {
        this.advprice = advprice;
    }

    public Integer getDevice() {
        return device;
    }

    public void setDevice(Integer device) {
        this.device = device;
    }

    public Long getPvnum() {
        return pvnum;
    }

    public void setPvnum(Long pvnum) {
        this.pvnum = pvnum;
    }

    public Long getUvnum() {
        return uvnum;
    }

    public void setUvnum(Long uvnum) {
        this.uvnum = uvnum;
    }

    public Long getUrlnum() {
        return urlnum;
    }

    public Long getUvpvnum() {
        return uvpvnum;
    }

    public void setUvpvnum(Long uvpvnum) {
        this.uvpvnum = uvpvnum;
    }

    public Long getUvipnum() {
        return uvipnum;
    }

    public void setUvipnum(Long uvipnum) {
        this.uvipnum = uvipnum;
    }

    public Long getUvurlnum() {
        return uvurlnum;
    }

    public void setUvurlnum(Long uvurlnum) {
        this.uvurlnum = uvurlnum;
    }

    public void setUrlnum(Long urlnum) {
        this.urlnum = urlnum;
    }

    public String getDoubt() {
        return doubt;
    }

    public void setDoubt(String doubt) {
        this.doubt = doubt;
    }
}
