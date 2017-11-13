package io.union.admin.entity;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Administrator on 2017/4/8.
 */
@Entity
@Table(name = "sys_role")
public class SysRole implements Serializable {
    private Long roleId;
    private String roleName;
    private String roleAuths;
    private Long roleVersion;

    public SysRole() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false, unique = true)
    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    @Column(name = "role_name")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @Column(name = "role_auths")
    public String getRoleAuths() {
        return roleAuths;
    }

    public void setRoleAuths(String roleAuths) {
        this.roleAuths = roleAuths;
    }

    @Version
    @Column(name = "role_version")
    public Long getRoleVersion() {
        return roleVersion;
    }

    public void setRoleVersion(Long roleVersion) {
        this.roleVersion = roleVersion;
    }

    @Override
    public String toString() {
        return "SysRole{" +
                "roleId=" + roleId +
                ", roleName='" + roleName + '\'' +
                ", roleAuths='" + roleAuths + '\'' +
                ", roleVersion=" + roleVersion +
                '}';
    }
}
