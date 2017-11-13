package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/8.
 */
@Entity
@Table(name = "sys_manage")
public class SysManage implements Serializable {
    private Long manId;
    private String manAccount;
    private String manPasswd;
    private String manName;
    private Date manAddTime;
    private Date manLoginTime;
    private String manLoginIp;
    private Date manLastTime;
    private String manLastIp;
    private String manStatus;
    private Long manRole;
    private Long manVersion;
    private String menuPos;

    public SysManage() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "man_id", unique = true, nullable = false)
    public Long getManId() {
        return manId;
    }

    public void setManId(Long manId) {
        this.manId = manId;
    }

    @Column(name = "man_account")
    public String getManAccount() {
        return manAccount;
    }

    public void setManAccount(String manAccount) {
        this.manAccount = manAccount;
    }

    @Column(name = "man_passwd")
    public String getManPasswd() {
        return manPasswd;
    }

    public void setManPasswd(String manPasswd) {
        this.manPasswd = manPasswd;
    }

    @Column(name = "man_name")
    public String getManName() {
        return manName;
    }

    public void setManName(String manName) {
        this.manName = manName;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "man_add_time")
    public Date getManAddTime() {
        return manAddTime;
    }

    public void setManAddTime(Date manAddTime) {
        this.manAddTime = manAddTime;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "man_login_time")
    public Date getManLoginTime() {
        return manLoginTime;
    }

    public void setManLoginTime(Date manLoginTime) {
        this.manLoginTime = manLoginTime;
    }

    @Column(name = "man_login_ip")
    public String getManLoginIp() {
        return manLoginIp;
    }

    public void setManLoginIp(String manLoginIp) {
        this.manLoginIp = manLoginIp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "man_last_time")
    public Date getManLastTime() {
        return manLastTime;
    }

    public void setManLastTime(Date manLastTime) {
        this.manLastTime = manLastTime;
    }

    @Column(name = "man_last_ip")
    public String getManLastIp() {
        return manLastIp;
    }

    public void setManLastIp(String manLastIp) {
        this.manLastIp = manLastIp;
    }

    @Column(name = "man_status")
    public String getManStatus() {
        return manStatus;
    }

    public void setManStatus(String manStatus) {
        this.manStatus = manStatus;
    }

    @Column(name = "man_role")
    public Long getManRole() {
        return manRole;
    }

    public void setManRole(Long manRole) {
        this.manRole = manRole;
    }

    @Version
    @Column(name = "man_version")
    public Long getManVersion() {
        return manVersion;
    }

    public void setManVersion(Long manVersion) {
        this.manVersion = manVersion;
    }

    @Column(name = "menu_pos")
    public String getMenuPos() {
        return menuPos;
    }

    public void setMenuPos(String menuPos) {
        this.menuPos = menuPos;
    }

    @Override
    public String toString() {
        return "SysManage{" +
                "manId=" + manId +
                ", manAccount='" + manAccount + '\'' +
                ", manPasswd='" + manPasswd + '\'' +
                ", manName='" + manName + '\'' +
                ", manAddTime=" + manAddTime +
                ", manLoginTime=" + manLoginTime +
                ", manLoginIp='" + manLoginIp + '\'' +
                ", manLastTime=" + manLastTime +
                ", manLastIp='" + manLastIp + '\'' +
                ", manStatus='" + manStatus + '\'' +
                ", manRole=" + manRole +
                ", manVersion=" + manVersion +
                ", menuPos='" + menuPos + '\'' +
                '}';
    }
}
