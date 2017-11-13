package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/8.
 */
@Entity
@Table(name = "sys_authority")
public class SysAuthority implements Serializable {
    private Long authId;
    private String authName;
    private String authUrl;
    private Long authParent;
    private String authSort;
    private String authDescription;

    public SysAuthority() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id", unique = true, nullable = false)
    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    @Column(name = "auth_name")
    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String authName) {
        this.authName = authName;
    }

    @Column(name = "auth_url")
    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    @Column(name = "auth_parent")
    public Long getAuthParent() {
        return authParent;
    }

    public void setAuthParent(Long authParent) {
        this.authParent = authParent;
    }

    @Column(name = "auth_sort")
    public String getAuthSort() {
        return authSort;
    }

    public void setAuthSort(String authSort) {
        this.authSort = authSort;
    }

    @Column(name = "auth_description")
    public String getAuthDescription() {
        return authDescription;
    }

    public void setAuthDescription(String authDescription) {
        this.authDescription = authDescription;
    }

    @Override
    public String toString() {
        return "SysAuthority{" +
                "authId=" + authId +
                ", authName='" + authName + '\'' +
                ", authUrl='" + authUrl + '\'' +
                ", authParent=" + authParent +
                ", authSort='" + authSort + '\'' +
                ", authDescription='" + authDescription + '\'' +
                '}';
    }
}
