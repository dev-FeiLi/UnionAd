package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/8.
 */
@Entity
@Table(name = "sys_operate_log")
public class SysOperateLog implements Serializable {
    private Long optId;
    private Date optAddTime;
    private Long optManId;
    private String optAccount;
    private String optIp;
    private String optUrl;
    private String optTagValue;
    private Long optStart;
    private Long optEnd;
    private Long optSecond;

    public SysOperateLog() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "opt_id", unique = true, nullable = false)
    public Long getOptId() {
        return optId;
    }

    public void setOptId(Long optId) {
        this.optId = optId;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "opt_add_time")
    public Date getOptAddTime() {
        return optAddTime;
    }

    public void setOptAddTime(Date optAddTime) {
        this.optAddTime = optAddTime;
    }

    @Column(name = "opt_man_id")
    public Long getOptManId() {
        return optManId;
    }

    public void setOptManId(Long optManId) {
        this.optManId = optManId;
    }

    @Column(name = "opt_account")
    public String getOptAccount() {
        return optAccount;
    }

    public void setOptAccount(String optAccount) {
        this.optAccount = optAccount;
    }

    @Column(name = "opt_ip")
    public String getOptIp() {
        return optIp;
    }

    public void setOptIp(String optIp) {
        this.optIp = optIp;
    }

    @Column(name = "opt_url")
    public String getOptUrl() {
        return optUrl;
    }

    public void setOptUrl(String optUrl) {
        this.optUrl = optUrl;
    }

    @Column(name = "opt_tag_value")
    public String getOptTagValue() {
        return optTagValue;
    }

    public void setOptTagValue(String optTagValue) {
        this.optTagValue = optTagValue;
    }

    @Column(name = "opt_second")
    public Long getOptSecond() {
        return optSecond;
    }

    public void setOptSecond(Long optSecond) {
        this.optSecond = optSecond;
    }

    @Column(name = "opt_start")
    public Long getOptStart() {
        return optStart;
    }

    public void setOptStart(Long optStart) {
        this.optStart = optStart;
    }

    @Column(name = "opt_end")
    public Long getOptEnd() {
        return optEnd;
    }

    public void setOptEnd(Long optEnd) {
        this.optEnd = optEnd;
    }

    @Override
    public String toString() {
        return "SysOperateLog{" +
                "optId=" + optId +
                ", optAddTime=" + optAddTime +
                ", optManId=" + optManId +
                ", optAccount='" + optAccount + '\'' +
                ", optIp='" + optIp + '\'' +
                ", optUrl='" + optUrl + '\'' +
                ", optTagValue='" + optTagValue + '\'' +
                ", optStart=" + optStart +
                ", optEnd=" + optEnd +
                ", optSecond=" + optSecond +
                '}';
    }
}
